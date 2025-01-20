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

import kotlin.math.sign

/*public class Slots<E: Any>(public val initCapacity: Int, public val maxCapacity: Int) {

    private var _slotPosition: Int = 0

    private val unusedSlots = mutableSetOf<Int>()
    private val preparedSlots = ArrayList<E>(initCapacity)

    public fun allocate(element: E): Int = when {
        unusedSlots.isNotEmpty() -> unusedSlots.first().also {
            unusedSlots.remove(it)
            preparedSlots[it] = element
        }
    }

    public fun recycle(element: E): Boolean = when {
        element !in preparedSlots -> false
        else -> {
            true
        }
    }

    public fun free(element: E) {}
}*/