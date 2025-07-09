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
@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package org.angproj.io.net

import org.angproj.aux.util.TypePointer

public actual object NativeInterface {
    public actual fun strerror(error: Int): String {
        return "N/a"
    }

    public actual fun errno(): Int {
        return 0
    }

    public actual fun read(fd: Int, data: TypePointer, size: Int): Int {
        throw UnsupportedOperationException()
    }

    public actual fun write(fd: Int, data: TypePointer, size: Int): Int {
        throw UnsupportedOperationException()
    }

    public actual fun socket(domain: Int, type: Int, protocol: Int): Int {
        throw UnsupportedOperationException()
    }

    public actual fun connect(s: Int, name: TypePointer, namelen: Int): Int {
        throw UnsupportedOperationException()
    }

    public actual fun pipe(fds: IntArray): Int {
        throw UnsupportedOperationException()
    }

    public actual fun close(fd: Int): Int {
        throw UnsupportedOperationException()
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
        throw UnsupportedOperationException()
    }

    public actual fun fcntl(fd: Int, cmd: Int, data: Int): Int {
        throw UnsupportedOperationException()
    }

    public actual fun shutdown(s: Int, how: Int): Int {
        throw UnsupportedOperationException()
    }

    public actual fun getpeername(fd: Int, addr: TypePointer, len: Int): Int {
        throw UnsupportedOperationException()
    }
}
