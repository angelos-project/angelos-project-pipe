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

import kotlinx.coroutines.sync.withLock
import org.angproj.aux.util.NullObject
import org.angproj.io.pipe.channel
import org.angproj.io.sel.event.CoroEvent
import org.angproj.io.sel.key.CoroSelectionKey
import org.angproj.io.sel.key.SelectionKey


public abstract class CoroSelector<F: Any>(factory: F) : Selector<F, CoroEvent>(factory) {

    final override val nullEvent: CoroEvent = CoroEvent()

    protected val semaphore: CoroSelectionKey<CoroEvent> = CoroSelectionKey(
        this, NullObject.channel, nullEvent,
        SelectionKey.OP_READ or SelectionKey.OP_WRITE
    )

    override suspend fun handleRegistered(): Unit = keyMutex.withLock {
        while (true) {
            try {
                val key = registered.tryReceive().getOrThrow()
                keys[key.chan.id] = key
            } finally { break }
        }
    }

    override suspend fun handleCancelled(): Unit = keyMutex.withLock {
        while (true) {
            try {
                val key = cancelled.tryReceive().getOrThrow()
                unregister(key)
            } finally { break }
        }
    }

    override fun wakeUpReceived() {
        suspend { semaphore.wakeUpReceived() }
    }

    override fun wakeUp(): Selector<F, CoroEvent> {
        semaphore.wakeUp(0)
        return this
    }

    // ?
    override fun handleChanged(key: SelectionKey<CoroEvent>) {
        TODO("Not yet implemented")
    }

    override fun unregister(key: SelectionKey<CoroEvent>) {
        keys.remove(key.chan.id)
    }

    public override fun close() {
        super.close()
        semaphore.close()
    }
}