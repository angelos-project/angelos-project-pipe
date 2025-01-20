/**
 * Copyright (c) 2024 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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
package org.angproj.io.ffi

import org.angproj.aux.io.Binary
import org.angproj.aux.io.TypeSize
import org.angproj.aux.io.address
import org.angproj.aux.util.Closable
import org.angproj.aux.util.TypePointer

public abstract class NativeStruct internal constructor(
    protected val bin: Binary,
    index: Int,
    private val layout: NativeLayout<*>
): Closable {
    private var _index: Int = index
    public val index: Int
        get() = _index

    private var _offset: Int = if(index < 0) 0 else index * layout.length
    public val offset: Int
        get() = _offset

    init {
        check(bin.isMem())
        require(offset in 0..(bin.limit - layout.length))
    }

    private var _ptr: TypePointer = TypePointer(bin.address().toPointer() + offset)
    public val ptr: TypePointer
        get() = _ptr

    public fun Binary.loadByte(index: Int): Byte {
        check(layout.typeOf(index) == TypeSize.BYTE)
        return retrieveByte(layout.offsetOf(index) + offset)
    }

    public fun Binary.loadUByte(index: Int): UByte {
        check(layout.typeOf(index) == TypeSize.U_BYTE)
        return retrieveUByte(layout.offsetOf(index) + offset)
    }

    public fun Binary.loadShort(index: Int): Short {
        check(layout.typeOf(index) == TypeSize.SHORT)
        return retrieveShort(layout.offsetOf(index) + offset)
    }

    public fun Binary.loadUShort(index: Int): UShort {
        check(layout.typeOf(index) == TypeSize.U_SHORT)
        return retrieveUShort(layout.offsetOf(index) + offset)
    }

    public fun Binary.loadInt(index: Int): Int {
        check(layout.typeOf(index) == TypeSize.INT)
        return retrieveInt(layout.offsetOf(index) + offset)
    }

    public fun Binary.loadUInt(index: Int): UInt {
        check(layout.typeOf(index) == TypeSize.U_INT)
        return retrieveUInt(layout.offsetOf(index) + offset)
    }

    public fun Binary.loadLong(index: Int): Long {
        check(layout.typeOf(index) == TypeSize.LONG)
        return retrieveLong(layout.offsetOf(index) + offset)
    }

    public fun Binary.loadULong(index: Int): ULong {
        check(layout.typeOf(index) == TypeSize.U_LONG)
        return retrieveULong(layout.offsetOf(index) + offset)
    }

    public fun Binary.loadFloat(index: Int): Float {
        check(layout.typeOf(index) == TypeSize.FLOAT)
        return retrieveFloat(layout.offsetOf(index) + offset)
    }

    public fun Binary.loadDouble(index: Int): Double {
        check(layout.typeOf(index) == TypeSize.DOUBLE)
        return retrieveDouble(layout.offsetOf(index) + offset)
    }

    public fun Binary.saveByte(index: Int, value: Byte) {
        check(layout.typeOf(index) == TypeSize.BYTE)
        storeByte(layout.offsetOf(index) + offset, value)
    }

    public fun Binary.saveUByte(index: Int, value: UByte) {
        check(layout.typeOf(index) == TypeSize.U_BYTE)
        storeUByte(layout.offsetOf(index) + offset, value)
    }

    public fun Binary.saveShort(index: Int, value: Short) {
        check(layout.typeOf(index) == TypeSize.SHORT)
        storeShort(layout.offsetOf(index) + offset, value)
    }

    public fun Binary.saveUShort(index: Int, value: UShort) {
        check(layout.typeOf(index) == TypeSize.U_SHORT)
        storeUShort(layout.offsetOf(index) + offset, value)
    }

    public fun Binary.saveInt(index: Int, value: Int) {
        check(layout.typeOf(index) == TypeSize.INT)
        storeInt(layout.offsetOf(index) + offset, value)
    }

    public fun Binary.saveUInt(index: Int, value: UInt) {
        check(layout.typeOf(index) == TypeSize.U_INT)
        storeUInt(layout.offsetOf(index) + offset, value)
    }

    public fun Binary.saveLong(index: Int, value: Long) {
        check(layout.typeOf(index) == TypeSize.LONG)
        storeLong(layout.offsetOf(index) + offset, value)
    }

    public fun Binary.saveULong(index: Int, value: ULong) {
        check(layout.typeOf(index) == TypeSize.U_LONG)
        storeULong(layout.offsetOf(index) + offset, value)
    }

    public fun Binary.saveFloat(index: Int, value: Float) {
        check(layout.typeOf(index) == TypeSize.FLOAT)
        storeFloat(layout.offsetOf(index) + offset, value)
    }

    public fun Binary.saveDouble(index: Int, value: Double) {
        check(layout.typeOf(index) == TypeSize.DOUBLE)
        storeDouble(layout.offsetOf(index) + offset, value)
    }

    override fun close(): Unit = when(index < 0) {
        true -> bin.close()
        else -> Unit
    }
}