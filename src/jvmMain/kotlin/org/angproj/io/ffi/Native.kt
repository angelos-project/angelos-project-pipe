package org.angproj.io.ffi

import jnr.constants.platform.AddressFamily
import jnr.constants.platform.Fcntl
import jnr.constants.platform.OpenFlags
import jnr.constants.platform.Shutdown
import jnr.constants.platform.Sock
import jnr.ffi.Library
import jnr.ffi.Platform
import jnr.ffi.Runtime

public object Native {
    val libnames: Array<String?> by lazy {
        if (Platform.getNativePlatform().getOS() == Platform.OS.SOLARIS) arrayOf<String?>("socket", "nsl", "c")
        else arrayOf<String?>(Platform.getNativePlatform().getStandardCLibraryName())
    }

    val libc: LibC = Library.loadLibrary<LibC>(LibC::class.java, *libnames)
    val runtime: Runtime = Runtime.getSystemRuntime()

    val AF_INET: Int = AddressFamily.AF_INET.intValue()
    val SOCK_STREAM: Int = Sock.SOCK_STREAM.intValue()

    val F_GETFL: Int = Fcntl.F_GETFL.intValue()
    val F_SETFL: Int = Fcntl.F_SETFL.intValue()
    val O_NONBLOCK: Int = OpenFlags.O_NONBLOCK.intValue()

    val SHUT_RD = Shutdown.SHUT_RD.intValue()
    val SHUT_WR = Shutdown.SHUT_WR.intValue()
}