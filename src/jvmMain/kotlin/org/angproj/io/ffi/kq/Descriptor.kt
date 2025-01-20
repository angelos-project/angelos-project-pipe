package org.angproj.io.ffi.kq

import org.angproj.io.ffi.KQSelectionKey

public class Descriptor public constructor(fd: Int) {
    public val fd: Int
    public val keys: MutableSet<KQSelectionKey> = HashSet<KQSelectionKey>()
    public var write: Boolean = false
    public var read: Boolean = false

    init {
        this.fd = fd
    }
}