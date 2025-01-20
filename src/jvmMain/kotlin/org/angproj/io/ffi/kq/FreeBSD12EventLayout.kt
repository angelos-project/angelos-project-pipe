package org.angproj.io.ffi.kq

import jnr.ffi.Runtime
import jnr.ffi.StructLayout

public class FreeBSD12EventLayout(runtime: Runtime?) : EventLayout(runtime) {
    public val data: int64_t = int64_t()
    public val udata: Pointer = Pointer()
    public val ext: Array<u_int64_t?>? = array<u_int64_t?>(arrayOfNulls<u_int64_t>(4))
}