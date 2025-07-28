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

    // JNI: Get all cards in hand as array of [suit, rank] pairs
    external fun getHandCards(): IntArray

    // JNI: Get hand size
    external fun getHandSize(): Int

    // JNI: Get card at specific index as [suit, rank]
    external fun getCardByIndex(index: Int): IntArray

    // Helper method to convert IntArray to NativeCard list
    fun getHandAsCards(): List<NativeCard> {
        val handData = getHandCards()
        val cards = mutableListOf<NativeCard>()
        
        for (i in handData.indices step 2) {
            if (i + 1 < handData.size) {
                cards.add(NativeCard(handData[i], handData[i + 1]))
            }
        }
        return cards
    }

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
