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

import kotlinx.cinterop.*
import org.angproj.aux.util.TypePointer
import platform.posix.strerror as posix_strerror
import platform.posix.errno as posix_errno
import platform.posix.read as posix_read
import platform.posix.write as posix_write
import platform.posix.socket as posix_socket
import platform.posix.connect as posix_connect
import platform.posix.pipe as posix_pipe
import platform.posix.close as posix_close
import platform.posix.poll as posix_poll
import platform.posix.fcntl as posix_fcntl
import platform.posix.shutdown as posix_shutdown


@OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
public actual object NativeInterface {
    public actual fun strerror(error: Int): String {
        return posix_strerror(error)?.toKStringFromUtf8() ?: ""
    }

    public actual fun errno(): Int {
        return posix_errno
    }

    public actual fun read(fd: Int, data: TypePointer, size: Long): Int {
        return posix_read(fd, data.toPointer().toCPointer<CArrayPointerVar<ByteVar>>(), size.convert())
    }

    public actual fun write(fd: Int, data: TypePointer, size: Long): Int {
        return posix_write(fd, data.toPointer().toCPointer<CArrayPointerVar<ByteVar>>(), size.convert())
    }

    public actual fun socket(domain: Int, type: Int, protocol: Int): Int {
        return posix_socket(domain, type, protocol)
    }

    public actual fun connect(s: Int, name: TypePointer, namelen: Int): Int {
        return posix_connect(s, name.toPointer().toCPointer(), namelen.convert())
    }

    public actual fun pipe(fds: IntArray): Int {
        return posix_pipe(fds.toCValues())
    }

    public actual fun close(fd: Int): Int {
        return posix_close(fd)
    }

    public actual fun kqueue(): Int {
        throw UnsupportedOperationException()
    }

    public actual fun kevent(
        kq: Int,
        changebuf: TypePointer,
        nchanges: Int,
        eventbuf: TypePointer,
        nevents: Int,
        timeout: TypePointer
    ): Int {
        throw UnsupportedOperationException()
    }

    public actual fun poll(pfds: TypePointer, nfds: Int, timeout: Int): Int {
        return posix_poll(pfds.toPointer().toCPointer(), nfds.convert(), timeout)
    }

    public actual fun fcntl(fd: Int, cmd: Int, data: Int): Int {
        return posix_fcntl(fd.convert(), cmd.convert(), data)
    }

    public actual fun shutdown(s: Int, how: Int): Int {
        return posix_shutdown(s, how)
    }
}