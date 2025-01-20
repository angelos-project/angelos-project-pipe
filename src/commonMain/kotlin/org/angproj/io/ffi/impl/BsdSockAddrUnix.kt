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

import org.angproj.aux.buf.copyInto
import org.angproj.aux.buf.wrap
import org.angproj.aux.io.*
import org.angproj.aux.mem.BufMgr
import org.angproj.aux.utf.Ascii
import org.angproj.aux.util.toCodePoint
import org.angproj.io.ffi.NativeLayout
import org.angproj.io.ffi.NativeStruct
import org.angproj.io.ffi.type.BsdSockAddrUnixT


public class BsdSockAddrUnix internal constructor(
    bin: Binary, offset: Int, layout: NativeLayout<BsdSockAddrUnix>
) : NativeStruct(bin, offset, layout), BsdSockAddrUnixT {

    override val sunLen: UByte
        get() = bin.loadUByte(0)

    override var sunFamily: UByte
        get() = bin.loadUByte(1)
        set(value) { bin.saveUByte(1, value) }

    override var sunAddr: Text
        get() = getPath()
        set(value) { setPath(value) }

    override fun getPath(): Text {
        val txt = BufMgr.txt(107)
        val txtOff = offsetOf(2) + offset
        bin.copyInto(txt.asBinary(), 0, txtOff, txtOff + 107)
        val eol = txt.indexOfFirst { it.value == Ascii.CTRL_NUL.cp }
        if(eol >= 0) txt.limitAt(eol-1)
        return txt
    }

    override fun setPath(txt: Text) {
        check(txt.limit <= 107)
        val dstOff = offsetOf(2) + offset
        txt.asBinary().copyInto(bin, dstOff, 0, txt.limit)
        bin.wrap(dstOff) {
            positionAt(txt.limit+1)
            writeGlyph(Ascii.CTRL_NUL.cp.toCodePoint())
        }
        bin.saveUByte(0, (txt.limit+1).toUByte())
    }

    public companion object : NativeLayout<BsdSockAddrUnix>() {
        override val layout: Array<TypeSize> = arrayOf(
            TypeSize.U_BYTE, TypeSize.U_BYTE,

            TypeSize.LONG, TypeSize.LONG, TypeSize.LONG, TypeSize.LONG,
            TypeSize.LONG, TypeSize.LONG, TypeSize.LONG, TypeSize.LONG,
            TypeSize.LONG, TypeSize.LONG, TypeSize.LONG, TypeSize.LONG,
            TypeSize.LONG, TypeSize.INT // 108 bytes of filler
        )

        override fun create(
            bin: Binary, index: Int, layout: NativeLayout<BsdSockAddrUnix>
        ): BsdSockAddrUnix = BsdSockAddrUnix(bin, index, layout)
    }
}

/**
 *          TODO("Implement according to jnr.unixsocket.SockAddrUnix")
 *        public final Unsigned8 sun_len = new Unsigned8();
 *         public final Unsigned8 sun_family = new Unsigned8();
 *         public final UTF8String sun_addr = new UTF8String(ADDR_LENGTH);
 * */