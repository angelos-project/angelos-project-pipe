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


public interface FreeBsd12KQueueEventT : KQueueEventT {
    public var data: Long
    public var udata: TypePointer
    public var ext0: Long
    public var ext1: Long
    public var ext2: Long
    public var ext3: Long
}

/**
 *         public final int64_t data = new int64_t();
 *         public final Pointer udata = new Pointer();
 *         public final u_int64_t[] ext = array(new u_int64_t[4]);
 * */