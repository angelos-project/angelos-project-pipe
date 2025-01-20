package jnr.enxio.channels

import jnr.constants.platform.Errno
import java.io.IOException

public class NativeException public constructor(message: String?, errno: Errno?) : IOException(message) {
    private val errno: Errno?

    init {
        this.errno = errno
    }

    public fun getErrno(): Errno? {
        return errno
    }
}
