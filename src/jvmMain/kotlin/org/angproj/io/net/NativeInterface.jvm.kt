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

import jnr.ffi.LastError
import jnr.ffi.Library
import jnr.ffi.Platform
import jnr.ffi.Runtime
import org.angproj.aux.util.TypePointer

public actual object NativeInterface {
    @JvmStatic
    private val libnames: Array<String?> by lazy {
        if (Platform.getNativePlatform().getOS() == Platform.OS.SOLARIS) arrayOf("socket", "nsl", "c")
        else arrayOf(Platform.getNativePlatform().getStandardCLibraryName())
    }

    @JvmStatic
    private val libc: LibC = Library.loadLibrary(LibC::class.java, *libnames)

    @JvmStatic
    private val runtime: Runtime = Runtime.getSystemRuntime()

    public actual fun strerror(error: Int): String {
        return libc.strerror(error) ?: ""
    }

    public actual fun errno(): Int {
        return LastError.getLastError(runtime)
    }

    public actual fun read(fd: Int, data: TypePointer, size: Long): Int {
        return libc.read(fd, data.toPointer(), size)
    }

    public actual fun write(fd: Int, data: TypePointer, size: Long): Int {
        return libc.write(fd, data.toPointer(), size)
    }

    public actual fun socket(domain: Int, type: Int, protocol: Int): Int {
        return libc.socket(domain, type, protocol)
    }

    public actual fun connect(s: Int, name: TypePointer, namelen: Int): Int {
        return libc.connect(s, name.toPointer(), namelen)
    }

    public actual fun pipe(fds: IntArray): Int {
        return libc.pipe(fds)
    }

    public actual fun close(fd: Int): Int {
        return libc.close(fd)
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
        return libc.kevent(
            kq,
            changebuf.toPointer(),
            nchanges,
            eventbuf.toPointer(),
            nevents,
            timeout.toPointer()
        )
    }
}