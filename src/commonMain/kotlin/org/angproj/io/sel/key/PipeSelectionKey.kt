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
package org.angproj.io.sel.key

import org.angproj.aux.io.Binary
import org.angproj.aux.io.address
import org.angproj.aux.io.memBinOf
import org.angproj.io.ffi.impl.NativeSelectionEvent
import org.angproj.io.net.NetworkConnect
import org.angproj.io.pipe.Channel
import org.angproj.io.pipe.ChannelMode
import org.angproj.io.sel.PipePair
import org.angproj.io.sel.Selector

public class PipeSelectionKey<E: NativeSelectionEvent> private constructor(
    selector: Selector<*, E>,
    event: E,
    subsFlags: Int,
    private val pipePair: PipePair
): NativeSelectionKey<E>(selector, create(pipePair.inComing), event, subsFlags) {

    private val buf: Binary = memBinOf(4)

    public constructor(selector: Selector<*, E>, event: E, subsFlags: Int): this(
        selector,
        event,
        subsFlags,
        PipePair().also { create(it) }
    )

    override fun wakeUpImpl(msg: Int)  {
        buf.storeInt(0, msg)
        write(pipePair.outGoing, buf.address(), buf.capacity)
    }

    override suspend fun wakeUpReceivedImpl(): Int {
        read(pipePair.inComing, buf.address(), buf.capacity)
        return buf.retrieveInt(0)
    }

    public override fun close() {
        close(pipePair.outGoing)
        close(pipePair.inComing)
        buf.close()
    }

    public companion object: NetworkConnect() {
        private fun create(pipePair: PipePair) {
            check(pipe(pipePair) == 0) {
                getLastErrorString()
            }
        }

        private fun create(fileDescr: Int): Channel {
            return object : Channel() {
                override val id: Int = fileDescr
                override val mode: ChannelMode = ChannelMode.VIRTUAL

                override fun isOpen(): Boolean {
                    TODO("Not yet implemented")
                }

                override fun close() {
                    TODO("Not yet implemented")
                }
            }
        }
    }
}