package org.angproj.io.net

import jnr.constants.platform.Fcntl
import jnr.constants.platform.OpenFlags
import jnr.ffi.annotations.IgnoreError
import jnr.ffi.annotations.In
import jnr.ffi.annotations.Out
import jnr.ffi.annotations.Variadic
import jnr.ffi.byref.IntByReference
import jnr.ffi.types.size_t
import jnr.ffi.types.ssize_t
import jnr.ffi.types.u_int64_t
import jnr.ffi.annotations.Transient


public interface LibC {
    public fun socket(domain: Int, type: Int, protocol: Int): Int
    public fun listen(fd: Int, backlog: Int): Int
    public fun bind(fd: Int, @In @Out @Transient addr: /*SockAddrUnix?*/ Long, len: Int): Int
    public fun accept(fd: Int, @Out addr: /*SockAddrUnix?*/ Long, @In @Out len: IntByReference?): Int
    public fun connect(s: Int, @In @Transient name: /*SockAddrUnix?*/ Long, namelen: Int): Int
    public fun getsockname(fd: Int, @Out addr: /*SockAddrUnix?*/ Long, @In @Out len: IntByReference?): Int
    public fun getpeername(fd: Int, @Out addr: /*SockAddrUnix?*/ Long, @In @Out len: IntByReference?): Int
    public fun socketpair(domain: Int, type: Int, protocol: Int, @Out sv: IntArray?): Int
    @Variadic(fixedCount = 2)
    public fun fcntl(fd: Int, cmd: Int, @u_int64_t data: Int): Int
    //public fun fcntl(fd: Int, cmd: Int, data: Int): Int
    public fun getsockopt(s: Int, level: Int, optname: Int, @Out optval: /*ByteBuffer? Timeval? */ Long, @In @Out optlen: IntByReference?): Int
    //public fun getsockopt(s: Int, level: Int, optname: Int, @Out optval: /*Timeval?*/ Long, @In @Out optlen: IntByReference?): Int
    public fun setsockopt(s: Int, level: Int, optname: Int, @In optval: /*ByteBuffer? Timeval? */ Long, optlen: Int): Int
    //public fun setsockopt(s: Int, level: Int, optname: Int, @In optval: /*Timeval?*/ Long, optlen: Int): Int

    @ssize_t
    public fun sendto(
        s: Int,
        @In data: /*ByteBuffer?*/ Long,
        @size_t size: Long,
        flags: Int,
        @In @Transient name: /*SockAddrUnix?*/ Long,
        namelen: Int
    ): Int

    @ssize_t
    public fun recvfrom(
        s: Int,
        @Out data: /*ByteBuffer?*/ Long,
        @size_t size: Long,
        flags: Int,
        @Out addr: /*SockAddrUnix?*/ Long,
        @In @Out len: IntByReference?
    ): Int

    public fun close(fd: Int): Int

    @ssize_t
    public fun read(fd: Int, @Out data: /*ByteArray?*/ Long, @size_t size: Long): Int

    @ssize_t
    public fun write(fd: Int, @In data: /*ByteArray?*/ Long, @size_t size: Long): Int


    public fun poll(@In @Out pfds: /*ByteBuffer?*/ Long, nfds: Int, timeout: Int): Int
    //public fun poll(@In @Out pfds: /*Pointer?*/ Long, nfds: Int, timeout: Int): Int
    public fun kqueue(): Int
    public fun kevent(
        kq: Int, @In changebuf: /*ByteBuffer?*/ Long, nchanges: Int,
        @Out eventbuf: /*ByteBuffer?*/ Long, nevents: Int,
        @In @Transient timeout: /*Timespec?*/ Long
    ): Int

    /*public fun kevent(
        kq: Int,
        @In changebuf: /*Pointer?*/ Long, nchanges: Int,
        @Out eventbuf: /*Pointer?*/ Long, nevents: Int,
        @In @Transient timeout: /*Timespec?*/ Long
    ): Int*/

    public fun pipe(@Out fds: IntArray?): Int
    public fun shutdown(s: Int, how: Int): Int

    @IgnoreError
    public fun strerror(error: Int): String?
    //public fun strerror(error: Int): String?

    public companion object {
        public val F_GETFL: Int = Fcntl.F_GETFL.intValue()
        public val F_SETFL: Int = Fcntl.F_SETFL.intValue()
        public val O_NONBLOCK: Int = OpenFlags.O_NONBLOCK.intValue()
    }
}