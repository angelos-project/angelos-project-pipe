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


public interface BsdSockAddrIn6T: SockAddrIn6T {
    public var sin6Len: UByte
    public override var sin6family: UInt // ?
    public override var sin6Port: UShort
    public override var sin6FlowInfo: UInt
    public override var sin6Addr0: UByte
    public override var sin6Addr1: UByte
    public override var sin6Addr2: UByte
    public override var sin6Addr3: UByte
    public override var sin6Addr4: UByte
    public override var sin6Addr5: UByte
    public override var sin6Addr6: UByte
    public override var sin6Addr7: UByte
    public override var sin6Addr8: UByte
    public override var sin6Addr9: UByte
    public override var sin6Addr10: UByte
    public override var sin6Addr11: UByte
    public override var sin6Addr12: UByte
    public override var sin6Addr13: UByte
    public override var sin6Addr14: UByte
    public override var sin6Addr15: UByte
    public override var sin6ScopeId: UInt
}

/**
 *       struct sockaddr_in6 {
 * 		     uint8_t	     sin6_len;
 * 		     sa_family_t     sin6_family;
 * 		     in_port_t	     sin6_port;
 * 		     uint32_t	     sin6_flowinfo;
 * 		     struct in6_addr sin6_addr;
 * 		     uint32_t	     sin6_scope_id;
 * 	     };
 *
 * */