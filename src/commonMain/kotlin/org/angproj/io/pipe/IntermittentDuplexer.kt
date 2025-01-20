/**
 * Copyright (c) 2022 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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
package org.angproj.io.pipe

import org.angproj.aux.io.DataSize
import org.angproj.aux.mem.Default
import org.angproj.aux.mem.MemoryManager
import org.angproj.aux.pipe.*
import org.angproj.aux.util.Once

/**
 * Intermittent duplexer for sending and receiving pipes.
 *
 * @constructor Create empty Intermittent duplexer
 */
public class IntermittentDuplexer (
    private val memMgr: MemoryManager<*> = Default,
    private val segSize: DataSize = DataSize._1K,
    private val bufSize: DataSize = DataSize._1K
) {
    private lateinit var pullPipe: PullPipe
    private lateinit var pushPipe: PushPipe

    private var entryPoint: Protocol by Once()
    private var endPoint: Transport by Once()

    public fun setup() {
        pullPipe = PullPipe(memMgr, PumpSource(endPoint.getPull()), segSize, bufSize)
        pushPipe = PushPipe(memMgr, PumpSink(endPoint.getPush()), segSize, bufSize)

        entryPoint.setPull(pullPipe)
        entryPoint.setPush(pushPipe)
    }

    public fun bind(entry: Protocol) {
        entry.rearIntermittent = this
        entryPoint = entry
    }

    public fun bind(end: Transport) {
        end.frontIntermittent = this
        endPoint = end
    }
}