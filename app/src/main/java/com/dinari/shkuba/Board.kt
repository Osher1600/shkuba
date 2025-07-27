package com.dinari.shkuba

class Board {
    // Native pointer to the C++ Board instance
    private var nativeHandle: Long = 0

    init {
        nativeHandle = nativeCreate()
    }

    // JNI: Create C++ Board instance
    private external fun nativeCreate(): Long

    // JNI: Clean up C++ Board instance
    private external fun nativeDestroy(handle: Long)

    // JNI: Get board size
    external fun getBoardSize(): Int

    // JNI: Add card to board
    external fun addToBoard(suit: Int, rank: Int)

    // JNI: Get all cards on board as array of [suit, rank] pairs
    external fun getBoard(): IntArray

    protected fun finalize() {
        if (nativeHandle != 0L) {
            nativeDestroy(nativeHandle)
            nativeHandle = 0L
        }
    }

    companion object {
        init {
            System.loadLibrary("shkuba")
        }
    }
}
