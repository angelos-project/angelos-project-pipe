/**
 * Copyright (c) 2025 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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
package org.angproj.io.net

import platform.posix.SHUT_RD
import platform.posix.SHUT_RDWR
import platform.posix.SHUT_WR

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
public actual enum class ShutFlag {
    UNKNOWN, SD_READ, SD_WRITE, SD_BOTH;

    public actual fun toCode(): Int = when (this) {
        SD_READ -> SHUT_RD
        SD_WRITE -> SHUT_WR
        SD_BOTH -> SHUT_RDWR
        else -> 0
    }

    public actual companion object {
        public actual fun <E> mapCode(code: E): ShutFlag = when (code) {
            SHUT_RD -> SD_READ
            SHUT_WR -> SD_WRITE
            SHUT_RDWR -> SD_BOTH
            else -> UNKNOWN
        }
    }
}