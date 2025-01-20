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


public enum class FileMode(public val flag: Int, public val bin: Boolean) {
    /**
     * Open existing text file as READ_ONLY positioned at the beginning.
     *
     * @constructor Create empty Read Only Txt
     */
    READ_ONLY_TXT(OpenFlag.RDONLY.toCode(), false), // "r"

    /**
     * Open existing text file truncated or create a new one as WRITE_ONLY positioned at the beginning.
     *
     * @constructor Create empty Write Only Txt
     */
    WRITE_ONLY_TXT(OpenFlag.WRONLY.toCode() or OpenFlag.CREAT.toCode() or OpenFlag.TRUNC.toCode(), false), // "w"

    /**
     * Append existing text file or create a new one as APPENDING positioned at the end.
     *
     * @constructor Create empty Appending Txt
     */
    APPENDING_TXT(OpenFlag.WRONLY.toCode() or OpenFlag.CREAT.toCode() or OpenFlag.APPEND.toCode(), false), // "a"

    /**
     * Open existing text file as READ & WRITE positioned at the beginning.
     *
     * @constructor Create empty Read Plus Txt
     */
    READ_PLUS_TXT(OpenFlag.RDWR.toCode(), false), // "r+"

    /**
     * Open existing text file truncated or create a new one as READ & WRITE positioned at the beginning.
     *
     * @constructor Create empty Write Plus Txt
     */
    WRITE_PLUS_TXT(OpenFlag.RDWR.toCode() or OpenFlag.CREAT.toCode() or OpenFlag.TRUNC.toCode(), false), // "w+"

    /**
     * Append existing text file or create a new one as READ & WRITE positioned at the end. (?)
     *
     * @constructor Create empty Append Plus Txt
     */
    APPEND_PLUS_TXT(OpenFlag.RDWR.toCode() or OpenFlag.CREAT.toCode() or OpenFlag.APPEND.toCode(), false), // "a+"

    /**
     * Open existing binary file as READ_ONLY positioned at the beginning.
     *
     * @constructor Create empty Read Only Bin
     */
    READ_ONLY_BIN(OpenFlag.RDONLY.toCode(), true), // "rb"

    /**
     * Open existing binary file truncated or create a new one as WRITE_ONLY positioned at the beginning.
     *
     * @constructor Create empty Write Only Bin
     */
    WRITE_ONLY_BIN(OpenFlag.WRONLY.toCode() or OpenFlag.CREAT.toCode() or OpenFlag.TRUNC.toCode(), true), // "wb"

    /**
     * Append binary file or create a new one as APPENDING positioned at the end.
     *
     * @constructor Create empty Appending Bin
     */
    APPENDING_BIN(OpenFlag.WRONLY.toCode() or OpenFlag.CREAT.toCode() or OpenFlag.APPEND.toCode(), true), // "ab"

    /**
     * Open existing binary file as READ & WRITE positioned at the beginning.
     *
     * @constructor Create empty Read Plus Bin
     */
    READ_PLUS_BIN(OpenFlag.RDWR.toCode(), true), // "rb+"

    /**
     * Open existing binary file truncated or create a new one as READ & WRITE positioned at the beginning.
     *
     * @constructor Create empty Write Plus Bin
     */
    WRITE_PLUS_BIN(OpenFlag.RDWR.toCode() or OpenFlag.CREAT.toCode() or OpenFlag.TRUNC.toCode(), true), // "wb+"

    /**
     * Append existing binary file or create a new one as READ & WRITE positioned at the end. (?)
     *
     * @constructor Create empty Append Plus Bin
     */
    APPEND_PLUS_BIN(OpenFlag.RDWR.toCode() or OpenFlag.CREAT.toCode() or OpenFlag.APPEND.toCode(), true), // "ab+"

    WRITE_EXCL_TXT(OpenFlag.WRONLY.toCode() or OpenFlag.CREAT.toCode() or OpenFlag.EXCL.toCode(), false), // "wx"
    WRITE_EXCL_PLUS_TXT(OpenFlag.RDWR.toCode() or OpenFlag.CREAT.toCode() or OpenFlag.EXCL.toCode(), false), // "w+x"

    WRITE_EXCL_BIN(OpenFlag.WRONLY.toCode() or OpenFlag.CREAT.toCode() or OpenFlag.EXCL.toCode(), true),
    WRITE_EXCL_PLUS_BIN(OpenFlag.RDWR.toCode() or OpenFlag.CREAT.toCode() or OpenFlag.EXCL.toCode(), true);

    public companion object {
        public fun toFlag(mode: String): FileMode = when(mode) {
            "r" -> READ_ONLY_TXT
            "rb" ->  READ_ONLY_BIN
            "w" -> WRITE_ONLY_TXT
            "wb" -> WRITE_ONLY_BIN
            "wx" -> WRITE_EXCL_TXT
            "a" -> APPENDING_TXT
            "ab" -> APPENDING_BIN
            "r+" -> READ_PLUS_TXT
            "rb+" -> READ_PLUS_BIN
            "w+" -> WRITE_PLUS_TXT
            "wb+" -> WRITE_PLUS_BIN
            "wx+" -> WRITE_EXCL_PLUS_TXT
            "a+" -> APPEND_PLUS_TXT
            "ab+" -> APPEND_PLUS_BIN
            "wbx" -> WRITE_EXCL_BIN
            "wb+x" -> WRITE_EXCL_PLUS_BIN
            else -> throw FileSystemException("Unknown mode: $mode")
        }
    }
}