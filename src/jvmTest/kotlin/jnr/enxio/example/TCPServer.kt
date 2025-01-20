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
package jnr.enxio.example

import jnr.constants.platform.AddressFamily
import jnr.constants.platform.Sock
import jnr.enxio.channels.NativeSelectableChannel
import jnr.enxio.channels.NativeSelectorProvider.Companion.getInstance
import jnr.enxio.channels.NativeServerSocketChannel
import jnr.enxio.channels.NativeSocketChannel
import jnr.ffi.LastError
import jnr.ffi.Library
import jnr.ffi.NativeType
import jnr.ffi.Platform
import jnr.ffi.Runtime
import jnr.ffi.Struct
import jnr.ffi.annotations.In
import jnr.ffi.annotations.Out
import jnr.ffi.types.size_t
import jnr.ffi.types.ssize_t
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.SelectableChannel
import java.nio.channels.SelectionKey
import java.nio.channels.Selector

/**
 *
 * @author wayne
 */
object TCPServer {
    val libnames: Array<String?> = if (Platform.getNativePlatform().getOS() == Platform.OS.SOLARIS)
        arrayOf<String?>("socket", "nsl", "c")
    else
        arrayOf<String?>(Platform.getNativePlatform().getStandardCLibraryName())
    val libc: LibC = Library.loadLibrary<LibC>(LibC::class.java, *libnames)
    val runtime: Runtime = Runtime.getSystemRuntime()

    fun htons(value: Short): Short {
        return (((value.toInt() and 0xFF) shl 8) or ((value.toInt() and 0xFF00) ushr 8)).toShort()
    }

    fun serverSocket(port: Int): NativeServerSocketChannel {
        val fd: Int = libc.socket(LibC.Companion.AF_INET, LibC.Companion.SOCK_STREAM, 0)
        println("fd=" + fd)
        var addr: SockAddr?
        if (Platform.getNativePlatform().isBSD()) {
            val sin = BSDSockAddrIN()
            sin.sin_family.set(LibC.Companion.AF_INET.toByte().toShort())
            sin.sin_port.set(htons(port.toShort()).toInt())
            addr = sin
        } else {
            val sin = SockAddrIN()
            sin.sin_family.set(htons(LibC.Companion.AF_INET.toShort()).toInt())
            sin.sin_port.set(htons(port.toShort()).toInt())
            addr = sin
        }
        println("sizeof addr=" + Struct.size(addr))
        if (libc.bind(fd, addr, Struct.size(addr)) < 0) {
            System.err.println("bind failed: " + libc.strerror(LastError.getLastError(runtime)))
            System.exit(1)
        }
        if (libc.listen(fd, 5) < 0) {
            System.err.println("listen failed: " + libc.strerror(LastError.getLastError(runtime)))
            System.exit(1)
        }
        println("bind+listen succeeded")
        return NativeServerSocketChannel(fd)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val baseport: Short = 2000
        try {
            val selector: Selector = getInstance().openSelector()
            for (i in 0..1) {
                val ch: NativeServerSocketChannel = serverSocket(baseport + i)
                ch.configureBlocking(false)
                ch.register(selector, SelectionKey.OP_ACCEPT, Accepter(selector, ch))
            }
            while (true) {
                selector.select()
                for (k in selector.selectedKeys()) {
                    if ((k.readyOps() and (SelectionKey.OP_READ or SelectionKey.OP_ACCEPT)) != 0) {
                        (k.attachment() as IO).read()
                    }
                    if ((k.readyOps() and (SelectionKey.OP_WRITE or SelectionKey.OP_CONNECT)) != 0) {
                        (k.attachment() as IO).write()
                    }
                }
            }
        } catch (ex: IOException) {
        }
    }

    open class SockAddr : Struct(runtime)

    private class BSDSockAddrIN : SockAddr() {
        val sin_len: Unsigned8 = Unsigned8()
        val sin_family: Unsigned8 = Unsigned8()
        val sin_port: Unsigned16 = Unsigned16()
        val sin_addr: Unsigned32 = Unsigned32()
        val sin_zero: Padding = Padding(NativeType.SCHAR, 8)
    }

    private class SockAddrIN : SockAddr() {
        val sin_family: Unsigned16 = Unsigned16()
        val sin_port: Unsigned16 = Unsigned16()
        val sin_addr: Unsigned32 = Unsigned32()
        val sin_zero: Padding = Padding(NativeType.SCHAR, 8)
    }

    interface LibC {
        fun socket(domain: Int, type: Int, protocol: Int): Int
        fun close(fd: Int): Int
        fun listen(fd: Int, backlog: Int): Int
        fun bind(fd: Int, addr: SockAddr?, len: Int): Int
        fun accept(fd: Int, @Out addr: SockAddr?, len: IntArray?): Int

        @ssize_t
        fun read(fd: Int, @Out data: ByteBuffer?, @size_t len: Int): Int

        @ssize_t
        fun read(fd: Int, @Out data: ByteArray?, @size_t len: Int): Int

        @ssize_t
        fun write(fd: Int, @In data: ByteBuffer?, @size_t len: Int): Int
        fun strerror(error: Int): String?

        companion object {
            val AF_INET: Int = AddressFamily.AF_INET.intValue()
            val SOCK_STREAM: Int = Sock.SOCK_STREAM.intValue()
        }
    }

    private abstract class IO(selector: Selector, ch: SelectableChannel) {
        protected val channel: SelectableChannel
        protected val selector: Selector

        init {
            this.selector = selector
            this.channel = ch
        }

        abstract fun read()
        abstract fun write()
    }

    private class Accepter(selector: Selector, ch: NativeServerSocketChannel) : IO(selector, ch) {
        override fun read() {
            val sin = SockAddrIN()
            val addrSize = intArrayOf(Struct.size(sin))
            val clientfd: Int = libc.accept((channel as NativeSelectableChannel).getFD(), sin, addrSize)
            println("client fd = " + clientfd)
            val ch = NativeSocketChannel(clientfd)
            try {
                ch.configureBlocking(false)
                ch.register(selector, SelectionKey.OP_READ, Client(selector, ch))
                selector.wakeup()
            } catch (ex: IOException) {
            }
        }

        override fun write() {
            val k = channel.keyFor(selector)
            k.interestOps(SelectionKey.OP_ACCEPT)
        }
    }

    private class Client(selector: Selector, ch: NativeSocketChannel) : IO(selector, ch) {
        private val buf: ByteBuffer = ByteBuffer.allocateDirect(1024)
        override fun read() {
            val n: Int = libc.read((channel as NativeSelectableChannel).getFD(), buf, buf.remaining())
            println("Read " + n + " bytes from client")
            if (n <= 0) {
                val k = channel.keyFor(selector)
                k.cancel()
                libc.close((channel as NativeSelectableChannel).getFD())
                return
            }
            buf.position(n)
            buf.flip()
            channel.keyFor(selector).interestOps(SelectionKey.OP_WRITE)
        }

        override fun write() {
            while (buf.hasRemaining()) {
                val n: Int = libc.write((channel as NativeSelectableChannel).getFD(), buf, buf.remaining())
                println("write returned " + n)
                if (n > 0) {
                    buf.position(buf.position() + n)
                }
                if (n == 0) {
                    return
                }
                if (n < 0) {
                    channel.keyFor(selector).cancel()
                    libc.close((channel as NativeSelectableChannel).getFD())
                    return
                }
            }
            println("outbuf empty")
            buf.clear()
            channel.keyFor(selector).interestOps(SelectionKey.OP_READ)
        }
    }
}
