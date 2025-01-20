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

import jnr.ffi.Platform
import java.io.IOException
import java.lang.UnsupportedOperationException
import java.net.ProtocolFamily
import java.nio.channels.DatagramChannel
import java.nio.channels.Pipe
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.nio.channels.spi.AbstractSelector
import java.nio.channels.spi.SelectorProvider

public class NativeSelectorProvider public constructor() : SelectorProvider() {
    private object SingletonHolder {
        public var INSTANCE: NativeSelectorProvider = NativeSelectorProvider()
    }

    @Throws(IOException::class)
    override fun openDatagramChannel(): DatagramChannel? {
        throw UnsupportedOperationException("Not supported yet.")
    }

    @Throws(IOException::class)
    override fun openDatagramChannel(family: ProtocolFamily?): DatagramChannel? {
        throw UnsupportedOperationException("Not supported yet.")
    }

    @Throws(IOException::class)
    override fun openPipe(): Pipe? {
        throw UnsupportedOperationException("Not supported yet.")
    }

    @Throws(IOException::class)
    override fun openSelector(): AbstractSelector {
        return if (Platform.getNativePlatform().isBSD()) KQSelector(this) else PollSelector(this)
    }

    @Throws(IOException::class)
    override fun openServerSocketChannel(): ServerSocketChannel? {
        throw UnsupportedOperationException("Not supported yet.")
    }

    @Throws(IOException::class)
    override fun openSocketChannel(): SocketChannel? {
        throw UnsupportedOperationException("Not supported yet.")
    }

    public companion object {
        @JvmStatic
        public fun getInstance(): SelectorProvider {
            return SingletonHolder.INSTANCE
        }
    }
}
