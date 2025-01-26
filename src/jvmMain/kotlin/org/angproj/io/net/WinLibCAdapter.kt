/*
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
package org.angproj.io.net


import jnr.ffi.Runtime
import jnr.ffi.annotations.IgnoreError
import jnr.ffi.annotations.In
import jnr.ffi.annotations.Out
import jnr.ffi.byref.IntByReference
import jnr.ffi.provider.LoadedLibrary
import jnr.ffi.types.size_t
import jnr.ffi.types.ssize_t
import java.lang.UnsupportedOperationException
import java.nio.ByteBuffer

/**
 * MSVCRT.DLL only supports some LibC functions, but the symbols are different.
 * This adapter maps the MSVCRT.DLL names to standard LibC names
 */
public class WinLibCAdapter public constructor(winlibc: LibMSVCRT) : LibC, LoadedLibrary {
    public interface LibMSVCRT {
        public fun _close(fd: Int): Int

        @ssize_t
        public fun _read(fd: Int, @Out data: ByteBuffer?, @size_t size: Long): Int

        @ssize_t
        public fun _read(fd: Int, @Out data: ByteArray?, @size_t size: Long): Int

        @ssize_t
        public fun _write(fd: Int, @In data: ByteBuffer?, @size_t size: Long): Int

        @ssize_t
        public fun _write(fd: Int, @In data: ByteArray?, @size_t size: Long): Int
        public fun _pipe(@Out fds: IntArray?): Int

        @IgnoreError
        public fun _strerror(error: Int): String? // These functions don't exist:
        //public int shutdown(int s, int how);
        //public int fcntl(int fd, int cmd, int data);
        //public int poll(@In @Out ByteBuffer pfds, int nfds, int timeout);
        //public int poll(@In @Out Pointer pfds, int nfds, int timeout);
        //public int kqueue();
        //public int kevent(int kq, @In ByteBuffer changebuf, int nchanges,
        //                  @Out ByteBuffer eventbuf, int nevents,
        //                  @In @Transient Timespec timeout);
        //public int kevent(int kq,
        //                  @In Pointer changebuf, int nchanges,
        //                  @Out Pointer eventbuf, int nevents,
        //                  @In @Transient Timespec timeout);
    }

    private val win: LibMSVCRT

    init {
        this.win = winlibc
    }

    override fun close(fd: Int): Int {
        return win._close(fd)
    }

    override fun read(fd: Int, data: Long, size: Long): Int {
        TODO("Not yet implemented")
    }

    override fun write(fd: Int, data: Long, size: Long): Int {
        TODO("Not yet implemented")
    }

    override fun poll(pfds: Long, nfds: Int, timeout: Int): Int {
        TODO("Not yet implemented")
    }

    /*override fun read(fd: Int, data: ByteBuffer?, size: Long): Int {
        return win._read(fd, data, size)
    }

    override fun read(fd: Int, data: ByteArray?, size: Long): Int {
        return win._read(fd, data, size)
    }

    override fun write(fd: Int, data: ByteBuffer?, size: Long): Int {
        return win._write(fd, data, size)
    }

    override fun write(fd: Int, data: ByteArray?, size: Long): Int {
        return win._write(fd, data, size)
    }*/

    override fun pipe(fds: IntArray?): Int {
        return win._pipe(fds)
    }

    override fun strerror(error: Int): String? {
        return win._strerror(error)
    }

    override fun getRuntime(): Runtime? {
        return Runtime.getRuntime(win)
    }

    override fun socket(domain: Int, type: Int, protocol: Int): Int {
        TODO("Not yet implemented")
    }

    override fun listen(fd: Int, backlog: Int): Int {
        TODO("Not yet implemented")
    }

    override fun bind(fd: Int, addr: Long, len: Int): Int {
        TODO("Not yet implemented")
    }

    override fun accept(fd: Int, addr: Long, len: IntByReference?): Int {
        TODO("Not yet implemented")
    }

    override fun connect(s: Int, name: Long, namelen: Int): Int {
        TODO("Not yet implemented")
    }

    override fun getsockname(fd: Int, addr: Long, len: IntByReference?): Int {
        TODO("Not yet implemented")
    }

    override fun getpeername(fd: Int, addr: Long, len: IntByReference?): Int {
        TODO("Not yet implemented")
    }

    override fun socketpair(domain: Int, type: Int, protocol: Int, sv: IntArray?): Int {
        TODO("Not yet implemented")
    }

    // Unsupported Operations. Some may be implementable, others like fcntl may not be.
    override fun fcntl(fd: Int, cmd: Int, data: Int): Int {
        throw UnsupportedOperationException("fcntl isn't supported on Windows")
    }

    override fun getsockopt(s: Int, level: Int, optname: Int, optval: Long, optlen: IntByReference?): Int {
        TODO("Not yet implemented")
    }

    override fun setsockopt(s: Int, level: Int, optname: Int, optval: Long, optlen: Int): Int {
        TODO("Not yet implemented")
    }

    override fun sendto(s: Int, data: Long, size: Long, flags: Int, name: Long, namelen: Int): Int {
        TODO("Not yet implemented")
    }

    override fun recvfrom(s: Int, data: Long, size: Long, flags: Int, addr: Long, len: IntByReference?): Int {
        TODO("Not yet implemented")
    }

    /*override fun poll(pfds: ByteBuffer?, nfds: Int, timeout: Int): Int {
        throw UnsupportedOperationException("poll isn't supported on Windows")
    }

    override fun poll(pfds: Pointer?, nfds: Int, timeout: Int): Int {
        throw UnsupportedOperationException("poll isn't supported on Windows")
    }*/

    override fun kqueue(): Int {
        throw UnsupportedOperationException("kqueue isn't supported on Windows")
    }

    override fun kevent(kq: Int, changebuf: Long, nchanges: Int, eventbuf: Long, nevents: Int, timeout: Long): Int {
        TODO("Not yet implemented")
    }

    /*override fun kevent(
        kq: Int,
        changebuf: ByteBuffer?,
        nchanges: Int,
        eventbuf: ByteBuffer?,
        nevents: Int,
        timeout: Timespec?
    ): Int {
        throw UnsupportedOperationException("kevent isn't supported on Windows")
    }

    override fun kevent(
        kq: Int,
        changebuf: Pointer?,
        nchanges: Int,
        eventbuf: Pointer?,
        nevents: Int,
        timeout: Timespec?
    ): Int {
        throw UnsupportedOperationException("kevent isn't supported on Windows")
    }*/

    override fun shutdown(s: Int, how: Int): Int {
        throw UnsupportedOperationException("shutdown isn't supported on Windows")
    }
}