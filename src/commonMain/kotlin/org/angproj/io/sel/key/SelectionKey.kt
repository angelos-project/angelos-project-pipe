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
import org.angproj.io.net.NetworkConnect
import org.angproj.io.sel.FileDescr
import org.angproj.io.sel.Selector

public abstract class SelectionKey(
    protected val selector: Selector
): NetworkConnect(), Closable {

    public abstract val fileDescr: FileDescr

    public val readable: Boolean
        get() = (readyOps() and OP_READ) != 0

    public val writable: Boolean
        get() = (readyOps() and OP_WRITE) != 0

    public val connectable: Boolean
        get() = (readyOps() and OP_CONNECT) != 0

    public val acceptable: Boolean
        get() = (readyOps() and OP_ACCEPT) != 0

    public abstract fun cancel()

    public suspend abstract fun wakeupReceived(): Int

    public abstract fun wakeup(msg: Int)

    public companion object {
        public const val OP_READ: Int = 1 shl 0
        public const val OP_WRITE: Int = 1 shl 2
        public const val OP_CONNECT: Int = 1 shl 3
        public const val OP_ACCEPT: Int = 1 shl 4
    }
}