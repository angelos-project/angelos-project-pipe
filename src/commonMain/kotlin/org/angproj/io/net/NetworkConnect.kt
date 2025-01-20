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

import org.angproj.io.ffi.type.KQueueEventT
import org.angproj.io.sel.FileDescr
import org.angproj.io.sel.PipePair

public abstract class NetworkConnect : NetworkDriver() {

    protected fun socket(domain: ProtocolFamily, type: SockType, protocol: Int): FileDescr {
        val fd = NativeInterface.socket(domain.toCode(), type.toCode(), protocol)
        if (fd < 0) throw NetworkException(getLastErrorString())
        return FileDescr(fd)
    }

    protected fun pipe(pipeFd: PipePair): Int = NativeInterface.pipe(pipeFd.pair)

    protected fun close(fd: Int): Int = NativeInterface.close(fd)

    protected fun kqueue(): FileDescr = FileDescr(NativeInterface.kqueue())

    protected fun<E: KQueueEventT> kevent(
        kqfd: FileDescr,

        ) {

    }
}