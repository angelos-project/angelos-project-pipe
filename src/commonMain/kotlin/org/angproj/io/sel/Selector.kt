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

import org.angproj.aux.util.Closable
import org.angproj.io.net.NetworkConnect
import org.angproj.io.sel.key.PipeSelectionKey


public abstract class Selector: NetworkConnect(), Closable {
    protected val pipe: PipeSelectionKey = PipeSelectionKey()

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