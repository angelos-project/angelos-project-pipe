/**
 * Copyright (c) 2025 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
 *
 * This software is available under the terms of the MIT license. Parts are licensed
 * under different terms if stated. The legal terms are attached to the LICENSE file
 * and are made available on:
 *
 *      https://opensource.org/licenses/MIT
 *
 * SPDX-License-Identifier: MIT
 *
 * Contributors:
 *      Kristoffer Paulsson - initial implementation
 */
package org.angproj.io.sel

import kotlinx.coroutines.sync.withLock
import org.angproj.io.ffi.ArrayMode
import org.angproj.io.ffi.NativeArray
import org.angproj.io.ffi.NativeLayout
import org.angproj.io.ffi.impl.AbstractKQueueEvent
import org.angproj.io.ffi.impl.TimeSpec
import org.angproj.io.net.NetworkConnect
import org.angproj.io.pipe.Channel
import org.angproj.io.pipe.FileDescr
import org.angproj.io.sel.key.FileSelectionKey
import org.angproj.io.sel.key.NativeSelectionKey
import org.angproj.io.sel.key.SelectionKey
import kotlin.time.DurationUnit
import kotlin.time.toDuration


public class KQSelector<F: NativeLayout<E>, E: AbstractKQueueEvent>(
    factory: F
) : NativeSelector<F, E>(factory) {

    private var kqfd: FileDescr = FileDescr()
    private var changeBuf: NativeArray<E> = factory.allocateArray(MAX_EVENTS, ArrayMode.BUFFER)
    private var eventBuf: NativeArray<E> = factory.allocateArray(MAX_EVENTS, ArrayMode.BUFFER)
    private val zeroTimeSpec: TimeSpec = TimeSpec.allocate().also {
        it.tvSec = 0
        it.tvNSec = 0
    }

    init {
        kqfd = kqueue()

        val witness = changeBuf.allocateWitness()
        changeBuf.access(witness) {
            ident = semaphore.fileDescr.single.toLong()
            filter = EVFILT_READ
            flags = EV_ADD
        }
        changeBuf.flip()

        kevent(kqfd, changeBuf, zeroTimeSpec)
        changeBuf.clear()
    }

    override fun registerImpl(chan: Channel, subsFlags: Int): SelectionKey<E> {
        return FileSelectionKey(this, chan, nullEvent, subsFlags)
    }

    override suspend fun handleRegistered(): Unit = keyMutex.withLock {
        while (true) {
            try {
                val key = registered.tryReceive().getOrThrow() as NativeSelectionKey
                keys[key.fileDescr.single] = key

                memMutex.withLock {
                    val fd = key.fileDescr.single
                    val witness = changeBuf.allocateWitness()
                    changeBuf.access(witness) {
                        ident = fd.toLong()
                        filter = EVFILT_READ
                        flags = when (key.subsFlags and (SelectionKey.OP_ACCEPT or SelectionKey.OP_READ) != 0 ) {
                            true -> EV_ADD or EV_ENABLE or EV_CLEAR
                            else -> EV_DISABLE
                        }
                    }
                    changeBuf.access(witness) {
                        ident = fd.toLong()
                        filter = EVFILT_WRITE
                        flags = when (key.subsFlags and (SelectionKey.OP_CONNECT or SelectionKey.OP_WRITE) != 0 ) {
                            true -> EV_ADD or EV_ENABLE or EV_CLEAR
                            else -> EV_DISABLE
                        }
                    }
                    changeBuf.flip()
                    kevent(kqfd, changeBuf, zeroTimeSpec)
                    changeBuf.clear()
                }
            } finally {
                break
            }
        }
    }

    // ?
    override fun handleChanged(key: SelectionKey<E>) {
        val fd = (key as NativeSelectionKey).fileDescr.single
        val witness = changeBuf.allocateWitness()
        changeBuf.access(witness) {
            ident = fd.toLong()
            filter = EVFILT_READ
            flags = when (key.subsFlags and (SelectionKey.OP_ACCEPT or SelectionKey.OP_READ) != 0 ) {
                true -> EV_ADD or EV_ENABLE or EV_CLEAR
                else -> EV_DISABLE
            }
        }
        changeBuf.access(witness) {
            ident = fd.toLong()
            filter = EVFILT_WRITE
            flags = when (key.subsFlags and (SelectionKey.OP_CONNECT or SelectionKey.OP_WRITE) != 0 ) {
                true -> EV_ADD or EV_ENABLE or EV_CLEAR
                else -> EV_DISABLE
            }
        }
        changeBuf.flip()
        kevent(kqfd, changeBuf, zeroTimeSpec)
        changeBuf.clear()
    }

    override suspend fun handleCancelled(): Unit = keyMutex.withLock {
        memMutex.withLock {
            val witness = changeBuf.allocateWitness()
            while (true) {
                try {
                    val key = cancelled.tryReceive().getOrThrow()
                    val fd = key.chan.id
                    unregister(key)

                    changeBuf.access(witness) {
                        ident = fd.toLong()
                        filter = EVFILT_READ
                        flags = EV_DELETE
                    }
                    changeBuf.access(witness) {
                        ident = fd.toLong()
                        filter = EVFILT_WRITE
                        flags = EV_DELETE
                    }

                    if (!changeBuf.hasRemaining()) {
                        changeBuf.flip()
                        kevent(kqfd, changeBuf, zeroTimeSpec)
                        changeBuf.clear()
                    }

                } finally {
                    break
                }
            }
        }
    }

    override suspend fun eventLoop(timeout: Long): Int {
        var ready = 0
        memMutex.withLock {
            val ts = if(timeout >= 0) TimeSpec.allocate().apply {
                tvSec = timeout.toDuration(DurationUnit.MILLISECONDS).inWholeSeconds
                tvNSec = (timeout % 1000).toDuration(DurationUnit.MILLISECONDS).inWholeNanoseconds
            } else zeroTimeSpec

            eventBuf.clear()
            try {
                ready = repeat { kevent(kqfd, changeBuf, eventBuf, ts) }
            } finally {

            }

            if(ts != zeroTimeSpec) ts.close()
        }

        var keyCount = 0
        val fdFilter = mutableMapOf<Int, Int>()
        val fdOrder = mutableListOf<Int>()
        fullMutex {
            eventBuf.positionAt(ready)
            eventBuf.flip()
            val witness = eventBuf.allocateWitness()
            while (eventBuf.hasRemaining()) {
                eventBuf.access(witness) {
                    val fd = ident.toInt()
                    if(!fdFilter.contains(fd)) {
                        fdFilter[fd] = 0
                        fdOrder.add(fd)
                    }
                    val key = keys[fd] ?: error("Invalid selection key")
                    val ops: Int = when(filter) {
                        EVFILT_READ -> (key.subsFlags and (SelectionKey.OP_ACCEPT or SelectionKey.OP_READ))
                        EVFILT_WRITE -> (key.subsFlags and (SelectionKey.OP_CONNECT or SelectionKey.OP_WRITE))
                        else -> 0
                    }
                    fdFilter[fd] = fdFilter[fd]!! or ops
                }
            }
            fdOrder.forEach { fd ->
                if(fd == semaphore.fileDescr.single) {
                    wakeUpReceived()
                } else {
                    keyCount++
                    fdFilter.remove(fd)!!.also { ops -> keys[fd]!!.wakeUp(ops) }
                }
                fdOrder.remove(fd)
            }
        }
        fdOrder.clear()
        fdFilter.clear()
        return keyCount
    }

    override fun close() {
        super.close()
        changeBuf.clear()
        changeBuf.close()

        eventBuf.clear()
        eventBuf.close()

        zeroTimeSpec.close()
        nullEvent.close()
    }

    public companion object: NetworkConnect() {
        public const val MAX_EVENTS: Int = 128

        public const val EVFILT_READ: Short = -1
        public const val EVFILT_WRITE: Short = -2

        public const val EV_ADD: UShort = 0x0001u
        public const val EV_DELETE: UShort = 0x0002u
        public const val EV_ENABLE: UShort = 0x0004u
        public const val EV_DISABLE: UShort = 0x0008u
        public const val EV_CLEAR: UShort = 0x0020u
    }
}

