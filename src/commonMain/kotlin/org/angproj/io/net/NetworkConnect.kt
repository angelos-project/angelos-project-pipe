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
package org.angproj.io.net

import org.angproj.aux.util.NullObject
import org.angproj.aux.util.TypePointer
import org.angproj.io.ffi.NativeArray
import org.angproj.io.ffi.impl.AbstractKQueueEvent
import org.angproj.io.ffi.impl.PollEvent
import org.angproj.io.ffi.impl.SockAddrUnix
import org.angproj.io.ffi.impl.TimeSpec
import org.angproj.io.ffi.ptr
import org.angproj.io.fs.OpenFlag
import org.angproj.io.fs.StatusFlag
import org.angproj.io.pipe.FileDescr
import org.angproj.io.sel.PipePair


public abstract class NetworkConnect : NetworkDriver() {

    protected fun socket(domain: ProtocolFamily, type: SockType, protocol: Int): FileDescr {
        val fd = NativeInterface.socket(domain.toCode(), type.toCode(), protocol)
        if (fd < 0) throw NetworkException(getLastErrorString())
        return FileDescr(fd)
    }

    protected fun connect(sock: FileDescr, addr: SockAddrUnix): Boolean {
        val n = NativeInterface.connect(sock.single, addr.ptr, addr.limit)
        return if (n == 0) true else when(getLastError()) {
            Errno.EAGAIN, Errno.EWOULDBLOCK -> false
            else -> throw NetworkException(getLastErrorString())
        }
    }

    protected fun getpeername(fd: FileDescr, addr: SockAddrUnix): Boolean {
        val n = NativeInterface.getpeername(fd.single, addr.ptr, addr.capacity)
        return when(n < 0) {
            true -> throw NetworkException(getLastErrorString())
            else -> {
                addr.newLimit(n)
                true
            }
        }
    }

    protected fun pipe(pipeFd: PipePair): Int = NativeInterface.pipe(pipeFd.pair)

    protected fun close(fd: Int): Int = NativeInterface.close(fd)

    protected fun kqueue(): FileDescr = FileDescr(NativeInterface.kqueue())

    protected fun <E: AbstractKQueueEvent>kevent(
        kqfd: FileDescr,
        changeBuf: NativeArray<E>,
        timeOut: TimeSpec
    ): Int {
        return NativeInterface.kevent(
            kqfd.single,
            changeBuf.ptr,
            changeBuf.limit,
            NullObject.ptr,
            0,
            timeOut.ptr
        )
    }

    protected fun <E: AbstractKQueueEvent>kevent(
        kqfd: FileDescr,
        changeBuf: NativeArray<E>,
        eventBuf: NativeArray<E>,
        timeOut: TimeSpec
    ): Int {
        return NativeInterface.kevent(
            kqfd.single,
            changeBuf.ptr,
            changeBuf.limit,
            eventBuf.ptr,
            eventBuf.limit,
            timeOut.ptr
        )
    }

    protected fun poll(pfds: NativeArray<PollEvent>, timeout: Int): Int {
        return NativeInterface.poll(pfds.ptr, pfds.limit, timeout)
    }

    protected fun fcntl(fd: FileDescr, cmd: StatusFlag, data: Int): Int {
        return NativeInterface.fcntl(fd.single, cmd.toCode(), data)
    }

    protected fun nonBlocking(fileDescr: FileDescr) {
        var flags: Int = fcntl(fileDescr, StatusFlag.GETFL, 0)
        flags = flags or OpenFlag.NONBLOCK.toCode()
        fcntl(fileDescr, StatusFlag.SETFL, flags)
    }
}