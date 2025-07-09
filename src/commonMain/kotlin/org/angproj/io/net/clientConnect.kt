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
package org.angproj.io.net

import org.angproj.aux.io.Text
import org.angproj.aux.util.Once
import org.angproj.io.ffi.impl.SockAddrUnix
import org.angproj.io.pipe.FileDescr
import org.angproj.io.sel.Selector

public typealias Suspendable<E> = suspend E.() -> Unit

/**
 * Client connection builder returning a socket.
 * */
public object clientConnect: NetworkConnect() {

    private val protocolFamily: ProtocolFamily = ProtocolFamily.PF_INET

    public abstract class Connect {
        public var address: Text by Once()

        public fun onSuccess(code: Suspendable<ClientSocket>) { onSuccess = code }
        public fun onFailure(code: Suspendable<NetworkException>) { onFailure = code }

        public var onSuccess: Suspendable<ClientSocket> by Once()
        public var onFailure: Suspendable<NetworkException> by Once()

        public fun getSocket(): FileDescr = socket(protocolFamily, SockType.SOCK_STREAM, 0)

        public fun doConnect(fileDescr: FileDescr, sockAddr: SockAddrUnix): Boolean {
            sockAddr.sunFamily = protocolFamily
            sockAddr.sunAddr = address
            return connect(fileDescr, sockAddr)
        }

        public fun getPeerName(fileDescr: FileDescr, sockAddr: SockAddrUnix): Boolean = getpeername(fileDescr, sockAddr)
    }

    public suspend operator fun invoke(conf: Connect.() -> Unit) {
        val c = object : Connect() {}
        c.conf()

        val sockAddr = Selector.newSockAddrUnix()
        try {
            c.getSocket().also {
                c.doConnect(it, sockAddr)
                c.getPeerName(it, sockAddr)
                nonBlocking(it)
                with(c) {
                    address.close()
                    ClientSocket(it, sockAddr).onSuccess()
                }
            }
        } catch (e: NetworkException) {
            sockAddr.close()
            with(c) {
                address.close()
                e.onFailure()
            }
        }
    }
}