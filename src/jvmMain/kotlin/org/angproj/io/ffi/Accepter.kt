package org.angproj.io.ffi

import org.angproj.io.ffi.type.IO
import org.angproj.io.ffi.type.SockAddr
import java.io.IOException
import java.nio.channels.SelectionKey
import java.nio.channels.Selector

internal class Accepter(selector: Selector, ch: NativeServerSocketChannel) : IO(selector, ch) {
    override fun read() {
        val inSock = SockAddr()
        val addrSize = intArrayOf(inSock.capcacity)
        val clientFd: Int = Native.libc.accept((channel as NativeSelectableChannel).getFD(), inSock, addrSize)
        println("client fd = " + clientFd)
        val ch = NativeSocketChannel(clientFd)
        try {
            ch.configureBlocking(false)
            ch.register(selector, SelectionKey.OP_READ, Client(selector, ch))
            selector.wakeup()
        } catch (ex: IOException) {
        }
    }

    override fun write() {
        val k = channel.keyFor(selector)
        k!!.interestOps(SelectionKey.OP_ACCEPT)
    }
}