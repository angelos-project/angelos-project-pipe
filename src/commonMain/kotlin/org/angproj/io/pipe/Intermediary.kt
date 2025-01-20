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
 * Intermediary is a combined entry- and endpoint to layer between the top entrypoint and bottom endpoint.
 *
 * @constructor Create empty Intermediary
 */
public abstract class Intermediary<
        IF: Intermittent<IF, EF, ER>,
        IR: Intermittent<IR, EF, ER>,
        EF: EntryPoint<EF, ER>,
        ER: EndPoint<EF, ER>>(
) : EntryPoint<EF, ER>, EndPoint<EF, ER> {
    public var frontIntermittent: IF by Once()
    public var rearIntermittent: IR by Once()
}