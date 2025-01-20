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
package org.angproj.io.sel

import kotlin.jvm.JvmInline

@JvmInline
public value class PipePair private constructor(public val pair: IntArray) {

    public constructor(): this(intArrayOf(-1, -1))

    public var inComing: Int
        get() = pair[0]
        set(value) { pair[0] = value }

    public var outGoing: Int
        get() = pair[1]
        set(value) { pair[1] = value }
}