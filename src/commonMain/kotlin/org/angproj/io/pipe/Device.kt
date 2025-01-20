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

public interface Device<EF: File<EF, ER>, ER: Device<EF, ER>> : EndPoint<EF, ER> {
    public fun doFlush()

    public fun doTell(): Long

    public fun doSeek(position: Long, whence: Seek): Int

    public fun doTruncate(position: Long): Int

    public fun doClose()

    public fun forwardWrite()
}