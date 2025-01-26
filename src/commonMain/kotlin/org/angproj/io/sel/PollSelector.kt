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
import org.angproj.io.ffi.impl.PollEvent
import org.angproj.io.net.NetworkConnect
import org.angproj.io.pipe.Channel
import org.angproj.io.sel.key.FileSelectionKey
import org.angproj.io.sel.key.NativeSelectionKey
import org.angproj.io.sel.key.SelectionKey


public class PollSelector(
    factory: NativeLayout<PollEvent>
) : NativeSelector<NativeLayout<PollEvent>, PollEvent>(factory) {

    private val pollData: NativeArray<PollEvent> = factory.allocateArray(10000, ArrayMode.INCREMENT)
    private val pipePoll: PollEvent = pollData.allocate().apply {
        fd = semaphore.fileDescr.single
        event = POLL_IN
    }

    override fun registerImpl(chan: Channel, subsFlags: Int): SelectionKey<PollEvent> {
        return FileSelectionKey(this, chan, pollData.allocate(), subsFlags)
    }

    override suspend fun handleRegistered(): Unit = keyMutex.withLock {
        while (true) {
            try {
                val key = registered.tryReceive().getOrThrow() as NativeSelectionKey
                keys[key.fileDescr.single] = key

                memMutex.withLock {
                    key.event.apply {
                        var events: Int = 0
                        if ((key.subsFlags and (SelectionKey.OP_ACCEPT or SelectionKey.OP_READ)) != 0)
                            events = (events or POLL_IN.toInt())
                        if ((key.subsFlags and (SelectionKey.OP_WRITE or SelectionKey.OP_CONNECT)) != 0)
                            events = (events or POLL_OUT.toInt())

                        event = events.toShort()
                        fd = key.fileDescr.single
                    }
                }
            } finally {
                break
            }
        }
    }

    // ?
    override fun handleChanged(key: SelectionKey<PollEvent>) {
        key.event.apply {
            var events: Int = 0
            if ((key.subsFlags and (SelectionKey.OP_ACCEPT or SelectionKey.OP_READ)) != 0)
                events = (events or POLL_IN.toInt())
            if ((key.subsFlags and (SelectionKey.OP_WRITE or SelectionKey.OP_CONNECT)) != 0)
                events = (events or POLL_OUT.toInt())

            event = events.toShort()
        }
    }

    override suspend fun handleCancelled(): Unit = keyMutex.withLock {
        while (true) {
            try {
                val key = cancelled.tryReceive().getOrThrow()
                unregister(key)

                memMutex.withLock {
                    key.event.apply {
                        fd = -1
                        event = 0
                    }

                    pollData.recycle(key.event)
                }
            } finally {
                break
            }
        }
    }

    override suspend fun eventLoop(timeout: Long): Int {
        var nready: Int
        memMutex.withLock {
            try {
                nready = repeat { poll(pollData, timeout.toInt()) }
            } finally {
            }
        }

        if (nready < 1) return nready

        if ((pipePoll.rEvent.toInt() and POLL_IN.toInt()) != 0) wakeUpReceived()

        return fullMutex {
            var keyCount = 0
            keys.forEach { pair ->
                val pk = pair.value
                val pe = pk.event

                if (pe.rEvent.toInt() != 0) {
                    val rEvent = pe.rEvent
                    pe.rEvent = 0

                    var ops: Int = 0
                    if ((rEvent.toInt() and POLL_IN.toInt()) != 0)
                        ops = ops or (pk.subsFlags and (SelectionKey.OP_ACCEPT or SelectionKey.OP_READ))
                    if ((rEvent.toInt() and POLL_OUT.toInt()) != 0)
                        ops = ops or (pk.subsFlags and (SelectionKey.OP_CONNECT or SelectionKey.OP_WRITE))

                    if ((rEvent.toInt() and (POLL_HUP.toInt() or POLL_ERR.toInt())) != 0)
                        ops = pk.subsFlags

                    pk.wakeUp(ops)
                    keyCount++
                }
            }
            keyCount
        }
    }

    override fun close() {
        super.close()
        pipePoll.close()
        pollData.close()
        nullEvent.close()
    }

    public companion object : NetworkConnect() {
        public const val POLL_IN: Short = 0x1
        public const val POLL_OUT: Short = 0x4
        public const val POLL_ERR: Short = 0x8
        public const val POLL_HUP: Short = 0x10
    }
}