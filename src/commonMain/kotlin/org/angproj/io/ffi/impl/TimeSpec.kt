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
package org.angproj.io.ffi.impl

import org.angproj.aux.io.Binary
import org.angproj.aux.io.TypeSize
import org.angproj.io.ffi.NativeLayout
import org.angproj.io.ffi.NativeStruct
import org.angproj.io.ffi.type.TimeSpecT


public class TimeSpec internal constructor(
    bin: Binary, offset: Int, layout: NativeLayout<TimeSpec>
) : NativeStruct(bin, offset, layout), TimeSpecT {

    override var tvSec: Long
        get() = bin.loadLong(0)
        set(value) { bin.saveLong(0, value) }

    override var tvNSec: Long
        get() = bin.loadLong(1)
        set(value) { bin.saveLong(1, value) }

    public companion object : NativeLayout<TimeSpec>() {
        override val layout: Array<TypeSize> = arrayOf(
            TypeSize.LONG, TypeSize.LONG
        )

        override fun create(
            bin: Binary, index: Int, layout: NativeLayout<TimeSpec>
        ): TimeSpec = TimeSpec(bin, index, layout)
    }
}

/**
 *             public final SignedLong tv_sec = new SignedLong();
 *             public final SignedLong tv_nsec = new SignedLong();
 * */