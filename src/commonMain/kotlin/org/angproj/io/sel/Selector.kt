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
package org.angproj.io.sel

import kotlinx.coroutines.channels.Channel
import org.angproj.aux.util.Closable
import org.angproj.io.net.NetworkConnect
import org.angproj.io.sel.key.PipeSelectionKey
import org.angproj.io.sel.key.SelectionKey


public abstract class Selector: NetworkConnect(), Closable {

    protected val keys: LinkedHashMap<Int, SelectionKey> = linkedMapOf()

    protected val selected: Channel<SelectionKey> = Channel(Channel.UNLIMITED)

    protected val cancelled: Channel<SelectionKey> = Channel(Channel.UNLIMITED)

    protected val pipe: PipeSelectionKey = PipeSelectionKey(this)

    public fun selectNow(): Int {
        return poll(0)
    }

    public fun select(timeout: Long): Int {
        return poll(timeout)
    }

    public fun select(): Int {
        return poll(-1)
    }

    public fun cancel(key: SelectionKey) {
        cancelled.trySend(key)
    }

    protected abstract fun poll(timeout: Long): Int

    protected fun wakeupReceived() {
        suspend { pipe.wakeupReceived() }
    }

    public fun wakeup(): Selector {
        pipe.wakeup(0)
        return this
    }

    public override fun close() {
        pipe.close()
    }
}