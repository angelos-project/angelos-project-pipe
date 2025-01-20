/**
 * Copyright (c) 2024 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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
package org.angproj.io.net

import jnr.constants.platform.AddressFamily
import jnr.constants.platform.Sock
import jnr.ffi.Platform
import jnr.ffi.Pointer
import jnr.ffi.Struct
import kotlinx.coroutines.sync.Mutex
import org.angproj.aux.util.BufferAware
import org.angproj.aux.util.swapEndian
import org.angproj.io.ffi.Native
import org.angproj.io.ffi.ServerSocket
import org.angproj.io.ffi.SocketAddressIncoming
import org.angproj.io.ffi.type.BSDSockAddr
import org.angproj.io.ffi.type.SockAddr


public class KQ(port: Int): Server, BufferAware {

    private val mutex = Mutex()
    private val connections: LinkedHashMap<Int, KQServerConnection> = linkedMapOf()

    private val pipeFd = intArrayOf(-1, -1)
    private var kQueueFd: Int = -1

    private lateinit var changeBuffer: Pointer
    private lateinit var eventBuffer: Pointer

    private val _port: Short = port.toShort().swapEndian()
    public override val port: Short
        get() = _port.swapEndian()

    private var _fileDescriptor: Int = -1
    public override val fd: Int
        get() = _fileDescriptor

    init {
        require(port in (0 until UShort.MAX_VALUE.toInt())) { "Port is not within standard range ($port)" }
    }

    public override suspend fun bindAndListen() {
        val fileDescriptor: Int = Native.libc.socket(
            AddressFamily.AF_INET.intValue(),
            Sock.SOCK_STREAM.intValue(),
            0
        )

        val sockAddr = when (Platform.getNativePlatform().isBSD) {
            true -> BSDSockAddr().apply {
                family = AddressFamily.AF_INET.intValue().toUByte()
                port = _port.toUShort()
            }
            else -> SockAddr().apply {
                family = AddressFamily.AF_INET.intValue().toUShort()
                port = _port.toUShort()
            }
        }

        check(Native.libc.bind(fileDescriptor, sockAddr.ptr, sockAddr.capcacity) >= 0) {
            "Bind on port $port failed " + ServerSocket.getLastErrorString()
        }

        check(Native.libc.listen(fileDescriptor, 5) >= 0) {
            "Listen on port $port failed " + ServerSocket.getLastErrorString()
        }

        _fileDescriptor = fileDescriptor

        ServerSocket.setBlocking(fd, false)
    }

    public suspend fun initializePipe() {
        Native.libc.pipe(pipeFd)

        kQueueFd = Native.libc.kqueue()
    }

    public companion object {

    }
}