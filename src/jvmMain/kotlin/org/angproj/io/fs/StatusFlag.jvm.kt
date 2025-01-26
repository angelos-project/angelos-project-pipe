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


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
public actual enum class StatusFlag {
    UNKNOWN, GETFL, SETFL;

    public actual fun toCode(): Int = when (this) {
        GETFL -> jnr.constants.platform.Fcntl.F_GETFL.intValue()
        SETFL -> jnr.constants.platform.Fcntl.F_SETFL.intValue()
        else -> 0
    }

    public actual companion object {
        public actual fun <E> mapCode(code: E): StatusFlag = when (code) {
            jnr.constants.platform.Fcntl.F_GETFL -> GETFL
            jnr.constants.platform.Fcntl.F_SETFL -> SETFL
            else -> org.angproj.io.fs.StatusFlag.UNKNOWN
        }
    }
}