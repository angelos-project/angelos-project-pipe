package org.angproj.io.ffi.kq

import jnr.ffi.Runtime
import jnr.ffi.StructLayout

public abstract class EventLayout(runtime: Runtime?) : StructLayout(runtime) {
    public val ident: uintptr_t = uintptr_t()
    public val filter: int16_t = int16_t()
    public val flags: u_int16_t = u_int16_t()
    public val fflags: u_int32_t = u_int32_t()
}