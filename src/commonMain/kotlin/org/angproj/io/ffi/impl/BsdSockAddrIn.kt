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
import org.angproj.io.ffi.type.BsdSockAddrInT

public class BsdSockAddrIn internal constructor(
    bin: Binary, offset: Int, layout: NativeLayout<BsdSockAddrIn>
) : SockAddrIn(bin, offset, layout), BsdSockAddrInT {

    override var sinFamily: UByte
        get() = bin.loadUByte(1)
        set(value) { bin.saveUByte(1, value) }

    override var sinPort: UShort
        get() = bin.loadUShort(2)
        set(value) { bin.saveUShort(2, value) }

    override var sinAddr: UInt
        get() = bin.loadUInt(3)
        set(value) { bin.saveUInt(3, value) }

    public companion object : NativeLayout<BsdSockAddrIn>() {
        override val layout: Array<TypeSize> = arrayOf(
            TypeSize.U_BYTE, TypeSize.U_BYTE, TypeSize.U_SHORT, TypeSize.U_INT, TypeSize.LONG
        )

        override fun create(
            bin: Binary, index: Int, layout: NativeLayout<BsdSockAddrIn>
        ): BsdSockAddrIn = BsdSockAddrIn(bin, index, layout)
    }
}

/**
 *         public final Unsigned8 sin_len = new Unsigned8();
 *         public final Unsigned8 sin_family = new Unsigned8();
 *         public final Unsigned16 sin_port = new Unsigned16();
 *         public final Unsigned32 sin_addr = new Unsigned32();
 *         public final Padding sin_zero = new Padding(NativeType.SCHAR, 8);
 * */