package org.angproj.io.ffi

import org.angproj.aux.buf.asWrapped
import org.angproj.aux.io.memBinOf
import org.angproj.io.ffi.type.IO
import java.nio.channels.SelectionKey
import java.nio.channels.Selector

internal class Client(selector: Selector, ch: NativeSocketChannel) : IO(selector, ch) {
    private val bin = memBinOf(1024)
    private var buf = bin.asWrapped()
    private val bufPtr = bin.toPointer()

    override fun read() {
        val n: Int = Native.libc.read((channel as NativeSelectableChannel).getFD(), bufPtr, buf.limit - buf.position)
        println("Read " + n + " bytes from client")
        if (n <= 0) {
            val k = channel.keyFor(selector)
            k!!.cancel()
            Native.libc.close((channel as NativeSelectableChannel).getFD())
            return
        }
        buf.positionAt(n)
        buf.flip()
        channel.keyFor(selector)!!.interestOps(SelectionKey.OP_WRITE)
    }

    override fun write() {
        while (buf.limit - buf.position > 0) {
            val n: Int = Native.libc.write((channel as NativeSelectableChannel).getFD(), bufPtr, buf.limit - buf.position)
            println("write returned " + n)
            if (n > 0) {
                buf.positionAt(buf.position + n)
            }
            if (n == 0) {
                return
            }
            if (n < 0) {
                channel.keyFor(selector)!!.cancel()
                Native.libc.close((channel as NativeSelectableChannel).getFD())
                return
            }
        }
        println("outbuf empty")
        buf = bin.asWrapped()
        channel.keyFor(selector)!!.interestOps(SelectionKey.OP_READ)
    }
}