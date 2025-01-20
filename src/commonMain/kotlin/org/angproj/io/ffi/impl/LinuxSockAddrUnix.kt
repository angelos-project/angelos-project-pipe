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
import org.angproj.aux.io.Binary
import org.angproj.aux.io.Text
import org.angproj.aux.io.TypeSize
import org.angproj.aux.io.asBinary
import org.angproj.aux.mem.BufMgr
import org.angproj.aux.utf.Ascii
import org.angproj.aux.util.toCodePoint
import org.angproj.io.ffi.NativeLayout
import org.angproj.io.ffi.NativeStruct
import org.angproj.io.ffi.type.DefaultSockAddrUnixT


public class LinuxSockAddrUnix internal constructor(
    bin: Binary, offset: Int, layout: NativeLayout<LinuxSockAddrUnix>
) : NativeStruct(bin, offset, layout), DefaultSockAddrUnixT {

    override var sunFamily: UShort
        get() = bin.loadUShort(0)
        set(value) { bin.saveUShort(0, value) }

    override var sunAddr: Text
        get() = getPath()
        set(value) { setPath(value) }

    override fun getPath(): Text {
        val txt = BufMgr.txt(106)
        val txtOff = BsdSockAddrUnix.offsetOf(1) + offset
        bin.copyInto(txt.asBinary(), 0, txtOff + 1, txtOff + 1 + 106)
        val eol = txt.indexOfFirst { it.value == Ascii.CTRL_NUL.cp }
        if(eol >= 0) txt.limitAt(eol-1)
        return txt
    }

    override fun setPath(txt: Text) {
        check(txt.limit <= 106)
        val dstOff = BsdSockAddrUnix.offsetOf(1) + offset
        txt.asBinary().copyInto(bin, dstOff + 1, 0, txt.limit)
        bin.wrap(dstOff) {
            writeGlyph(Ascii.CTRL_NUL.cp.toCodePoint())
            positionAt(txt.limit+2)
            writeGlyph(Ascii.CTRL_NUL.cp.toCodePoint())
        }
    }

    public companion object : NativeLayout<LinuxSockAddrUnix>() {
        override val layout: Array<TypeSize> = arrayOf(
            TypeSize.U_SHORT,

            TypeSize.LONG, TypeSize.LONG, TypeSize.LONG, TypeSize.LONG,
            TypeSize.LONG, TypeSize.LONG, TypeSize.LONG, TypeSize.LONG,
            TypeSize.LONG, TypeSize.LONG, TypeSize.LONG, TypeSize.LONG,
            TypeSize.LONG, TypeSize.INT // 108 bytes of filler
        )

        override fun create(
            bin: Binary, index: Int, layout: NativeLayout<LinuxSockAddrUnix>
        ): LinuxSockAddrUnix = LinuxSockAddrUnix(bin, index, layout)
    }
}

/**
 *          TODO("Implement according to jnr.unixsocket.SockAddrUnix")
 *         public final Unsigned16 sun_family = new Unsigned16();
 *         public final UTF8String sun_addr = new UTF8String(ADDR_LENGTH);
 * */