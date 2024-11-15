/**
 * Copyright (c) 2022 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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
package org.angproj.io.pipe

public abstract class AbstractDevice(override val entryPoint: File, override val entryShared: IntermittentTransformer) : Device {
    val trueEntry by lazy { getTrueEntryOf() as AbstractFile }

    private var _readable = true
    val isReadable: Boolean
        get() = _readable

    private var _writable = true
    val isWritable: Boolean
        get() = _writable

    fun updateMode(mode: Mode): Unit = when(mode) {
        Mode.READ_ONLY_TXT -> {
            _readable = true
            _writable = false
        }
        Mode.WRITE_ONLY_TXT -> {
            _readable = false
            _writable = true
        }
        Mode.APPENDING_TXT -> {
            _readable = false
            _writable = true
        }
        Mode.READ_PLUS_TXT -> {
            _readable = true
            _writable = true
        }
        Mode.WRITE_PLUS_TXT -> {
            _readable = true
            _writable = true
        }
        Mode.APPEND_PLUS_TXT -> {
            _readable = true
            _writable = true
        }
        Mode.READ_ONLY_BIN -> {
            _readable = true
            _writable = false
        }
        Mode.WRITE_ONLY_BIN -> {
            _readable = false
            _writable = true
        }
        Mode.APPENDING_BIN -> {
            _readable = false
            _writable = true
        }
        Mode.READ_PLUS_BIN -> {
            _readable = true
            _writable = true
        }
        Mode.WRITE_PLUS_BIN -> {
            _readable = true
            _writable = true
        }
        Mode.APPEND_PLUS_BIN -> {
            _readable = true
            _writable = true
        }
    }
}