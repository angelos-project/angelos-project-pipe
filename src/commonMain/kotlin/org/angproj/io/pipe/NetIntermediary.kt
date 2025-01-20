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

import org.angproj.aux.io.PumpReader
import org.angproj.aux.io.PumpWriter
import org.angproj.aux.pipe.PullPipe
import org.angproj.aux.pipe.PushPipe
import org.angproj.aux.util.Once

/**
 * Net intermediary layer for network protocol layer implementations.
 *
 * @constructor Create empty Net intermediary
 */
public abstract class NetIntermediary(): Protocol, Transport {
    override var rearIntermittent: IntermittentDuplexer by Once()
    override var frontIntermittent: IntermittentDuplexer by Once()

    abstract override fun setPull(pull: PullPipe)

    abstract override fun setPush(push: PushPipe)


    abstract override fun getPush(): PumpWriter

    abstract override fun getPull(): PumpReader
}