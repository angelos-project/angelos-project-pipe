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

import org.angproj.aux.util.Once

/**
 * Blk intermediary layer for simulated block devices.
 *
 * @constructor Create empty Blk intermediary
 */
public abstract class BlkIntermediary<
        IF: IntermittentTransformer<IF, EF, ER>,
        IR: IntermittentTransformer<IR, EF, ER>,
        EF: AbstractFile<*, EF, ER>,
        ER: AbstractDevice<*, EF, ER>>(
) : Intermediary<IF, IR, EF, ER>(), File<EF, ER>, Device<EF, ER> {
}