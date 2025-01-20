package org.angproj.io.ffi.type

import java.nio.channels.SelectableChannel
import java.nio.channels.Selector

internal abstract class IO(selector: Selector, ch: SelectableChannel) {
    protected val channel: SelectableChannel
    protected val selector: Selector

    init {
        this.selector = selector
        this.channel = ch
    }

    abstract fun read()
    abstract fun write()
}