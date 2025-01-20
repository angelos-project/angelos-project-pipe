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
package org.angproj.io.ffi

import java.nio.channels.SelectableChannel
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.spi.AbstractSelectionKey


public class KQSelectionKey public constructor(
    selector: KQSelector,
    channel: NativeSelectableChannel,
    ops: Int
) : AbstractSelectionKey() {
    private val selector: KQSelector
    private val channel: NativeSelectableChannel
    private var interestOps = 0
    private var readyOps = 0

    init {
        this.selector = selector
        this.channel = channel
        this.interestOps = ops
    }

    public fun getFD(): Int {
        return channel.getFD()
    }

    override fun channel(): SelectableChannel? {
        return channel as SelectableChannel?
    }

    override fun selector(): Selector {
        return selector
    }

    override fun interestOps(): Int {
        return interestOps
    }

    override fun interestOps(ops: Int): SelectionKey {
        interestOps = ops
        selector.interestOps(this, ops)
        return this
    }

    override fun readyOps(): Int {
        return readyOps
    }

    public fun readyOps(readyOps: Int) {
        this.readyOps = readyOps
    }
}
