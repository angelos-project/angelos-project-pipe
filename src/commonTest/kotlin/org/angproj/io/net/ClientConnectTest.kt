package org.angproj.io.net

import kotlinx.coroutines.test.runTest
import org.angproj.aux.io.toText
import kotlin.test.Test
import kotlin.test.assertTrue

class clientConnectTest {

    @Test
    fun testInvoke() = runTest {
        var sock: ClientSocket
        clientConnect {
            address = "/tmp".toText()
            onSuccess { sock = this }
            onFailure {
                address.forEach { print(it.value.toChar()) }
                println()
                printStackTrace()
            }
        }
    }
}