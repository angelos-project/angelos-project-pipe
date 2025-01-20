/**
 * Copyright (c) 2021-2025 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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
package org.angproj.io.fs

import org.angproj.aux.io.Text
import org.angproj.aux.mem.BufMgr
import org.angproj.aux.utf.Ascii
import org.angproj.aux.util.CodePoint
import org.angproj.aux.util.toCodePoint

public enum class PathSeparator {
    POSIX,
    WINDOWS;

    public fun toText(): Text = when(this) {
        POSIX -> posix
        WINDOWS -> win
    }

    public fun toCodePoint(): CodePoint = when(this) {
        POSIX -> Ascii.PRNT_SLASH.cp
        WINDOWS -> Ascii.PRNT_BSLASH.cp
    }.toCodePoint()

    public companion object {
        private val posix by lazy {
            BufMgr.txt(1).also {
                it.storeGlyph(0, POSIX.toCodePoint())
            }
        }

        private val win by lazy {
            BufMgr.txt(1).also {
                it.storeGlyph(0, WINDOWS.toCodePoint())
            }
        }
    }
}