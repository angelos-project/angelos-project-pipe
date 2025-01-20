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
package org.angproj.io.net

import org.angproj.aux.io.PumpReader
import org.angproj.aux.util.TypePointer

public abstract class AbstractReader(protected val fileDescriptor: Int): PumpReader {

    protected var count: Long = 0

    protected abstract fun readImpl(ptr: TypePointer, length: Int): Int

}