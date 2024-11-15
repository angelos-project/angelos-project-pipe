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
 * Net intermediary layer for network protocol layer implementations.
 *
 * @constructor Create empty Net intermediary
 */
class NetIntermediary(
    override val entryPoint: Protocol,
    override val entryShared: IntermittentDuplexer,
    override val endShared: IntermittentDuplexer
) : Intermediary, Transport, Protocol {
    override var endPoint: Transport by EntryPoint.Attach()
}