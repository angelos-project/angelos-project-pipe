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

import kotlinx.coroutines.yield
import org.angproj.aux.util.TypePointer


public abstract class NetworkDriver {

    protected val rwErr: Set<Errno> = setOf(Errno.EAGAIN, Errno.EWOULDBLOCK)

    protected fun isError(errno: Errno): Boolean = rwErr.contains(errno)

    protected fun getLastErrorString(): String {
        return NativeInterface.strerror(NativeInterface.errno())
    }

    protected fun getLastError(): Errno {
        return Errno.mapCode(NativeInterface.errno())
    }

    protected fun repeat(block: () -> Int): Int {
        var n: Int
        do {
            n = block()
        } while(n < 0 && getLastError() == Errno.EINTR)
        return n
    }

    protected suspend fun until(nonblocking: suspend () -> Int): Int {
        var n: Int
        do {
            yield()
            n = nonblocking()
            val lastErr = getLastError()
        } while(n < 0 && (lastErr == Errno.EAGAIN || lastErr == Errno.EAGAIN))
        return n
    }

    protected fun read(
        fd: Int, dataPtr: TypePointer, len: Int
    ): Int = NativeInterface.read(fd, dataPtr, len.toLong())

    protected fun write(
        fd: Int, dataPtr: TypePointer, len: Int
    ): Int = NativeInterface.write(fd, dataPtr, len.toLong())
}