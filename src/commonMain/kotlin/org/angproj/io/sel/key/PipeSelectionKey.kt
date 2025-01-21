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
import org.angproj.io.sel.FileDescr
import org.angproj.io.sel.PipePair
import org.angproj.io.sel.Selector

public class PipeSelectionKey(selector: Selector) : SelectionKey(selector) {
    protected val pipeFd: PipePair = PipePair()
    protected val pipePayload: Binary = memBinOf(4)

    private var _fd: FileDescr

    init {
        check(pipe(pipeFd) == 0) { getLastErrorString() }
        _fd = FileDescr(pipeFd.inComing)
    }

    override val fileDescr: FileDescr
        get() = _fd

    override fun cancel() {
        TODO("Not yet implemented")
    }

    override suspend fun wakeupReceived(): Int {
        read(pipeFd.inComing, pipePayload.address(), pipePayload.capacity)
        return pipePayload.retrieveInt(0)
    }

    override fun wakeup(msg: Int) {
        pipePayload.storeInt(0, msg)
        write(pipeFd.outGoing, pipePayload.address(), pipePayload.capacity)
    }

    public override fun close() {
        close(pipeFd.outGoing)
        close(pipeFd.inComing)
        pipePayload.close()
    }
}