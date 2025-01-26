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

import kotlinx.coroutines.channels.onClosed
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.onSuccess
import kotlinx.coroutines.sync.Mutex
import org.angproj.aux.util.Closable
import org.angproj.io.pipe.Channel
import org.angproj.io.pipe.ChannelMode
import org.angproj.io.sel.event.SelectionEvent
import org.angproj.io.sel.key.SelectionKey
import org.angproj.io.sel.key.SelectionKey.Companion.MSG_FAIL
import org.angproj.io.sel.key.SelectionKey.Companion.MSG_INIT


public abstract class Selector<F, E: SelectionEvent>(
    factory: F
): Closable {

    protected abstract val nullEvent: E

    protected val keyMutex: Mutex = Mutex(true)
    protected val keys: LinkedHashMap<Int, SelectionKey<E>> = linkedMapOf()

    protected val registered: kotlinx.coroutines.channels.Channel<SelectionKey<E>> = kotlinx.coroutines.channels.Channel()
    protected val cancelled: kotlinx.coroutines.channels.Channel<SelectionKey<E>> = kotlinx.coroutines.channels.Channel()

    public fun register(chan: Channel, subsFlags: Int): SelectionKey<E> {
        require(chan.mode != ChannelMode.UNKNOWN)
        val key = registerImpl(chan, subsFlags)
        registered.trySend(key).onSuccess { key.wakeUp(MSG_INIT)
        }.onClosed { key.close()
        }.onFailure { key.close() }
        return key
    }

    protected abstract fun registerImpl(chan: Channel, subsFlags: Int): SelectionKey<E>


    protected abstract fun unregister(key: SelectionKey<E>)

    protected abstract suspend fun handleRegistered()

    protected abstract suspend fun handleCancelled()

    protected abstract fun handleChanged(key: SelectionKey<E>)

    public suspend fun selectNow(): Int {
        return poll(0)
    }

    public suspend fun select(timeout: Long): Int {
        return poll(timeout)
    }

    public suspend fun select(): Int {
        return poll(-1)
    }

    protected abstract suspend fun eventLoop(timeout: Long): Int

    protected suspend fun poll(timeout: Long): Int {
        handleCancelled()
        handleRegistered()
        //wakeupReceived()
        return eventLoop(timeout)
    }

    public fun cancel(key: SelectionKey<E>) {
        cancelled.trySend(key).onClosed { key.wakeUp(MSG_FAIL)
        }.onFailure { key.wakeUp(MSG_FAIL) }
    }

    protected abstract fun wakeUpReceived()

    public abstract fun wakeUp(): Selector<F, E>

    public override fun close() {
        keys.forEach { cancel(it.value) }

        while (true) {
            try {
                val key = registered.tryReceive().getOrThrow()
                cancel(key)
            } finally { break }
        }

        while (true) {
            try {
                val key = cancelled.tryReceive().getOrThrow()
                unregister(key)
            } finally { break }
        }

        registered.close()
        cancelled.close()
        keys.clear()
    }

    public companion object : SelectorProvider()
}