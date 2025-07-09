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
import org.angproj.aux.io.text
import org.angproj.aux.util.NullObject
import org.angproj.io.ffi.impl.SockAddrUnix
import org.angproj.io.pipe.ChannelMode
import org.angproj.io.pipe.FileDescr
import org.angproj.io.sel.Selector
import org.angproj.io.sel.key.SelectionKey

public class ClientSocket internal constructor(
    override val mode: ChannelMode,
    private val selector: Selector<*, *>
): ChannelIO() {

    private lateinit var sockAddr: SockAddrUnix
    private var _closed: Boolean = false

    private var _id: Int = -1
    override val id: Int
        get() = _id

    private var _address: Text = NullObject.text
    public val address: String
        get() = _address.toString()

    public constructor(fileDescr: FileDescr, sockAddr: SockAddrUnix): this(
        ChannelMode.NATIVE,
        Selector.openNativeSelector()
    ) {
        _id = fileDescr.single
        this.sockAddr = sockAddr
        _address = sockAddr.sunAddr
    }

    private var key: SelectionKey<*> = selector.register(
        this, SelectionKey.OP_READ or SelectionKey.OP_WRITE
    )

    public override fun isOpen(): Boolean = !_closed

    public override fun close() {
        _address.close()
    }
}