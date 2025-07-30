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

    // JNI: Play card with board interaction
    external fun playCard(cardIndex: Int, cardsToTake: IntArray, boardHandle: Long): Int

    // JNI: Drop card to board
    external fun dropCard(cardIndex: Int, boardHandle: Long): Int

    // JNI: Get card by index
    external fun getCardByIndex(index: Int): IntArray

    // JNI: Get hand size
    external fun getHandSize(): Int

    // Helper methods for easier use
    fun addCard(card: NativeCard) {
        addCard(card.getSuit(), card.getRank())
    }

    fun playCard(cardIndex: Int, cardsToTake: IntArray, board: Board): Int {
        return playCard(cardIndex, cardsToTake, board.nativeHandle)
    }

    fun dropCard(cardIndex: Int, board: Board): Int {
        return dropCard(cardIndex, board.nativeHandle)
    }

    fun getCardAsNativeCard(index: Int): NativeCard? {
        val cardData = getCardByIndex(index)
        return if (cardData.size == 2) {
            NativeCard(cardData[0], cardData[1])
        } else {
            null
        }
    }

    fun getAllCards(): List<NativeCard> {
        val cards = mutableListOf<NativeCard>()
        val handSize = getHandSize()
        for (i in 0 until handSize) {
            getCardAsNativeCard(i)?.let { cards.add(it) }
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
        // Status constants matching C++ enum
        const val STATUS_OK = 0
        const val STATUS_ERROR_NOT_FIT = 1
        const val STATUS_ERROR_CARD_EXIST = 2

        init {
            System.loadLibrary("shkuba")
        }
    }
}
