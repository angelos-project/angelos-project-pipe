package org.angproj.io.ffi.kq

import jnr.ffi.Platform
import jnr.ffi.Pointer
import jnr.ffi.Type
import jnr.ffi.TypeAlias
import jnr.ffi.provider.jffi.NativeRuntime
import org.angproj.io.ffi.KQSelector.Companion.DEBUG
import java.lang.NumberFormatException

public class EventIO {
    private val layout: EventLayout //? = null
    private val uintptr_t: Type?

    init {
        var is_freebsd_12_or_later = false
        if (Platform.getNativePlatform().getOS() == Platform.OS.FREEBSD) {
            var version = System.getProperty("os.version")
            if (version != null) {
                var tr_i = -1
                for (c in charArrayOf(' ', '_', '-', '+', '.')) {
                    val i = version.indexOf(c)
                    if (i >= 0 && (tr_i == -1 || tr_i > i)) tr_i = i
                }
                if (tr_i >= 0) version = version.substring(0, tr_i)
                try {
                    val freebsd_major_version = version.toInt()
                    if (freebsd_major_version > 11) is_freebsd_12_or_later = true
                } catch (e: NumberFormatException) {
                    if (DEBUG) e.printStackTrace()
                }
            }
        }
        if (is_freebsd_12_or_later) {
            layout = FreeBSD12EventLayout(NativeRuntime.getSystemRuntime())
        } else {
            layout = LegacyEventLayout(NativeRuntime.getSystemRuntime())
        }
        uintptr_t = layout.getRuntime().findType(TypeAlias.uintptr_t)
    }

    public fun put(buf: Pointer, index: Int, fd: Int, filt: Int, flags: Int) {
        buf.putInt(uintptr_t, (index * layout.size()) + layout.ident.offset(), fd.toLong())
        buf.putShort((index * layout.size()) + layout.filter.offset(), filt.toShort())
        buf.putShort((index * layout.size()) + layout.flags.offset(), flags.toShort())
    }

    public fun size(): Int {
        return layout.size()
    }

    public fun getFD(ptr: Pointer, index: Int): Int {
        return ptr.getInt(uintptr_t, (index * layout.size()) + layout.ident.offset()).toInt()
    }

    public fun putFilter(buf: Pointer, index: Int, filter: Int) {
        buf.putShort((index * layout.size()) + layout.filter.offset(), filter.toShort())
    }

    public fun getFilter(buf: Pointer, index: Int): Int {
        return buf.getShort((index * layout.size()) + layout.filter.offset()).toInt() // .toInt()
    }

    public fun putFlags(buf: Pointer, index: Int, flags: Int) {
        buf.putShort((index * layout.size()) + layout.flags.offset(), flags.toShort())
    }

    public companion object {
        private val INSTANCE = EventIO()
        public fun getInstance(): EventIO {
            return INSTANCE
        }
    }
}