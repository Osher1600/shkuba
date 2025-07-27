package com.shkuba.native

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

    // JNI: Get hand size
    external fun getHandSize(): Int

    // JNI: Get card at index (returns suit and rank as array)
    external fun getCardByIndex(index: Int): IntArray

    // JNI: Drop card to board
    external fun dropCard(cardIndex: Int, boardHandle: Long): Int

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