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
package org.angproj.io.fs

import platform.posix.F_GETFL
import platform.posix.F_SETFL


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
public actual enum class StatusFlag {
    UNKNOWN, GETFL, SETFL;

    public actual fun toCode(): Int = when (this) {
        GETFL -> F_GETFL
        SETFL -> F_SETFL
        else -> 0
    }

    public actual companion object {
        public actual fun <E> mapCode(code: E): StatusFlag = when (code) {
            F_GETFL -> GETFL
            F_SETFL -> SETFL
            else -> UNKNOWN
        }
    }
}