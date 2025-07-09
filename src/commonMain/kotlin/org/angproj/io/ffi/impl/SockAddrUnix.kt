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
package org.angproj.io.ffi.impl

import org.angproj.aux.io.Binary
import org.angproj.aux.io.Text
import org.angproj.io.ffi.NativeLayout
import org.angproj.io.ffi.NativeStruct
import org.angproj.io.ffi.type.SockAddrUnixT


public abstract class SockAddrUnix internal constructor(
    bin: Binary,
    offset: Int,
    layout: NativeLayout<*>
) : NativeStruct(bin, offset, layout), SockAddrUnixT {

    public abstract override var sunAddr: Text

    public val capacity: Int
        get() = bin.capacity

    public val limit: Int
        get() = bin.limit

    public fun newLimit(limit: Int): Unit = bin.limitAt(limit)
}