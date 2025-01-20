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
 * Intermittent transformer for random access data blocks.
 *
 * @constructor Create empty Intermittent transformer
 */
public abstract class IntermittentTransformer<
        I :IntermittentTransformer<I, EF, ER>,
        EF: File<EF, ER>,
        ER: Device<EF, ER>> : Intermittent<I, EF, ER>() {
}