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
@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package org.angproj.io.net

import org.angproj.aux.util.TypePointer

public expect object NativeInterface {
    public fun strerror(error: Int): String

    public fun errno(): Int

    public fun read(fd: Int, data: TypePointer, size: Long): Int

    public fun write(fd: Int, data: TypePointer, size: Long): Int

    public fun socket(domain: Int, type: Int, protocol: Int): Int

    public fun connect(s: Int, name: TypePointer, namelen: Int): Int

    public fun pipe(fds: IntArray): Int

    public fun close(fd: Int): Int

    public fun kqueue(): Int

    public fun kevent(
        kq: Int,
        changebuf: TypePointer,
        nchanges: Int,
        eventbuf: TypePointer,
        nevents: Int,
        timeout: TypePointer
    ): Int
}