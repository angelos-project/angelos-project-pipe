/*
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
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.spi.AbstractSelectableChannel
import java.nio.channels.spi.AbstractSelector
import java.nio.channels.spi.SelectorProvider
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * An implementation of a [Selector] that uses good old
 * poll(2)
 */
internal class PollSelector public constructor(provider: SelectorProvider?) : AbstractSelector(provider) {
    private var keyArray: Array<PollSelectionKey> = arrayOfNulls<PollSelectionKey>(0)
    private var pollData: ByteBuffer? = null
    private var nfds: Int

    private val pipefd = intArrayOf(-1, -1)
    private val regLock = Any()

    private val keys: MutableMap<SelectionKey?, Boolean?> = ConcurrentHashMap<SelectionKey?, Boolean?>()
    private val selected: MutableSet<SelectionKey?> = HashSet<SelectionKey?>()


    init {
        Native.libc.pipe(pipefd)
        // Register the wakeup pipe as the first element in the pollfd array
        pollData = ByteBuffer.allocateDirect(8).order(ByteOrder.nativeOrder())
        putPollFD(0, pipefd[0])
        putPollEvents(0, POLLIN)
        nfds = 1
        keyArray = arrayOfNulls<PollSelectionKey>(1)
    }

    private fun putPollFD(idx: Int, fd: Int) {
        pollData!!.putInt((idx * POLLFD_SIZE) + FD_OFFSET, fd)
    }

    private fun putPollEvents(idx: Int, events: Int) {
        pollData!!.putShort((idx * POLLFD_SIZE) + EVENTS_OFFSET, events.toShort())
    }

    private fun getPollFD(idx: Int): Int {
        return pollData!!.getInt((idx * POLLFD_SIZE) + FD_OFFSET)
    }

    private fun getPollEvents(idx: Int): Short {
        return pollData!!.getShort((idx * POLLFD_SIZE) + EVENTS_OFFSET)
    }

    private fun getPollRevents(idx: Int): Short {
        return pollData!!.getShort((idx * POLLFD_SIZE) + REVENTS_OFFSET)
    }

    private fun putPollRevents(idx: Int, events: Int) {
        pollData!!.putShort((idx * POLLFD_SIZE) + REVENTS_OFFSET, events.toShort())
    }

    @Throws(IOException::class)
    override fun implCloseSelector() {
        if (pipefd[0] != -1) {
            ServerSocket.close(pipefd[0])
        }
        if (pipefd[1] != -1) {
            ServerSocket.close(pipefd[1])
        }

        // remove all keys
        for (key in keys.keys) {
            remove((key as PollSelectionKey?)!!)
        }
    }

    override fun register(ch: AbstractSelectableChannel?, ops: Int, att: Any?): SelectionKey {
        val key = PollSelectionKey(this, (ch as NativeSelectableChannel?)!!)
        add(key)
        key.attach(att)
        key.interestOps(ops)
        return key
    }

    override fun keys(): MutableSet<SelectionKey?> {
        return HashSet<SelectionKey?>(Arrays.asList<PollSelectionKey?>(*keyArray).subList(1, nfds))
    }

    override fun selectedKeys(): MutableSet<SelectionKey?> {
        return selected
    }


    public fun interestOps(k: PollSelectionKey, ops: Int) {
        var events: Short = 0
        if ((ops and (SelectionKey.OP_ACCEPT or SelectionKey.OP_READ)) != 0) {
            events = events.toInt() or POLLIN
        }
        if ((ops and (SelectionKey.OP_WRITE or SelectionKey.OP_CONNECT)) != 0) {
            events = events.toInt() or POLLOUT
        }
        putPollEvents(k.getIndex(), events.toInt())
    }


    private fun add(k: PollSelectionKey) {
        synchronized(regLock) {
            ++nfds
            if (keyArray.size < nfds) {
                val newArray: Array<PollSelectionKey> = arrayOfNulls<PollSelectionKey>(nfds + (nfds / 2))
                System.arraycopy(keyArray, 0, newArray, 0, nfds - 1)
                keyArray = newArray
                val newBuffer = ByteBuffer.allocateDirect(newArray.size * 8)
                if (pollData != null) {
                    newBuffer.put(pollData)
                }
                newBuffer.position(0)
                pollData = newBuffer.order(ByteOrder.nativeOrder())
            }
            k.setIndex(nfds - 1)
            keyArray[nfds - 1] = k
            putPollFD(k.getIndex(), k.getFD())
            putPollEvents(k.getIndex(), 0)
            keys.put(k, true)
        }
    }


    private fun remove(k: PollSelectionKey) {
        val idx = k.getIndex()
        synchronized(regLock) {
            //
            // If not the last key, swap last one into the removed key's position
            //
            if (idx < (nfds - 1)) {
                val last = keyArray[nfds - 1]
                keyArray[idx] = last
                // Copy the data for the last key into place
                putPollFD(idx, getPollFD(last.getIndex()))
                putPollEvents(idx, getPollEvents(last.getIndex()).toInt())
                last.setIndex(idx)
            } else {
                putPollFD(idx, -1)
                putPollEvents(idx, 0)
            }
            keyArray[nfds - 1] = null
            --nfds
            synchronized(selected) {
                selected.remove(k)
            }
            keys.remove(k)
        }
        deregister(k)
    }


    @Throws(IOException::class)
    override fun selectNow(): Int {
        return poll(0)
    }


    @Throws(IOException::class)
    override fun select(timeout: Long): Int {
        return poll(if (timeout > 0) timeout else -1)
    }


    @Throws(IOException::class)
    override fun select(): Int {
        return poll(-1)
    }


    @Throws(IOException::class)
    private fun poll(timeout: Long): Int {
        //
        // Remove any cancelled keys
        //
        val cancelled = cancelledKeys()
        synchronized(cancelled) {
            for (k in cancelled) {
                remove((k as PollSelectionKey?)!!)
            }
            cancelled.clear()
        }

        var nready = 0
        try {
            begin()

            do {
                nready = Native.libc.poll(pollData, nfds, timeout.toInt())
            } while (nready < 0 && Errno.EINTR == Errno.valueOf(Native.runtime.getLastError().toLong()))
        } finally {
            end()
        }

        if (nready < 1) {
            return nready
        }

        if ((getPollRevents(0).toInt() and POLLIN) != 0) {
            wakeupReceived()
        }

        var updatedKeyCount = 0
        for (k in keys.keys) {
            val pk = k as PollSelectionKey
            val revents = getPollRevents(pk.getIndex()).toInt()
            if (revents != 0) {
                putPollRevents(pk.getIndex(), 0)
                val iops = k.interestOps()
                var ops = 0

                if ((revents and POLLIN) != 0) {
                    ops = ops or (iops and (SelectionKey.OP_ACCEPT or SelectionKey.OP_READ))
                }

                if ((revents and POLLOUT) != 0) {
                    ops = ops or (iops and (SelectionKey.OP_CONNECT or SelectionKey.OP_WRITE))
                }

                // If an error occurred, enable all interested ops and let the
                // event handling code deal with it
                if ((revents and (POLLHUP or POLLERR)) != 0) {
                    ops = iops
                }

                k.readyOps(ops)
                ++updatedKeyCount
                if (!selected.contains(k)) {
                    selected.add(k)
                }
            }
        }

        return updatedKeyCount
    }

    @Throws(IOException::class)
    private fun wakeupReceived() {
        ServerSocket.read(pipefd[0], ByteBuffer.allocate(1))
    }

    override fun wakeup(): Selector {
        try {
            ServerSocket.write(pipefd[1], ByteBuffer.allocate(1))
        } catch (ioe: IOException) {
            throw RuntimeException(ioe)
        }

        return this
    }

    public companion object {
        private const val POLLFD_SIZE = 8
        private const val FD_OFFSET = 0
        private const val EVENTS_OFFSET = 4
        private const val REVENTS_OFFSET = 6

        public const val POLLIN: Int = 0x1
        public const val POLLOUT: Int = 0x4
        public const val POLLERR: Int = 0x8
        public const val POLLHUP: Int = 0x10
    }
}
