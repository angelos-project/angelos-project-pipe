package org.angproj.io.ffi

import jnr.enxio.channels.Native
import jnr.enxio.channels.Native.Timespec
import jnr.enxio.channels.Native.read
import jnr.ffi.Pointer
import jnr.ffi.annotations.IgnoreError
import jnr.ffi.annotations.In
import jnr.ffi.annotations.Out
import jnr.ffi.annotations.Transient
import jnr.ffi.annotations.Variadic
import jnr.ffi.types.size_t
import jnr.ffi.types.ssize_t
import jnr.ffi.types.u_int64_t
import org.angproj.aux.util.TypePointer
import org.angproj.io.ffi.type.AbstractSockAddr
import java.nio.ByteBuffer


public interface LibC {
    public fun socket(domain: Int, type: Int, protocol: Int): Int
    public fun listen(fd: Int, backlog: Int): Int
    public fun bind(fd: Int, addrPtr: Long, len: Int): Int
    public fun accept(fd: Int, /*@Out*/ addrPtr: Long, len: IntArray?): Int

    @ssize_t
    public fun read(fd: Int, /*@Out*/ dataPtr: Long, @size_t len: Int): Int

    @ssize_t
    public fun read(fd: Int, @Out data: ByteArray?, @size_t len: Int): Int

    @ssize_t
    public fun write(fd: Int, @In data: Pointer?, @size_t len: Int): Int

    @ssize_t
    public fun write(fd: Int, /*@In*/ dataPtr: Long, @size_t len: Int): Int

    @ssize_t
    public fun write(fd: Int, @Out data: ByteArray?, @size_t len: Int): Int

    @IgnoreError
    public fun strerror(error: Int): String?

    public fun close(fd: Int): Int

    @ssize_t
    public fun read(fd: Int, @Out data: Pointer?, @size_t size: Long): Int

    @ssize_t
    public fun read(fd: Int, @Out data: ByteArray?, @size_t size: Long): Int

    @ssize_t
    public fun write(fd: Int, @In data: Pointer?, @size_t size: Long): Int

    @ssize_t
    public fun write(fd: Int, @In data: ByteArray?, @size_t size: Long): Int

    @Variadic(fixedCount = 2)
    public fun fcntl(fd: Int, cmd: Int, @u_int64_t data: Int): Int
    public fun poll(@In @Out pfds: Pointer?, nfds: Int, timeout: Int): Int
    public fun kqueue(): Int
    public fun kevent(
        kq: Int, @In changebuf: Pointer?, nchanges: Int,
        @Out eventbuf: Pointer?, nevents: Int,
        @In @Transient timeout: Timespec?
    ): Int

    public fun pipe(@Out fds: IntArray?): Int
    public fun shutdown(s: Int, how: Int): Int

    /**
     * Backward compatibility with origin
     * */
    public fun poll(@In @Out pfds: ByteBuffer?, nfds: Int, timeout: Int): Int
    @ssize_t
    public fun read(fd: Int, @Out data: ByteBuffer?, @size_t size: Long): Int
    @ssize_t
    public fun write(fd: Int, @In data: ByteBuffer?, @size_t size: Long): Int

    /**
     * Filesystem
     * */

    public fun fopen(@In pathname: Pointer, @In mode: ByteArray): Pointer?
    public fun fclose(@In stream: Pointer): Int
    public fun fread()
    public fun fwrite()
    public fun fseek()
    public fun ftell()
    public fun ftruncate()
    public fun feof()
    public fun ferror()
    public fun clearall()
    public fun fflush()
    public fun fileno()


}

public fun LibC.bind(fd: Int, addr: AbstractSockAddr, len: Int): Int = bind(fd, addr.ptr, len)
public fun LibC.accept(fd: Int, /*@Out*/ addrPtr: AbstractSockAddr, len: IntArray?): Int = accept(fd, addrPtr.ptr, len)
//public fun LibC.read(fd: Int, @Out data: Binary, @size_t len: Int): Int = read(fd, data.toPointer(), len)
