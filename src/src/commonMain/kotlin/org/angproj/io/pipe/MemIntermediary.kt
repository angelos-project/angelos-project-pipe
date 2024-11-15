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
 * Mem intermediary layer for in memory buffers.
 *
 * @constructor Create empty Mem intermediary
 */
abstract class MemIntermediary(
    override val entryPoint: Sheet,
    override val entryShared: Intermittent,
    override val endShared: Intermittent
) : Intermediary, Memory, Sheet {
    override var endPoint: Memory by EntryPoint.Attach()
}