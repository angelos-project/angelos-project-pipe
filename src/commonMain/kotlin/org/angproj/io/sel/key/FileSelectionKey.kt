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

import org.angproj.io.pipe.Channel
import kotlinx.coroutines.channels.Channel as QCHannel
import org.angproj.io.sel.Selector
import org.angproj.io.sel.event.SelectionEvent

public class FileSelectionKey<E: SelectionEvent>(
    selector: Selector<*, E>,
    chan: Channel,
    event: E,
    subsFlags: Int
): NativeSelectionKey<E>(selector, chan, event, subsFlags) {

    private val semaphore: QCHannel<Int> = QCHannel(QCHannel.RENDEZVOUS)

    override fun wakeUpImpl(msg: Int) { semaphore.trySend(msg) }

    override suspend fun wakeUpReceivedImpl(): Int = semaphore.receive()

    override fun close() {
        semaphore.close()
    }
}