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
import org.angproj.io.ffi.type.PollEventT


public class PollEvent internal constructor(
    bin: Binary, offset: Int, layout: NativeLayout<PollEvent>
) : NativeStruct(bin, offset, layout), PollEventT {

    override var fd: Int
        get() = bin.loadInt(0)
        set(value) { bin.saveInt(0, value) }

    override var event: Short
        get() = bin.loadShort(1)
        set(value) { bin.saveShort(1, value) }

    override var rEvent: Short
        get() = bin.loadShort(2)
        set(value) { bin.saveShort(2, value) }

    public companion object : NativeLayout<PollEvent>() {
        override val layout: Array<TypeSize> = arrayOf(
            TypeSize.INT, TypeSize.SHORT,TypeSize.SHORT
        )

        override fun create(
            bin: Binary, index: Int, layout: NativeLayout<PollEvent>
        ): PollEvent = PollEvent(bin, index, layout)
    }
}
