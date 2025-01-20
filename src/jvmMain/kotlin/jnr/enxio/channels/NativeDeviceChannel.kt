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
package jnr.enxio.channels

import jnr.constants.platform.Errno
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.ByteChannel
import java.nio.channels.SelectionKey
import java.nio.channels.spi.AbstractSelectableChannel
import java.nio.channels.spi.SelectorProvider


public class NativeDeviceChannel public constructor(provider: SelectorProvider?, fd: Int, ops: Int, isFile: Boolean) :
    AbstractSelectableChannel(provider), ByteChannel, NativeSelectableChannel {
    private val fd: Int
    private val validOps: Int
    private val isFile: Boolean

    @JvmOverloads
    public constructor(fd: Int, isFile: Boolean = false) : this(
        selectorProvider(isFile),
        fd,
        SelectionKey.OP_READ or SelectionKey.OP_WRITE,
        isFile
    )

    init {
        this.fd = fd
        this.validOps = ops
        this.isFile = isFile
    }

    @Throws(IOException::class)
    override fun implCloseSelectableChannel() {
        val n = Native.close(fd)
        if (n < 0) {
            throw IOException(Native.getLastErrorString())
        }
    }

    @Throws(IOException::class)
    override fun implConfigureBlocking(block: Boolean) {
        Native.setBlocking(fd, block)
    }

    override fun validOps(): Int {
        return validOps
    }

    override fun getFD(): Int {
        return fd
    }

    @Throws(IOException::class)
    override fun read(dst: ByteBuffer): Int { // ?
        val n = Native.read(fd, dst)
        when (n) {
            0 -> return -1

            -1 -> when (Native.getLastError()) {
                Errno.EAGAIN, Errno.EWOULDBLOCK -> return 0

                else -> throw IOException(Native.getLastErrorString())
            }

            else -> return n
        }
    }

    @Throws(IOException::class)
    override fun write(src: ByteBuffer): Int { // ?
        val n = Native.write(fd, src)
        if (n < 0) {
            throw IOException(Native.getLastErrorString())
        }

        return n
    }

    public companion object {
        private fun selectorProvider(isFile: Boolean): SelectorProvider? {
            return if ((isFile)) NativeFileSelectorProvider.getInstance() else NativeSelectorProvider.getInstance()
        }
    }
}
