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
package org.angproj.io.ffi

import org.angproj.aux.io.Text
import org.angproj.aux.utf.Ascii
import org.angproj.aux.util.NullObject
import org.angproj.aux.util.TypePointer


public fun TypePointer.isNull(): Boolean = NullObject.ptr == this
private val nullPointer = TypePointer(0)
public val NullObject.ptr: TypePointer
    get() = nullPointer


public fun Text.limitAtNull(): Int {
    val oldLimit = limit
    limitAt(capacity)
    val newLimit: Int = indexOfFirst { it.value == Ascii.CTRL_NUL.cp }
    limitAt(if(newLimit < 0) oldLimit else newLimit)
    return limit
}