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

public abstract class SelectionKey: NetworkConnect(), Closable {

    public abstract val fileDescr: FileDescr

    public suspend abstract fun wakeupReceived(): Int

    public abstract fun wakeup(msg: Int)
}