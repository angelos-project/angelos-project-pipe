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
package org.angproj.io.sel

import org.angproj.io.ffi.NativeArray
import org.angproj.io.ffi.impl.DefaultKQueueEvent
import org.angproj.io.ffi.impl.TimeSpec
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

public class KQSelector : Selector() {

    private var kqfd: FileDescr = FileDescr()
    private var changeBuf: NativeArray<DefaultKQueueEvent> = DefaultKQueueEvent.allocateArray(MAX_EVENTS)
    private var eventBuf: NativeArray<DefaultKQueueEvent> = DefaultKQueueEvent.allocateArray(MAX_EVENTS)
    private val zeroTimeSpec: TimeSpec = TimeSpec.allocate().also {
        it.tvSec = 0
        it.tvNSec = 0
    }

    init {
        kqfd = kqueue()
        changeBuf.allocate().also {
            it.ident = pipe.fileDescr.fd.toLong()
            it.filter = EVFILT_READ
            it.flags = EV_ADD
        }
        kevent(kqfd, changeBuf, 1, zeroTimeSpec)
    }

    override fun poll(timeout: Long): Int {

        val ts = if(timeout >= 0) TimeSpec.allocate().also {
            it.tvSec = timeout.toDuration(DurationUnit.MILLISECONDS).inWholeSeconds
            it.tvNSec = (timeout % 1000).toDuration(DurationUnit.MILLISECONDS).inWholeNanoseconds
        } else zeroTimeSpec

        if(ts != zeroTimeSpec) ts.close()
    }

    override fun close() {
        changeBuf.clear()
        changeBuf.close()

        eventBuf.clear()
        eventBuf.close()

        zeroTimeSpec.close()
        super.close()
    }

    public companion object {
        public const val MAX_EVENTS: Int = 100
        public const val EVFILT_READ: Short = -1
        public const val EVFILT_WRITE: Short = -2
        public const val EV_ADD: UShort = 0x0001u
        public const val EV_DELETE: UShort = 0x0002u
        public const val EV_ENABLE: UShort = 0x0004u
        public const val EV_DISABLE: UShort = 0x0008u
        public const val EV_CLEAR: UShort = 0x0020u
    }
}

