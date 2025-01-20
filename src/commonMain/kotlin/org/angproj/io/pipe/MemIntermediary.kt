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
 * Mem intermediary layer for in memory buffers.
 *
 * @constructor Create empty Mem intermediary
 */
public abstract class MemIntermediary<
        IF: Intermittent<IF, EF, ER>,
        IR: Intermittent<IR, EF, ER>,
        EF: AbstractSheet<*, EF, ER>,
        ER: AbstractMemory<*, EF, ER>>(
) : Intermediary<IF, IR, EF, ER>(), Sheet<EF, ER>, Memory<EF, ER> {
}