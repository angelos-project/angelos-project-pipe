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
 * Implements the PumpReader interface for the IntermittentDuplexer for socket hardware connections.
 * */
public class NetworkReader(
    fileDescriptor: Int,
    private var _outputCount: Long,
    private var _outputStale: Boolean
) : AbstractReader(fileDescriptor) {

    override fun readImpl(ptr: TypePointer, length: Int): Int {
        val n = repeat { read(fileDescriptor, ptr, length) }
        return if(n == -1) when {
            isError(getLastError()) -> { _outputStale = true; 0 }
            else -> { _outputStale = true; throw NetworkException(getLastErrorString()) }
        } else n
    }

    override val outputCount: Long
        get() = _outputCount

    override val outputStale: Boolean
        get() = _outputStale

    override fun read(data: Segment<*>): Int {
        data as Memory

        val length = if(data.limit > 0) readImpl(data.address(), data.limit) else 0
        count += length

        return length
    }

    protected companion object: NetworkDriver()
}