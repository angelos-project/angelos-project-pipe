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
package org.angproj.io.fs

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
public actual enum class OpenFlag {
    UNKNOWN,
    RDONLY,        // open for reading only
    WRONLY,        // open for writing only
    RDWR,          // open for reading and writing
    SEARCH,        // open directory for searching
    EXEC,          // open for execute only

    // In addition any combination of the following values can be or'ed in oflag:

    NONBLOCK,      // do not block on open or for data to become available
    APPEND,        // append on each write
    CREAT,         // create file if it does not exist
    TRUNC,         // truncate size to 0
    EXCL,          // error if O_CREAT and the file exists
    SHLOCK,        // atomically obtain a shared lock
    EXLOCK,        // atomically obtain an exclusive lock
    DIRECTORY,     // restrict open to a directory
    NOFOLLOW,      // do not follow symlinks
    SYMLINK,       // allow open of symlinks
    EVTONLY,       // descriptor requested for event notifications only
    CLOEXEC,       // mark as close-on-exec
    NOFOLLOW_ANY;  // do not follow symlinks in the entire path.

    public actual fun toCode(): Int = when (this) {
        RDONLY -> platform.posix.O_RDONLY
        WRONLY -> platform.posix.O_WRONLY
        RDWR -> platform.posix.O_RDWR
        APPEND -> platform.posix.O_APPEND
        CREAT -> platform.posix.O_CREAT
        TRUNC -> platform.posix.O_TRUNC
        EXCL -> platform.posix.O_EXCL
        else -> 0
    }

    public actual companion object {
        public actual fun <E> mapCode(code: E): OpenFlag = when (code) {
            platform.posix.O_RDONLY -> RDONLY
            platform.posix.O_WRONLY -> WRONLY
            platform.posix.O_RDWR -> RDWR
            platform.posix.O_APPEND -> APPEND
            platform.posix.O_CREAT -> CREAT
            platform.posix.O_TRUNC -> TRUNC
            platform.posix.O_EXCL -> EXCL
            else -> UNKNOWN
        }
    }
}