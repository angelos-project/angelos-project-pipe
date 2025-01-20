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
package org.angproj.io.ffi.type

internal class BSDSockAddr : AbstractSockAddr(16) {
    public var length: UByte
        get() = bin.retrieveUByte(0)
        set(value) { bin.storeUByte(0, value) }

    public var family: UByte
        get() = bin.retrieveUByte(1)
        set(value) { bin.storeUByte(1, value) }

    public var port: UShort
        get() = bin.retrieveUShort(2)
        set(value) { bin.storeUShort(2, value) }

    public var address: UInt
        get() = bin.retrieveUInt(4)
        set(value) { bin.storeUInt(4, value) }

    // Padding pos 8, size 8
}