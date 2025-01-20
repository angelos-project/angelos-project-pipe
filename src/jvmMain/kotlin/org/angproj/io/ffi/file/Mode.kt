package org.angproj.io.ffi.kq

public enum class Mode(private val mode: ByteArray) {
    R(byteArrayOf(0x72, 0x00)),       // O_RDONLY
    W(byteArrayOf(0x77, 0x00)),       // O_WRONLY, O_CREAT, O_TRUNC
    A(byteArrayOf(0x61, 0x00)),       // O_WRONLY, O_CREAT, O_APPEND
    R_PLUS(byteArrayOf(0x72, 0x2B, 0x00)),  // O_RDWR
    W_PLUS(byteArrayOf(0x77, 0x2B, 0x00)),  // O_RDWR, O_CREAT, O_TRUNC
    A_PLUS(byteArrayOf(0x61, 0x2B, 0x00));  // O_RDWR, O_CREAT, O_APPEND
}