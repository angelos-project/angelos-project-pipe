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
package org.angproj.io.ffi.type

import org.angproj.aux.io.Text


public interface BsdSockAddrUnixT : SockAddrUnixT{
    public val sunLen: UByte
    public var sunFamily: UByte
    public var sunAddr: Text
}

/**
 *        public final Unsigned8 sun_len = new Unsigned8();
 *         public final Unsigned8 sun_family = new Unsigned8();
 *         public final UTF8String sun_addr = new UTF8String(ADDR_LENGTH);
 * */