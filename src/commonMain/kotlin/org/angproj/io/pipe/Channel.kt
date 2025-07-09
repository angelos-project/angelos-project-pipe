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

import org.angproj.aux.pipe.Close
import org.angproj.aux.util.NullObject

/**
 * Channel is used as an endpoint belonging to a shared connection at a multiplexer.
 *
 * @constructor Create empty Channel
 */
public abstract class Channel: Close {
    /**
     * Channel ID is not unique to process but rather unique to operating system or its current multiplexer.
     * A channel in UNKNOWN mode should have ID -1.
     * */
    public abstract val id: Int

    /**
     * Mode informs whether the ID came from the OS as NATIVE or from a multiplexer as VIRTUAL.
     * UNKNOWN means no underlying selector.
     * */
    public abstract val mode: ChannelMode
}


public fun Channel.isNull(): Boolean = NullObject.channel === this
private val nullChannel = object : Channel() {
    public override fun isOpen(): Boolean { throw UnsupportedOperationException() }
    override val id: Int = -1
    override val mode: ChannelMode = ChannelMode.UNKNOWN
    public override fun close() { throw UnsupportedOperationException() }
}
public val NullObject.channel: Channel
    get() = nullChannel