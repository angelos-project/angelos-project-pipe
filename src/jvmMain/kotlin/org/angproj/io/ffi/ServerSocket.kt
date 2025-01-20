package org.angproj.io.ffi

import jnr.constants.platform.Errno
import jnr.ffi.LastError
import jnr.ffi.Platform
import org.angproj.aux.io.Memory
import org.angproj.aux.io.Text
import org.angproj.aux.io.text
import org.angproj.aux.io.toText
import org.angproj.aux.util.NullObject
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.spi.AbstractSelector

public object ServerSocket {
    @JvmStatic
    @Throws(IOException::class)
    public fun close(fd: Int): Int = with(Native.libc) {
        var rc: Int
        do {
            rc = close(fd)
        } while (rc < 0 && Errno.EINTR == getLastError())

        if (rc < 0) {
            val message = String.format("Error closing fd %d: %s", fd, getLastErrorString())
            throw NativeException(message, getLastError())
        } else {
            return rc
        }
    }

    @JvmStatic
    @Throws(IOException::class)
    public fun read(fd: Int, dst: Memory): Int = with(Native.libc) {
        /*if (dst == null) {
            throw NullPointerException("Destination buffer cannot be null")
        }
        require(!dst.isReadOnly()) { "Read-only buffer" }*/

        var n: Int
        do {
            n = read(fd, dst.address(), dst.limit)
        } while (n < 0 && Errno.EINTR == getLastError())

        if (n > 0) dst.limitAt(n)
        return n
    }

    @JvmStatic
    @Throws(IOException::class)
    public fun write(fd: Int, src: Memory): Int = with(Native.libc) {
        /*if (src == null) {
            throw NullPointerException("Source buffer cannot be null")
        }*/

        val n = write(fd, src.address(), src.limit)
        /*var n: Int = 0
        do {
            n = write(fd, src.address(), src.limit)
        } while (n < 0 && Errno.EINTR == getLastError())

        if (n > 0) {
            src.position(src.position() + n)
        }*/

        return n
    }

    public fun setBlocking(fd: Int, block: Boolean): Unit = with(Native.libc) {
        var flags = fcntl(fd, Native.F_GETFL, 0)
        if (block) {
            flags = flags and Native.O_NONBLOCK.inv()
        } else {
            flags = flags or Native.O_NONBLOCK
        }

        fcntl(fd, Native.F_SETFL, flags)
    }

    public fun getBlocking(fd: Int): Boolean = with(Native.libc) {
        val flags = fcntl(fd, Native.F_GETFL, 0)
        return (flags and Native.O_NONBLOCK) != Native.O_NONBLOCK
    }

    public fun shutdown(fd: Int, how: Int): Int = with(Native.libc) {
        return shutdown(fd, how)
    }

    public fun getLastErrorString(): Text = with(Native.libc) {
        return strerror(LastError.getLastError(Native.runtime))?.toText() ?: NullObject.text
    }

    public fun getLastError(): Errno? {
        return Errno.valueOf(LastError.getLastError(Native.runtime).toLong())
    }

    public fun getSelector(): AbstractSelector {
        return if (Platform.getNativePlatform().isBSD()) KQSelector(this) else PollSelector(this)
    }
}