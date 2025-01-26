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
package org.angproj.io.ffi

import org.angproj.aux.buf.copyInto
import org.angproj.aux.io.*
import org.angproj.aux.util.Closable
import org.angproj.aux.util.TypePointer


public class NativeArray<E: NativeStruct>(
    private val initialSize: Int,
    private val mode: ArrayMode,
    private val layout: NativeLayout<E>
): Closable {
    private val bin: Binary = memBinOf(initialSize * layout.length)

    private var _ptr: TypePointer = bin.address()
    public val ptr: TypePointer
        get() = _ptr

    private var _position: Int = 0
    public val position: Int
        get() = _position

    public fun positionAt(newPos: Int) {
        require(newPos in 0.._limit)
        _position = newPos
    }

    private var _limit: Int = 0
    public val limit: Int
        get() = _limit

    /*public fun limitAt(newLimit: Int) {
        require(newLimit in 0..capacity)
        _limit = newLimit
        if(_position > newLimit) positionAt(newLimit) //_position = newLimit
    }*/

    public val capacity: Int
        get() = initialSize

    init {
        when(mode) {
            ArrayMode.BUFFER -> clear()
            ArrayMode.INCREMENT -> flip()
        }
    }

    public fun allocateWitness(): E = layout.create(bin, position, layout)

    public fun hasRemaining(): Boolean = limit - position == 0

    public fun access(ns: E, action: E.() -> Unit) {
        check(bin === ns.bin)
        check(position < limit)
        ns.update(_position++)
        ns.action()
    }

    public fun clear() {
        _position = 0
        _limit = capacity
    }

    public fun flip() {
        _limit = position
        _position = 0
    }

    public fun rewind() {
        _position = 0
    }

    private val slots = mutableListOf<E>()

    public fun allocate(): E {
        if(limit >= capacity) error("Maxed out")
        return layout.create(bin, _limit++, layout).also {
            slots.add(it)
            bin.limitAt(_limit * layout.length)
        }
    }

    public fun recycle(element: E) {
        if(element !in slots) error("Element from wrong array")
        val last: E = slots.last()
        if(last !== element) {
            val index = slots.indexOf(element)
            bin.copyInto(
                bin,
                index * layout.length,
                slots.lastIndex * layout.length,
                slots.size * layout.length
            )
            slots[index] = last
            slots.remove(last)
            _limit--
            bin.limitAt(_limit * layout.length)
        } else {
            slots.remove(last)
            _limit--
            bin.limitAt(_limit * layout.length)
        }
    }

    public fun reset() {
        slots.forEach { it.close() }
        slots.clear()
        bin.limitAt(0)
        _position = 0
    }

    public override fun close() {
        clear()
        bin.securelyRandomize()
        bin.close()
    }
}