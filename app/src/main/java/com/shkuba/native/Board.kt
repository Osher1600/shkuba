package com.shkuba.native

class Board {
    var nativeHandle: Long = 0
        private set

    init {
        nativeHandle = nativeCreate()
    }

    fun getBoardSize(): Int {
        return getBoardSizeNative()
    }

    fun addToBoardNative(suit: Int, rank: Int) {
        addToBoardJNI(suit, rank)
    }

    fun getBoardCards(): IntArray {
        return getBoardJNI()
    }

    fun destroy() {
        nativeDestroy(nativeHandle)
    }

    // Native methods
    private external fun nativeCreate(): Long
    private external fun nativeDestroy(handle: Long)
    private external fun getBoardSizeNative(): Int
    external fun addToBoardJNI(suit: Int, rank: Int)
    external fun getBoardJNI(): IntArray

    companion object {
        init {
            System.loadLibrary("shkuba")
        }
    }
}