package org.angproj.io.pipe

import org.angproj.aux.util.NullObject
import org.angproj.io.sel.Selector
import kotlin.test.Test

abstract class Plexer: Multiplexer<Channel>() {
    abstract override val selector: Selector<*, *>
    override val id: Int
        get() = TODO("Not yet implemented")
    override val mode: ChannelMode
        get() = TODO("Not yet implemented")

    override fun isOpen(): Boolean {
        TODO("Not yet implemented")
    }

    override fun close() {
        TODO("Not yet implemented")
    }

}

class MultiplexerTest {
    @Test
    fun testOpen() {
        Selector.openNativeSelector().register(NullObject.channel, 0).cancel()
        /*val plexer = Plexer()
        val chan = plexer.open {
            object : CoroChannel {
                override val regNumber: Int
                    get() = TODO("Not yet implemented")

                override fun isOpen(): Boolean {
                    TODO("Not yet implemented")
                }

                override fun close() {
                    TODO("Not yet implemented")
                }
            }
        }*/
    }
}