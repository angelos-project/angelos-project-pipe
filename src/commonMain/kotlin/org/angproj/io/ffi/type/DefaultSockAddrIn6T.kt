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


public interface DefaultSockAddrIn6T: SockAddrIn6T {
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
 *             struct sockaddr_in6 {
 *                sa_family_t     sin6_family;   /* AF_INET6 */
 *                in_port_t       sin6_port;     /* port number */
 *                uint32_t        sin6_flowinfo; /* IPv6 flow information */
 *                struct in6_addr sin6_addr;     /* IPv6 address */
 *                uint32_t        sin6_scope_id; /* Scope ID (new in Linux 2.4) */
 *            };
 *
 *            struct in6_addr {
 *                unsigned char   s6_addr[16];   /* IPv6 address */
 *            };
 * */