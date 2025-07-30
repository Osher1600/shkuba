package com.dinari.shkuba

class Board {
    private var nativeHandle: Long = 0

    init {
        nativeHandle = nativeCreate()
    }

    fun getBoardSize(): Int {
        return getBoardSizeNative()
    }

    fun destroy() {
        nativeDestroy(nativeHandle)
    }

    // Native methods
    private external fun nativeCreate(): Long
    private external fun nativeDestroy(handle: Long)
    private external fun getBoardSizeNative(): Int

    companion object {
        init {
            System.loadLibrary("shkuba")
        }
    }
}
