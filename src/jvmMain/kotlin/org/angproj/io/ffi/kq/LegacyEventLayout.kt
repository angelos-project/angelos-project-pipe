package org.angproj.io.ffi.kq

import jnr.ffi.Runtime
import jnr.ffi.StructLayout

public class LegacyEventLayout(runtime: Runtime?) : EventLayout(runtime) {
    public val data: intptr_t = intptr_t()
    public val udata: Pointer = Pointer()
}