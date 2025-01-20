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

import org.angproj.io.pipe.AbstractTransport
import org.angproj.io.sel.FileDescr

/**
 * Transport for all network connections using operating system network socket.
 * Also acts as an EndPoint when configuring an IntermittentDuplexer.
 * */
public class PosixSocketTransport(
    private val fileDescr: FileDescr
): AbstractTransport() {

    override fun getPull(): NetworkReader = NetworkReader(fileDescr.single, 0, false)
    override fun getPush(): NetworkWriter = NetworkWriter(fileDescr.single, 0, false)
}