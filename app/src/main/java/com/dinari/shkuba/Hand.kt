package com.dinari.shkuba

class Hand {
    // Native pointer to the C++ Hand instance
    private var nativeHandle: Long = 0

    init {
        nativeHandle = nativeCreate()
    }

    // JNI: Create C++ Hand instance
    private external fun nativeCreate(): Long

    // JNI: Clean up C++ Hand instance
    private external fun nativeDestroy(handle: Long)

    // JNI: Add card to hand (matches C++ addToHand method)
    external fun addCard(suit: Int, rank: Int)

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
