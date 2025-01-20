/**
 * Copyright (c) 2024-2025 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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
package org.angproj.io.ffi.type

import jnr.ffi.Runtime
import org.angproj.io.ffi.Native
import org.angproj.io.ffi.NativeStruct

public class Timespec(runtime: Runtime) : NativeStruct(runtime, 16) {
    public var tv_sec: Long
        get() = bin.retrieveLong(0)
        set(value) { bin.storeLong(8, value) }

    public var tv_nsec: Long
        get() = bin.retrieveLong(8)
        set(value) { bin.storeLong(8, value) }

    public constructor() : this(Native.runtime)

    public constructor(sec: Long, nsec: Long) : this(Native.runtime) {
        tv_sec = sec
        tv_nsec = nsec
    }
}