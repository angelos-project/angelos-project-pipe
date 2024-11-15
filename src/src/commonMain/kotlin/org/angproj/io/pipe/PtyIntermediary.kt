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
 * PTY intermediary layer for a console.
 *
 * @constructor Create empty Pty intermediary
 */
class PtyIntermediary(
    override val entryPoint: Terminal,
    override val entryShared: IntermittentExecutor,
    override val endShared: IntermittentExecutor
) : Intermediary, Shell, Terminal {
    override var endPoint: Shell by EntryPoint.Attach()
}