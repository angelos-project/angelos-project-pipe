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

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.angproj.io.ffi.NativeLayout
import org.angproj.io.ffi.impl.NativeSelectionEvent
import org.angproj.io.sel.key.NativeSelectionKey
import org.angproj.io.sel.key.PipeSelectionKey
import org.angproj.io.sel.key.SelectionKey

public abstract class NativeSelector<F: NativeLayout<E>, E: NativeSelectionEvent>(
    factory: F
) : Selector<F, E>(factory) {

    protected val memMutex: Mutex = Mutex(true)

    final override val nullEvent: E = factory.allocate()

    protected val semaphore: PipeSelectionKey<E> = PipeSelectionKey(
        this, nullEvent,
        SelectionKey.OP_READ or SelectionKey.OP_WRITE
    )

    override fun unregister(key: SelectionKey<E>) {
        keys.remove((key as NativeSelectionKey).fileDescr.single)
    }

    override fun wakeUpReceived() {
        suspend { semaphore.wakeUpReceived() }
    }

    public override fun wakeUp(): Selector<F, E> {
        semaphore.wakeUp(0)
        return this
    }

    public override fun close() {
        super.close()
        semaphore.close()
    }

    public fun toNative(): NativeSelector<*, *> = this

    protected suspend inline fun<reified E> fullMutex(shielded: () -> E): E = memMutex.withLock {
        keyMutex.withLock {
            shielded()
        }
    }
}