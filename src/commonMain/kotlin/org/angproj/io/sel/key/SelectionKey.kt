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
package org.angproj.io.sel.key

import org.angproj.aux.util.Closable
import org.angproj.aux.util.NullObject
import org.angproj.io.net.NetworkConnect
import org.angproj.io.pipe.Channel
import org.angproj.io.pipe.channel
import org.angproj.io.sel.Selector
import org.angproj.io.sel.event.SelectionEvent


public abstract class SelectionKey<E: SelectionEvent>(
    protected val selector: Selector<*, E>,
    public val chan: Channel,
    public val event: E,
    public val subsFlags: Int,
): NetworkConnect(), Closable {

    private var readyFlag: Int = 0

    public val readable: Boolean
        get() = (readyFlag and OP_READ) != 0

    public val writable: Boolean
        get() = (readyFlag and OP_WRITE) != 0

    public val connectable: Boolean
        get() = (readyFlag and OP_CONNECT) != 0

    public val acceptable: Boolean
        get() = (readyFlag and OP_ACCEPT) != 0

    public fun cancel() { selector.cancel(this) }

    public fun wakeUp(msg: Int) { wakeUpImpl(msg) }

    public suspend fun wakeUpReceived(): Int = wakeUpReceivedImpl().also { readyFlag = it }

    protected abstract fun wakeUpImpl(msg: Int)

    protected abstract suspend fun wakeUpReceivedImpl(): Int

    public companion object {
        public const val OP_READ: Int = 1 shl 0
        public const val OP_WRITE: Int = 1 shl 2
        public const val OP_CONNECT: Int = 1 shl 3
        public const val OP_ACCEPT: Int = 1 shl 4

        public const val MSG_INIT: Int = 1 shl 30
        public const val MSG_FAIL: Int = 1 shl 31

        public const val READ_ONLY: Int = OP_READ
        public const val WRITE_ONLY: Int = OP_WRITE
        public const val READ_WRITE: Int = READ_ONLY or WRITE_ONLY
    }
}