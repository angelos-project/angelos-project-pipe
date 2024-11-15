/**
 * Copyright (c) 2022 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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
package org.angproj.io.pipe

/**
 * Endpoint for IO operations.
 *
 * @constructor Create empty End point
 */
interface EndPoint<P: Intermix, S: Intermittent> : Intermix {
    val entryPoint: P
    val entryShared: S

    /**
     * Find the true uppermost EntryPoint of a stack in case of several Intermediaries.
     *
     * @param entry
     * @return
     */
    fun getTrueEntryOf(entry: EntryPoint<*, *> = entryPoint as EntryPoint<*, *>): EntryPoint<*, *> = when(entry) {
        is EndPoint<*, *> -> getTrueEntryOf(entry.entryPoint as EntryPoint<*, *>)
        else -> entry
    }
}