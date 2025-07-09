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
import org.angproj.io.ffi.NativeLayout
import org.angproj.io.ffi.NativeStruct
import org.angproj.io.ffi.type.SockAddrIn6T


public abstract class SockAddrIn6 internal constructor(
    bin: Binary,
    offset: Int,
    layout: NativeLayout<*>
) : NativeStruct(bin, offset, layout), SockAddrIn6T {
}

/**
 *         public final Unsigned16 sin_family = new Unsigned16();
 *         public final Unsigned16 sin_port = new Unsigned16();
 *         public final Unsigned32 sin_addr = new Unsigned32();
 *         public final Padding sin_zero = new Padding(NativeType.SCHAR, 8);
 * */