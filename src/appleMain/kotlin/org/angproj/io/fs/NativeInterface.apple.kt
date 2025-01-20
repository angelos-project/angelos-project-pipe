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

import kotlinx.cinterop.*
import platform.posix.strerror as posix_strerror
import platform.posix.errno as posix_errno

@OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
public actual object NativeInterface {
    public actual fun strerror(error: Int): String {
        return posix_strerror(error)?.toKStringFromUtf8() ?: ""
    }

    public actual fun errno(): Int {
        return posix_errno
    }
}