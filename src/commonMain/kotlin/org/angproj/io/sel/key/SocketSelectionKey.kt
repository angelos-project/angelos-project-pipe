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

import kotlinx.coroutines.channels.Channel
import org.angproj.io.sel.FileDescr
import org.angproj.io.sel.Selector

public class SocketSelectionKey(
    public override val fileDescr: FileDescr,
    selector: Selector
): SelectionKey(selector) {

    private val semaphore: Channel<Int> = Channel(Channel.RENDEZVOUS)

    override fun cancel() {
        TODO("Not yet implemented")
    }

    override suspend fun wakeupReceived(): Int {
        return semaphore.receive()
    }

    override fun wakeup(msg: Int) {
        semaphore.trySend(msg)
    }

    override fun close() {
        semaphore.close()
    }
}