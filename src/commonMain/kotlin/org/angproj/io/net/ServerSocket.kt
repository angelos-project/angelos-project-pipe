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

import org.angproj.io.pipe.ChannelMode


public class ServerSocket: Server, ChannelIO() {
    override val port: Short
        get() = TODO("Not yet implemented")

    override suspend fun bindAndListen() {
        TODO("Not yet implemented")
    }

    override var id: Int
        get() = TODO("Not yet implemented")
        set(value) {}
    override var mode: ChannelMode
        get() = TODO("Not yet implemented")
        set(value) {}

    override fun isOpen(): Boolean {
        TODO("Not yet implemented")
    }

    override fun close() {
        TODO("Not yet implemented")
    }
}