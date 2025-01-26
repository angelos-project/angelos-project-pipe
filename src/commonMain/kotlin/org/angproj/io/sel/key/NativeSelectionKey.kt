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
import org.angproj.io.pipe.FileDescr
import org.angproj.io.sel.Selector
import org.angproj.io.sel.event.SelectionEvent

public abstract class NativeSelectionKey<E: SelectionEvent>(
    selector: Selector<*, E>,
    chan: Channel,
    event: E,
    subsFlags: Int,
): SelectionKey<E>(selector, chan, event, subsFlags) {
    public val fileDescr: FileDescr = FileDescr(chan.id)
}