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
package org.angproj.io.fs

import jnr.ffi.annotations.IgnoreError
import jnr.ffi.types.u_int32_t


public interface LibC {

    @IgnoreError
    public fun strerror(error: Int): String?
    //public fun strerror(error: Int): String?

    public fun open(path: /*CharSequence?*/ Long, flags: Int, @u_int32_t perm: Int): Int

}