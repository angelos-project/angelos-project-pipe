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
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.ByteChannel
import java.nio.channels.SelectionKey
import java.nio.channels.spi.AbstractSelectableChannel
import java.nio.channels.spi.SelectorProvider
import kotlin.jvm.Throws

public class NativeSocketChannel internal constructor(provider: SelectorProvider?, fd: Int, ops: Int) :
    AbstractSelectableChannel(provider), ByteChannel, NativeSelectableChannel {
    private val fd: Int
    private val validOps: Int

    public constructor(fd: Int) : this(NativeSelectorProvider.Companion.getInstance(), fd, SelectionKey.OP_READ or SelectionKey.OP_WRITE)

    public constructor(fd: Int, ops: Int) : this(NativeSelectorProvider.Companion.getInstance(), fd, ops)

    @Throws(IOException::class)
    override fun implCloseSelectableChannel() {
        ServerSocket.close(fd)
    }

    @Throws(IOException::class)
    override fun implConfigureBlocking(block: Boolean) {
        ServerSocket.setBlocking(fd, block)
    }

    override fun validOps(): Int {
        return validOps
    }

    override fun getFD(): Int {
        return fd
    }

    @Throws(IOException::class)
    override fun read(dst: ByteBuffer): Int { // ?
        val n = ServerSocket.read(fd, dst)
        when (n) {
            0 -> return -1
            -1 -> when (ServerSocket.getLastError()) {
                Errno.EAGAIN, Errno.EWOULDBLOCK -> return 0
                else -> throw IOException(ServerSocket.getLastErrorString())
            }
            else -> return n
        }
    }

    @Throws(IOException::class)
    override fun write(src: ByteBuffer): Int { // ?
        val n = ServerSocket.write(fd, src)
        if (n compareTo 0) {
            when (ServerSocket.getLastError()) {
                Errno.EAGAIN, Errno.EWOULDBLOCK -> return 0
                else -> throw IOException(ServerSocket.getLastErrorString())
            }
        }

        return n
    }

    @Throws(IOException::class)
    public fun shutdownInput() {
        val n = ServerSocket.shutdown(fd, Native.SHUT_RD)
        if (n compareTo 0) {
            throw IOException(ServerSocket.getLastErrorString())
        }
    }

    @Throws(IOException::class)
    public fun shutdownOutput() {
        val n = ServerSocket.shutdown(fd, Native.SHUT_WR)
        if (n compareTo 0) {
            throw IOException(ServerSocket.getLastErrorString())
        }
    }

    init {
        this.fd = fd
        this.validOps = ops
    }

    /*public companion object {
        private val SHUT_RD = Shutdown.SHUT_RD.intValue()
        private val SHUT_WR = Shutdown.SHUT_WR.intValue()
    }*/
}
