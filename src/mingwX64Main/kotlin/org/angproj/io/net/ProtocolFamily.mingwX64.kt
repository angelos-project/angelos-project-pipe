/**
 * Copyright (c) 2024-2025 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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
@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package org.angproj.io.net

public actual enum class ProtocolFamily {
    UNKNOWN, PF_LOCAL, PF_INET, PF_INET6;

    public actual fun toCode(): Int = when(this) {
        PF_INET -> platform.posix.PF_INET
        PF_INET6 -> platform.posix.PF_INET6
        else -> 0
    }

    public actual companion object {
        public actual fun <E> mapCode(code: E): ProtocolFamily = when(code) {
            platform.posix.PF_INET -> PF_INET
            platform.posix.PF_INET6 -> PF_INET6
            else -> UNKNOWN
        }
    }

}