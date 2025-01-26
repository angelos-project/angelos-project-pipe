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
package org.angproj.io.ffi.type

import org.angproj.io.sel.event.SelectionEvent


public interface KQueueEventT : SelectionEvent {
    public var ident: Long
    public var filter: Short
    public var flags: UShort
    public var fflags: UInt
}

/**
 *         public final uintptr_t ident = new uintptr_t();
 *         public final int16_t filter = new int16_t();
 *         public final u_int16_t flags = new u_int16_t();
 *         public final u_int32_t fflags = new u_int32_t();
 * */