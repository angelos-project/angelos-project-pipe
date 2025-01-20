package org.angproj.io.pipe

import org.angproj.aux.pipe.*
import org.angproj.io.net.PosixSocketTransport
import kotlin.test.Test


class BinEntry() : AbstractProtocol<BinaryType>() {
    override fun setPull(pull: PullPipe) {
        BinarySink(pull)
    }

    override fun setPush(push: PushPipe) {
        BinarySource(push)
    }

}

class TxtEntry() : AbstractProtocol<TextType>() {
    override fun setPull(pull: PullPipe) {
        TextSink(pull)
    }

    override fun setPush(push: PushPipe) {
        TextSource(push)
    }
}

class PosixSocket() : AbstractTransport()

class IntermittentDuplexTest {

    @Test
    fun fixTrixSetup() {
        val mittent = IntermittentDuplexer().apply {
            bind(PosixSocketTransport())
            bind(BinEntry())
            setup()
        }
        mittent
    }
}