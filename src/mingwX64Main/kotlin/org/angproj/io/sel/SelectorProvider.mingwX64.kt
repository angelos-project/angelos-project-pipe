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
package org.angproj.io.sel

import org.angproj.io.ffi.impl.DefaultSockAddrUnix
import org.angproj.io.ffi.impl.SockAddrUnix
import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalNativeApi::class)
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
public actual abstract class SelectorProvider {
    public actual fun openNativeSelector(): NativeSelector<*, *> {
        throw UnsupportedOperationException()
    }

    public actual fun newSockAddrUnix(): SockAddrUnix = when(Platform.osFamily) {
        else -> DefaultSockAddrUnix.allocate()
    }
}