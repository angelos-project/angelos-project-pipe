/**
 * Copyright (c) 2024 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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
 * Abstract EndPoint
 * */
public abstract class AbstractEnd<I: Intermittent<I, EF, ER>, EF: AbstractEntry<I, EF, ER>, ER: AbstractEnd<I, EF, ER>>(): EndPoint<EF, ER> {
    public var frontIntermittent: I by Once()
}