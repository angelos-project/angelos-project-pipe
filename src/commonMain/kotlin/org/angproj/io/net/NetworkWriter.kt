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
package org.angproj.io.net

import org.angproj.aux.io.Memory
import org.angproj.aux.io.Segment
import org.angproj.aux.util.TypePointer

/**
 * Implements the PumpWriter interface for the IntermittentDuplexer for socket hardware connections.
 * */
public class NetworkWriter(
    fileDescriptor: Int,
    private var _inputCount: Long,
    private var _inputStale: Boolean
) : AbstractWriter(fileDescriptor) {

    override fun writeImpl(ptr: TypePointer, length: Int): Int {
        val n = repeat { write(fileDescriptor, ptr, length) }
        return if(n > 0) when {
            isError(getLastError()) -> { _inputStale = true; 0 }
            else -> {_inputStale = true; throw NetworkException(getLastErrorString()) }
        } else n
    }

    override val inputCount: Long
        get() = _inputCount

    override val inputStale: Boolean
        get() = _inputStale

    override fun write(data: Segment<*>): Int {
        data as Memory

        val length = if(data.limit > 0) writeImpl(data.address(), data.limit) else 0
        count += length

        return length
    }

    protected companion object: NetworkDriver()
}