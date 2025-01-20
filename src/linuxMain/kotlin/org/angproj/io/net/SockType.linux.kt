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

public actual enum class SockType {
    UNKNOWN, SOCK_STREAM, SOCK_DGRAM, SOCK_RAW, SOCK_RDM, SOCK_SEQPACKET;

    public actual fun toCode(): Int = when(this) {
        SOCK_STREAM -> platform.posix.SOCK_STREAM
        SOCK_DGRAM -> platform.posix.SOCK_DGRAM
        SOCK_RAW -> platform.posix.SOCK_RAW
        SOCK_RDM -> platform.posix.SOCK_RDM
        SOCK_SEQPACKET -> platform.posix.SOCK_SEQPACKET
        else -> 0
    }

    public actual companion object {
        public actual fun <E> mapCode(code: E): SockType = when(code) {
            platform.posix.SOCK_STREAM -> SOCK_STREAM
            platform.posix.SOCK_DGRAM -> SOCK_DGRAM
            platform.posix.SOCK_RAW -> SOCK_RAW
            platform.posix.SOCK_RDM -> SOCK_RDM
            platform.posix.SOCK_SEQPACKET -> SOCK_SEQPACKET
            else -> UNKNOWN
        }
    }

}