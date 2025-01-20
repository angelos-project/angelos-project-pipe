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

import org.angproj.aux.util.TypePointer


public interface DefaultKQueueEventT: KQueueEventT {
    public var data: Long
    public var udata: TypePointer
}

/**
 *         public final intptr_t data = new intptr_t();
 *         public final Pointer udata = new Pointer();
 * */