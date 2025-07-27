package com.shkuba.native

/**
 * Native wrapper for C++ Round class - manages full game rounds
 */
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

    // JNI: Get player 1 points
    external fun getP1Points(): Int

    // JNI: Get player 2 points
    external fun getP2Points(): Int

    // JNI: Execute first mini round with choice
    external fun firstMiniRound(choice: Boolean)

    // JNI: Deal cards to players
    external fun giveCardsToPlayers()

    // JNI: Get handle to player 1 hand for direct access
    external fun getP1HandHandle(): Long

    // JNI: Get handle to player 2 hand for direct access
    external fun getP2HandHandle(): Long

    // JNI: Get handle to board for direct access
    external fun getBoardHandle(): Long

    protected fun finalize() {
        if (nativeHandle != 0L) {
            nativeDestroy(nativeHandle)
            nativeHandle = 0L
        }
    }

    companion object {
        // Player constants to match C++ enum
        const val P1 = 0
        const val P2 = 1
        
        init {
            System.loadLibrary("shkuba")
        }
    }
}