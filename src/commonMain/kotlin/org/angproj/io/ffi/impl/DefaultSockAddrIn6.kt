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
import org.angproj.io.ffi.type.DefaultSockAddrIn6T


/*public class DefaultSockAddrIn6 internal constructor(
    bin: Binary, offset: Int, layout: NativeLayout<DefaultSockAddrIn6>
) : SockAddrIn6(bin, offset, layout), DefaultSockAddrIn6T {

    override var sinFamily: UShort
        get() = bin.loadUShort(0)
        set(value) { bin.saveUShort(0, value) }

    override var sinPort: UShort
        get() = bin.loadUShort(1)
        set(value) { bin.saveUShort(1, value) }

    override var sinAddr: UInt
        get() = bin.loadUInt(2)
        set(value) { bin.saveUInt(2, value) }

    public companion object : NativeLayout<DefaultSockAddrIn6>() {
        override val layout: Array<TypeSize> = arrayOf(
            TypeSize.U_SHORT, TypeSize.U_SHORT, TypeSize.U_INT, TypeSize.LONG
        )

        override fun create(
            bin: Binary, index: Int, layout: NativeLayout<DefaultSockAddrIn6>
        ): DefaultSockAddrIn6 = DefaultSockAddrIn6(bin, index, layout)
    }
}*/

/**
 *         public final Unsigned16 sin_family = new Unsigned16();
 *         public final Unsigned16 sin_port = new Unsigned16();
 *         public final Unsigned32 sin_addr = new Unsigned32();
 *         public final Padding sin_zero = new Padding(NativeType.SCHAR, 8);
 * */