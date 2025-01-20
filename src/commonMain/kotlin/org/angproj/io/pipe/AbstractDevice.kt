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

public abstract class AbstractDevice<
        I: IntermittentTransformer<I, EF, ER>,
        EF: AbstractFile<I, EF, ER>,
        ER: AbstractDevice<I, EF, ER>>(
) : /*AbstractEnd(I, EF, ER),*/ Device<EF, ER> {
    public var frontIntermittent: I by Once()
}