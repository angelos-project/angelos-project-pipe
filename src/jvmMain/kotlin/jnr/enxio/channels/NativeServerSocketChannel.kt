/*
 * Copyright (C) 2008 Wayne Meissner
 *
 * This file is part of the JNR project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jnr.enxio.channels

import jnr.enxio.channels.NativeSelectorProvider.Companion.getInstance
import java.io.IOException
import java.nio.channels.SelectionKey
import java.nio.channels.spi.AbstractSelectableChannel
import java.nio.channels.spi.SelectorProvider

public class NativeServerSocketChannel public constructor(provider: SelectorProvider?, fd: Int, ops: Int) :
    AbstractSelectableChannel(provider), NativeSelectableChannel {
    private val fd: Int
    private val validOps: Int

    public constructor(fd: Int) : this(getInstance(), fd, SelectionKey.OP_ACCEPT or SelectionKey.OP_READ)

    init {
        this.fd = fd
        this.validOps = ops
    }

    @Throws(IOException::class)
    override fun implCloseSelectableChannel() {
        Native.close(fd)
    }

    @Throws(IOException::class)
    override fun implConfigureBlocking(block: Boolean) {
        Native.setBlocking(fd, block)
    }

    override fun validOps(): Int {
        return validOps
    }

    override fun getFD(): Int {
        return fd
    }
}
