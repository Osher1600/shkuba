package com.dinari.shkuba

class Deck {
    // Native pointer to the C++ Deck instance
    private var nativeHandle: Long = 0

    init {
        nativeHandle = nativeCreate()
    }

    // JNI: Create C++ Deck instance
    private external fun nativeCreate(): Long

    // JNI: Clean up C++ Deck instance
    private external fun nativeDestroy(handle: Long)

    // JNI: Shuffle the deck (matches C++ shuffleDeck method)
    external fun shuffle()

    // JNI: Deal a card (returns suit and rank as array, matches C++ draw method)
    external fun dealCard(): IntArray

    protected fun finalize() {
        if (nativeHandle != 0L) {
            nativeDestroy(nativeHandle)
            nativeHandle = 0L
        }
    }

    companion object {
        // Library loaded in MainActivity
    }
}
