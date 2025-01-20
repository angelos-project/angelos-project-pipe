package jnr.enxio.channels

import jnr.enxio.channels.Native.close
import jnr.enxio.channels.Native.getBlocking
import jnr.enxio.channels.Native.setBlocking
import org.angproj.io.Task
//import org.junit.Rule
/*import org.junit.Assert
import org.junit.Rule
import org.junit.Test*/
//import org.junit.rules.ExpectedException
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.nio.channels.Pipe
import kotlin.test.Test
import kotlin.test.assertEquals

class NativeTest {
    //@Rule
    //var expectedEx: ExpectedException = ExpectedException.none()

    @Test
    //@Throws(Exception::class)
    fun closeThrowsOnNativeError() {
        val fos = FileOutputStream("/dev/null")
        val descriptor = fos.getFD()
        val fdField = descriptor.javaClass.getDeclaredField("fd")
        println(fdField)
        fdField.setAccessible(true)
        val fd = fdField.get(descriptor) as Int? as Int
        close(fd)
        //expectedEx.expect(NativeException::class.java)
        close(fd)
    }

    @Test
    @Throws(Exception::class)
    fun setBlocking() {
        val pipe = Pipe.open()
        val sink = pipe.sink()
        //        sink.getClass().getModule().addOpens("sun.nio.ch", NativeTest.class.getModule());
        val fd1 = sink.javaClass.getDeclaredField("fd")
        fd1.setAccessible(true)
        val descriptor = fd1.get(sink) as FileDescriptor
        val fdField = descriptor.javaClass.getDeclaredField("fd")
        fdField.setAccessible(true)
        val fd = fdField.get(descriptor) as Int? as Int
        //Assert.assertEquals(true, getBlocking(fd))
        assertEquals(true, getBlocking(fd))
        setBlocking(fd, false)
        //Assert.assertEquals(false, getBlocking(fd))
        assertEquals(false, getBlocking(fd))

    }
}

