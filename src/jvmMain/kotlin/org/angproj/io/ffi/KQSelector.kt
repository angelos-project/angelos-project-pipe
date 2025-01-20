/*
 * Copyright (C) 2008 Wayne Meissner
 *
 * This file is part of the JNR project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.angproj.io.ffi

import jnr.constants.platform.Errno
import jnr.ffi.Memory
import jnr.ffi.Pointer
import jnr.ffi.Runtime
import jnr.ffi.provider.jffi.NativeRuntime
import org.angproj.io.ffi.kq.EventIO
import org.angproj.io.ffi.kq.Descriptor
import org.angproj.io.ffi.type.Timespec
import java.io.IOException
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.spi.AbstractSelectableChannel
import java.nio.channels.spi.AbstractSelector
import java.util.Collections
import java.util.HashSet
import java.util.LinkedHashSet
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * An implementation of a [Selector] that uses the BSD (including MacOS)
 * kqueue(2) mechanism
 */
internal class KQSelector public constructor(provider: NativeSelectorProvider?) : AbstractSelector(provider) {
    private var kqfd = -1
    private val runtime: Runtime = NativeRuntime.getSystemRuntime()
    private val changebuf: Pointer
    private val eventbuf: Pointer
    private val io = EventIO.getInstance()
    private val pipefd = intArrayOf(-1, -1)
    private val regLock = Any()
    private val descriptors: MutableMap<Int?, Descriptor> = ConcurrentHashMap<Int?, Descriptor>()
    private val selected: MutableSet<SelectionKey?> = LinkedHashSet<SelectionKey?>()
    private val ZERO_TIMESPEC = Timespec(0, 0)

    init {
        changebuf = Memory.allocateDirect(runtime, MAX_EVENTS * io.size())
        eventbuf = Memory.allocateDirect(runtime, MAX_EVENTS * io.size())

        Native.libc.pipe(pipefd)

        kqfd = Native.libc.kqueue()
        io.put(changebuf, 0, pipefd[0], EVFILT_READ, EV_ADD)
        Native.libc.kevent(kqfd, changebuf, 1, null, 0, ZERO_TIMESPEC)
    }

    @Throws(IOException::class)
    override fun implCloseSelector() {
        if (kqfd != -1) {
            close(kqfd)
        }
        if (pipefd[0] != -1) {
            close(pipefd[0])
        }
        if (pipefd[1] != -1) {
            close(pipefd[1])
        }
        kqfd = -1
        pipefd[1] = kqfd
        pipefd[0] = pipefd[1]

        // deregister all keys
        for (entry in descriptors.entries) {
            for (k in entry.value.keys) {
                deregister(k)
            }
        }
    }

    override fun register(ch: AbstractSelectableChannel?, ops: Int, att: Any?): SelectionKey {
        val k = KQSelectionKey(this, (ch as NativeSelectableChannel?)!!, ops)
        synchronized(regLock) {
            val d = Descriptor(k.getFD())
            descriptors.put(k.getFD(), d)
            d.keys.add(k)
            handleChangedKey(d)
        }
        k.attach(att)
        return k
    }

    override fun keys(): MutableSet<SelectionKey?> {
        val keys: MutableSet<SelectionKey?> = HashSet<SelectionKey?>()
        for (fd in descriptors.values) {
            keys.addAll(fd.keys)
        }
        return Collections.unmodifiableSet<SelectionKey?>(keys)
    }

    override fun selectedKeys(): MutableSet<SelectionKey?> {
        return selected
    }

    @Throws(IOException::class)
    override fun selectNow(): Int {
        return poll(0)
    }

    @Throws(IOException::class)
    override fun select(timeout: Long): Int {
        return poll(timeout)
    }

    @Throws(IOException::class)
    override fun select(): Int {
        return poll(-1)
    }

    private fun poll(timeout: Long): Int {
        val nchanged = handleCancelledKeys()

        var ts: Timespec? = null
        if (timeout >= 0) {
            val sec = TimeUnit.MILLISECONDS.toSeconds(timeout)
            val nsec = TimeUnit.MILLISECONDS.toNanos(timeout % 1000)
            ts = Timespec(sec, nsec)
        }

        if (DEBUG) System.err.printf("nchanged=%d\n", nchanged)
        var nready = 0
        try {
            begin()
            do {
                nready = Native.libc.kevent(kqfd, changebuf, nchanged, eventbuf, MAX_EVENTS, ts)
            } while (nready < 0 && Errno.EINTR == Errno.valueOf(getRuntime().getLastError().toLong()))

            if (DEBUG) System.err.println("kevent returned " + nready + " events ready")
        } finally {
            end()
        }

        var updatedKeyCount = 0
        synchronized(regLock) {
            for (i in 0 until nready) {
                val fd = io.getFD(eventbuf, i)
                val d = descriptors.get(fd)

                if (d != null) {
                    val filt = io.getFilter(eventbuf, i)
                    if (DEBUG) System.err.printf("fd=%d filt=0x%x\n", d.fd, filt)
                    for (k in d.keys) {
                        val iops = k.interestOps()
                        var ops = 0

                        if (filt == EVFILT_READ) {
                            ops = ops or (iops and (SelectionKey.OP_ACCEPT or SelectionKey.OP_READ))
                        }
                        if (filt == EVFILT_WRITE) {
                            ops = ops or (iops and (SelectionKey.OP_CONNECT or SelectionKey.OP_WRITE))
                        }
                        ++updatedKeyCount
                        k.readyOps(ops)
                        if (!selected.contains(k)) {
                            selected.add(k)
                        }
                    }
                } else if (fd == pipefd[0]) {
                    if (DEBUG) System.err.println("Waking up")
                    wakeupReceived()
                }
            }
        }
        return updatedKeyCount
    }

    private fun handleCancelledKeys(): Int {
        val cancelled = cancelledKeys()
        synchronized(cancelled) {
            var nchanged = 0
            synchronized(regLock) {
                for (k in cancelled) {
                    val kqs = k as KQSelectionKey
                    deregister(kqs)
                    synchronized(selected) {
                        selected.remove(kqs)
                    }
                    val d = descriptors.get(kqs.getFD())
                    if (d != null) d.keys.remove(kqs)
                    if (d == null || d.keys.isEmpty()) {
                        io.put(changebuf, nchanged++, kqs.getFD(), EVFILT_READ, EV_DELETE)
                        io.put(changebuf, nchanged++, kqs.getFD(), EVFILT_WRITE, EV_DELETE)
                        descriptors.remove(kqs.getFD())
                    }
                    if (nchanged >= MAX_EVENTS) {
                        Native.libc.kevent(kqfd, changebuf, nchanged, null, 0, ZERO_TIMESPEC)
                        nchanged = 0
                    }
                }
            }
            cancelled.clear()
            return nchanged
        }
    }

    private fun handleChangedKey(changed: Descriptor) {
        synchronized(regLock) {
            var _nchanged = 0
            var writers = 0
            var readers = 0
            for (k in changed.keys) {
                if ((k.interestOps() and (SelectionKey.OP_ACCEPT or SelectionKey.OP_READ)) != 0) {
                    ++readers
                }
                if ((k.interestOps() and (SelectionKey.OP_CONNECT or SelectionKey.OP_WRITE)) != 0) {
                    ++writers
                }
            }
            for (filt in arrayOf<Int>(EVFILT_READ, EVFILT_WRITE)) {
                var flags = 0
                //
                // If no one is interested in events on the fd, disable it
                //
                if (filt == EVFILT_READ) {
                    if (readers > 0 && !changed.read) {
                        flags = EV_ADD or EV_ENABLE or EV_CLEAR
                        changed.read = true
                    } else if (readers == 0 && changed.read) {
                        flags = EV_DISABLE
                        changed.read = false
                    }
                }
                if (filt == EVFILT_WRITE) {
                    if (writers > 0 && !changed.write) {
                        flags = EV_ADD or EV_ENABLE or EV_CLEAR
                        changed.write = true
                    } else if (writers == 0 && changed.write) {
                        flags = EV_DISABLE
                        changed.write = false
                    }
                }
                if (DEBUG) System.err.printf(
                    "Updating fd %d filt=0x%x flags=0x%x\n",
                    changed.fd, filt, flags
                )
                if (flags != 0) {
                    io.put(changebuf, _nchanged++, changed.fd, filt, flags)
                }
            }
            Native.libc.kevent(kqfd, changebuf, _nchanged, null, 0, ZERO_TIMESPEC)
        }
    }

    private fun wakeupReceived() {
        Native.libc.read(pipefd[0], ByteArray(1), 1)
    }

    override fun wakeup(): Selector {
        Native.libc.write(pipefd[1], ByteArray(1), 1)
        return this
    }

    public fun interestOps(k: KQSelectionKey, ops: Int) {
        synchronized(regLock) {
            handleChangedKey(descriptors.get(k.getFD())!!)
        }
    }

    public companion object {
        private const val DEBUG = false
        private const val MAX_EVENTS = 100
        private val EVFILT_READ = -1
        private val EVFILT_WRITE = -2
        private const val EV_ADD = 0x0001
        private const val EV_DELETE = 0x0002
        private const val EV_ENABLE = 0x0004
        private const val EV_DISABLE = 0x0008
        private const val EV_CLEAR = 0x0020
    }
}
