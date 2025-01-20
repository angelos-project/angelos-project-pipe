/*
 * Copyright (C) 2008 Wayne Meissner
 *
 * This file is part of the JNR project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jnr.enxio.channels

import jnr.constants.platform.Errno
import jnr.constants.platform.Fcntl
import jnr.constants.platform.OpenFlags
import jnr.enxio.channels.Native.Timespec
import jnr.enxio.channels.WinLibCAdapter.LibMSVCRT
import jnr.ffi.LastError
import jnr.ffi.LibraryLoader
import jnr.ffi.Platform
import jnr.ffi.Pointer
import jnr.ffi.Runtime
import jnr.ffi.Struct
import jnr.ffi.annotations.IgnoreError
import jnr.ffi.annotations.In
import jnr.ffi.annotations.Out
import jnr.ffi.annotations.Transient
import jnr.ffi.annotations.Variadic
import jnr.ffi.types.size_t
import jnr.ffi.types.ssize_t
import jnr.ffi.types.u_int64_t
import java.io.IOException
import java.lang.NullPointerException
import java.nio.ByteBuffer

public object Native {
    @JvmStatic
    public fun libc(): LibC {
        return SingletonHolder.libc //!!
    }

    @JvmStatic
    public fun getRuntime(): Runtime { // ?
        return SingletonHolder.runtime
    }

    @JvmStatic
    @Throws(IOException::class)
    public fun close(fd: Int): Int {
        var rc: Int
        do {
            rc = libc().close(fd)
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
    public fun read(fd: Int, dst: ByteBuffer): Int {
        if (dst == null) {
            throw NullPointerException("Destination buffer cannot be null")
        }
        require(!dst.isReadOnly()) { "Read-only buffer" }

        var n: Int
        do {
            n = libc().read(fd, dst, dst.remaining().toLong())
        } while (n < 0 && Errno.EINTR == getLastError())

        if (n > 0) {
            dst.position(dst.position() + n)
        }

        return n
    }

    @JvmStatic
    @Throws(IOException::class)
    public fun write(fd: Int, src: ByteBuffer): Int {
        if (src == null) {
            throw NullPointerException("Source buffer cannot be null")
        }

        var n: Int
        do {
            n = libc().write(fd, src, src.remaining().toLong())
        } while (n < 0 && Errno.EINTR == getLastError())

        if (n > 0) {
            src.position(src.position() + n)
        }

        return n
    }

    public fun setBlocking(fd: Int, block: Boolean) {
        var flags = libc().fcntl(fd, LibC.Companion.F_GETFL, 0)
        if (block) {
            flags = flags and LibC.Companion.O_NONBLOCK.inv()
        } else {
            flags = flags or LibC.Companion.O_NONBLOCK
        }

        libc().fcntl(fd, LibC.Companion.F_SETFL, flags)
    }

    public fun getBlocking(fd: Int): Boolean {
        val flags = libc().fcntl(fd, LibC.Companion.F_GETFL, 0)

        return (flags and LibC.Companion.O_NONBLOCK) != LibC.Companion.O_NONBLOCK
    }

    public fun shutdown(fd: Int, how: Int): Int {
        return libc().shutdown(fd, how)
    }

    public fun getLastErrorString(): String? {
        return libc().strerror(LastError.getLastError(getRuntime()))
    }

    public fun getLastError(): Errno? {
        return Errno.valueOf(LastError.getLastError(getRuntime()).toLong())
    }

    public interface LibC {
        public fun close(fd: Int): Int

        @ssize_t
        public fun read(fd: Int, @Out data: ByteBuffer?, @size_t size: Long): Int

        @ssize_t
        public fun read(fd: Int, @Out data: ByteArray?, @size_t size: Long): Int

        @ssize_t
        public fun write(fd: Int, @In data: ByteBuffer?, @size_t size: Long): Int

        @ssize_t
        public fun write(fd: Int, @In data: ByteArray?, @size_t size: Long): Int

        @Variadic(fixedCount = 2)
        public fun fcntl(fd: Int, cmd: Int, @u_int64_t data: Int): Int
        public fun poll(@In @Out pfds: ByteBuffer?, nfds: Int, timeout: Int): Int
        public fun poll(@In @Out pfds: Pointer?, nfds: Int, timeout: Int): Int
        public fun kqueue(): Int
        public fun kevent(
            kq: Int, @In changebuf: ByteBuffer?, nchanges: Int,
            @Out eventbuf: ByteBuffer?, nevents: Int,
            @In @Transient timeout: Timespec?
        ): Int

        public fun kevent(
            kq: Int,
            @In changebuf: Pointer?, nchanges: Int,
            @Out eventbuf: Pointer?, nevents: Int,
            @In @Transient timeout: Timespec?
        ): Int

        public fun pipe(@Out fds: IntArray?): Int
        public fun shutdown(s: Int, how: Int): Int

        @IgnoreError
        public fun strerror(error: Int): String?

        public companion object {
            public val F_GETFL: Int = Fcntl.F_GETFL.intValue()
            public val F_SETFL: Int = Fcntl.F_SETFL.intValue()
            public val O_NONBLOCK: Int = OpenFlags.O_NONBLOCK.intValue()
        }
    }

    private object SingletonHolder {
        public var libc: LibC // ? = null
        public val runtime: Runtime // ?

        init {
            val platform = Platform.getNativePlatform()
            val loader = LibraryLoader.create<LibC?>(LibC::class.java)
            loader.library(platform.getStandardCLibraryName())
            if (platform.getOS() == Platform.OS.SOLARIS) {
                loader.library("socket")
            }
            val straight = loader.load()
            if (platform.getOS() == Platform.OS.WINDOWS) {
                val mslib =
                    LibraryLoader.create<LibMSVCRT>(LibMSVCRT::class.java).load(platform.getStandardCLibraryName())
                libc = WinLibCAdapter(mslib)
            } else {
                libc = straight
            }
            runtime = Runtime.getRuntime(libc)
        }
    }

    public class Timespec : Struct {
        public val tv_sec: SignedLong = SignedLong()
        public val tv_nsec: SignedLong = SignedLong()

        public constructor() : super(SingletonHolder.runtime)

        public constructor(runtime: Runtime?) : super(runtime)

        public constructor(sec: Long, nsec: Long) : super(SingletonHolder.runtime) {
            tv_sec.set(sec)
            tv_nsec.set(nsec)
        }
    }
}
