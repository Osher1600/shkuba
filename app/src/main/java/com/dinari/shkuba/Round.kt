package com.dinari.shkuba

class Round(firstPlayer: Int) {
    // Native pointer to the C++ Round instance
    private var nativeHandle: Long = 0

    init {
        nativeHandle = nativeCreate(firstPlayer)
    }

    // JNI: Create C++ Round instance
    private external fun nativeCreate(firstPlayer: Int): Long

    // JNI: Clean up C++ Round instance
    private external fun nativeDestroy(handle: Long)

    // JNI: Get P1 points
    external fun getP1Points(): Int

    // JNI: Get P2 points
    external fun getP2Points(): Int

    // JNI: Execute first mini round
    external fun firstMiniRound(choice: Boolean)

    // JNI: Give cards to players
    external fun giveCardsToPlayers()

    // JNI: Count piles and add points
    external fun countPiles()

    // JNI: Add card to P1 pile
    external fun addToP1Pile(suit: Int, rank: Int)

    // JNI: Add card to P2 pile  
    external fun addToP2Pile(suit: Int, rank: Int)

    // JNI: Get the start card
    external fun getStartCard(): NativeCard

    // Helper methods for enum support
    fun addToP1Pile(card: NativeCard) {
        addToP1Pile(card.getSuit(), card.getRank())
    }

    fun addToP2Pile(card: NativeCard) {
        addToP2Pile(card.getSuit(), card.getRank())
    }

    protected fun finalize() {
        if (nativeHandle != 0L) {
            nativeDestroy(nativeHandle)
            nativeHandle = 0L
        }
    }

    companion object {
        // Player enum values matching C++
        const val P1 = 0
        const val P2 = 1

        // Library loaded in MainActivity
    }
}