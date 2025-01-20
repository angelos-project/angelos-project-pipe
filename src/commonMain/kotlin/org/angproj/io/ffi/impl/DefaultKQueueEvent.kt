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
import org.angproj.aux.util.TypePointer
import org.angproj.io.ffi.NativeLayout
import org.angproj.io.ffi.NativeStruct
import org.angproj.io.ffi.type.DefaultKQueueEventT


public class DefaultKQueueEvent internal constructor(
    bin: Binary, offset: Int, layout: NativeLayout<DefaultKQueueEvent>
) : NativeStruct(bin, offset, layout), DefaultKQueueEventT {

    override var ident: Long
        get() = bin.loadLong(0)
        set(value) { bin.saveLong(0, value) }

    override var filter: Short
        get() = bin.loadShort(1)
        set(value) { bin.saveShort(1, value) }

    override var flags: UShort
        get() = bin.loadUShort(2)
        set(value) { bin.saveUShort(2, value) }

    override var fflags: UInt
        get() = bin.loadUInt(3)
        set(value) { bin.saveUInt(3, value) }

    override var data: Long
        get() = bin.loadLong(4)
        set(value) { bin.saveLong(4, value) }

    override var udata: TypePointer
        get() = TypePointer(bin.loadLong(5))
        set(value) { bin.saveLong(5, value.toPointer()) }

    public companion object : NativeLayout<DefaultKQueueEvent>() {
        override val layout: Array<TypeSize> = arrayOf(
            TypeSize.LONG, TypeSize.SHORT, TypeSize.U_SHORT, TypeSize.U_INT, TypeSize.LONG, TypeSize.LONG
        )

        override fun create(
            bin: Binary, index: Int, layout: NativeLayout<DefaultKQueueEvent>
        ): DefaultKQueueEvent = DefaultKQueueEvent(bin, index, layout)
    }
}

/**
 *         public final uintptr_t ident = new uintptr_t(); // 32/64
 *         public final int16_t filter = new int16_t();
 *         public final u_int16_t flags = new u_int16_t();
 *         public final u_int32_t fflags = new u_int32_t();
 *         public final intptr_t data = new intptr_t(); // 32/64
 *         public final Pointer udata = new Pointer();
 * */