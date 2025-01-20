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

package org.angproj.io.fs

import jnr.ffi.LastError
import jnr.ffi.Library
import jnr.ffi.Platform
import jnr.ffi.Runtime

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
}