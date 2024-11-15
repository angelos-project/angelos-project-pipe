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
 * Blk intermediary layer for simulated block devices.
 *
 * @constructor Create empty Blk intermediary
 */
abstract class BlkIntermediary(
    override val entryPoint: File,
    override val entryShared: IntermittentTransformer,
    override val endShared: IntermittentTransformer
) : Intermediary, Device, File {
    override var endPoint: Device by EntryPoint.Attach()
}