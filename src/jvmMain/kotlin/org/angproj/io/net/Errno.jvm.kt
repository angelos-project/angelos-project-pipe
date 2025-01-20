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
@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package org.angproj.io.net

public actual enum class Errno {
    UNKNOWN, EINTR, EAGAIN, EWOULDBLOCK, ENOTCONN;

    public actual companion object {
        public actual fun <E> mapCode(code: E): Errno = when(code) {
            jnr.constants.platform.Errno.EINTR -> EINTR
            jnr.constants.platform.Errno.EAGAIN -> EAGAIN
            jnr.constants.platform.Errno.EWOULDBLOCK -> EWOULDBLOCK
            jnr.constants.platform.Errno.ENOTCONN -> ENOTCONN
            else -> UNKNOWN
        }
    }

}