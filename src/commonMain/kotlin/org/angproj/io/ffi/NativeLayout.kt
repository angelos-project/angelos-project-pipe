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
import org.angproj.aux.io.TypeSize
import org.angproj.aux.io.memBinOf


public abstract class NativeLayout<E: NativeStruct> {

    protected abstract val layout: Array<TypeSize>

    private val offsets: IntArray by lazy { var cnt = 0; IntArray(layout.size) { cnt += layout[it].size; cnt } }
    public val count: Int by lazy { layout.size }
    public val length: Int by lazy { layout.sumOf { it.size } }

    public fun offsetOf(index: Int): Int = offsets[index]
    public fun typeOf(index: Int): TypeSize = layout[index]

    internal abstract fun create(bin: Binary, index: Int, layout: NativeLayout<E>): E

    /**
     * Allocates a single self-contained NativeStruct implementation of type E.
     * */
    public fun allocate(): E = create(memBinOf(length), -1, this)

    /**
     * Allocates an array of NativeStruct implementation of type E, the memory is centralized.
     * */
    public fun allocateArray(size: Int): NativeArray<E> = NativeArray(size, this)
}