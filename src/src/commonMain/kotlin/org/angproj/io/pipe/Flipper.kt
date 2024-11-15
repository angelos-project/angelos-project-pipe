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

/**
 * Flipper represents a communication state in an Intermittent for how communication happens.
 * Read mode for read only, write mode for write only,  and flow mode for streaming.
 *
 * @constructor
 *
 * @param mode
 */
enum class Flipper(mode: Int) {
    /**
     * When in READ mode only reading takes place on the stack.
     *
     * @constructor Create empty Read
     */
    READ(-1),

    /**
     * Whem in FLOW mode data can flow freely in both directions in the stack.
     *
     * @constructor Create empty Flow
     */
    FLOW(0),

    /**
     * When in WRITE mode only writing takes place on the stack.
     *
     * @constructor Create empty Write
     */
    WRITE(1),

    /**
     * When in PAUSE no data is processed in the current Intermittent.
     *
     * @constructor Create empty Pause
     */
    PAUSE(-3)
}