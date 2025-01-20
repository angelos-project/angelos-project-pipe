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

import org.angproj.aux.io.toBinary
import org.angproj.aux.pipe.Close
import org.angproj.aux.util.NullObject
import org.angproj.aux.util.Uuid4
import org.angproj.aux.util.uuid4Of

/**
 * Channel is used as an endpoint belonging to a shared connection at a multiplexer.
 *
 * @constructor Create empty Channel
 */
public interface Channel: Close {
    public override fun isOpen(): Boolean
    public override fun close()
}


public fun Channel.isNull(): Boolean = NullObject.channel === this
private val nullChannel = object : Channel {
    public override fun isOpen(): Boolean { throw UnsupportedOperationException() }
    public override fun close() { throw UnsupportedOperationException() }
}
public val NullObject.channel: Channel
    get() = nullChannel