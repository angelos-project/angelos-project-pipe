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

import org.angproj.aux.io.Binary
import org.angproj.aux.io.address
import org.angproj.aux.io.memBinOf
import org.angproj.aux.io.securelyRandomize
import org.angproj.aux.util.Closable
import org.angproj.aux.util.TypePointer


public class NativeArray<E: NativeStruct>(
    private val initialSize: Int,
    private val layout: NativeLayout<E>
): Closable {
    private val bin: Binary = memBinOf(initialSize * layout.length)

    private var _ptr: TypePointer = bin.address()
    public val ptr: TypePointer
        get() = _ptr

    private var position: Int = 0

    private val unusedSlots = mutableSetOf<Int>()
    private val preparedSlots = ArrayList<E>(initialSize)

    public fun allocate(): E = when {
        unusedSlots.isNotEmpty() -> unusedSlots.first().let {
            unusedSlots.remove(it)
            preparedSlots[it]
        }
        position < initialSize -> layout.create(bin, ++position, layout).also {
            preparedSlots[position] = it
            bin.limitAt(position * layout.length)
        }
        else -> error("Max capacity reached")
    }

    public fun recycle(element: E): Boolean = when {
        element !in preparedSlots -> false
        else -> unusedSlots.add(preparedSlots.indexOf(element))
    }

    public fun clear() {
        preparedSlots.forEach { it.close() }
        preparedSlots.clear()
        bin.limitAt(0)
        position = 0
    }

    public override fun close() {
        clear()
        bin.securelyRandomize()
        bin.close()
    }
}