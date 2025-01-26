/**
 * Copyright (c) 2022-2025 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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

import org.angproj.aux.sec.SecureRandom
import org.angproj.io.sel.Selector

/**
 * Multiplexer is an object that can open several channels and keep track on them.
 *
 * @constructor Create empty Multiplexer
 */
public abstract class Multiplexer<E: Channel>: Channel() {

    protected abstract val selector: Selector<*, *>

    public fun E.randId(): Int = SecureRandom.readInt()

    public fun open(create: () -> E): E {
        return create()
    }

}