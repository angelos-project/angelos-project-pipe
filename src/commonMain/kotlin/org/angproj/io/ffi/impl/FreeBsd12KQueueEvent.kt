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
import org.angproj.io.ffi.type.FreeBsd12KQueueEventT


public class FreeBsd12KQueueEvent internal constructor(
    bin: Binary, offset: Int, layout: NativeLayout<FreeBsd12KQueueEvent>
) : AbstractKQueueEvent(bin, offset, layout), FreeBsd12KQueueEventT {

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

    override var ext0: Long
        get() = bin.loadLong(6)
        set(value) { bin.saveLong(6, value) }

    override var ext1: Long
        get() = bin.loadLong(7)
        set(value) { bin.saveLong(7, value) }

    override var ext2: Long
        get() = bin.loadLong(8)
        set(value) { bin.saveLong(8, value) }

    override var ext3: Long
        get() = bin.loadLong(9)
        set(value) { bin.saveLong(9, value) }

    public companion object : NativeLayout<FreeBsd12KQueueEvent>() {
        override val layout: Array<TypeSize> = arrayOf(
            TypeSize.LONG, TypeSize.SHORT, TypeSize.U_SHORT, TypeSize.U_INT, TypeSize.LONG, TypeSize.LONG,
            TypeSize.LONG, TypeSize.LONG, TypeSize.LONG, TypeSize.LONG
        )

        override fun create(
            bin: Binary, index: Int, layout: NativeLayout<FreeBsd12KQueueEvent>
        ): FreeBsd12KQueueEvent = FreeBsd12KQueueEvent(bin, index, layout)
    }
}

/**
 *         public final uintptr_t ident = new uintptr_t(); // 32/64
 *         public final int16_t filter = new int16_t();
 *         public final u_int16_t flags = new u_int16_t();
 *         public final u_int32_t fflags = new u_int32_t();
 *         public final int64_t data = new int64_t();
 *         public final Pointer udata = new Pointer();
 *         public final u_int64_t[] ext = array(new u_int64_t[4]);
 * */