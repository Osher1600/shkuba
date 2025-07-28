package com.dinari.shkuba

class Round {
    // Native pointer to the C++ Round instance
    private var nativeHandle: Long = 0

    // Player enum to match C++ players enum
    enum class Player(val value: Int) {
        P1(0),  // Corresponds to P1 in C++
        P2(1)   // Corresponds to P2 in C++
    }

    constructor(firstPlayer: Player) {
        nativeHandle = nativeCreate(firstPlayer.value)
    }

    // JNI: Create C++ Round instance
    private external fun nativeCreate(firstPlayer: Int): Long

    // JNI: Clean up C++ Round instance
    private external fun nativeDestroy(handle: Long)

    // JNI: Get player 1 points
    external fun getP1Points(): Int

    // JNI: Get player 2 points  
    external fun getP2Points(): Int

    // JNI: Initialize first mini round with choice
    // choice: false = put start card on board, true = give start card to first player
    external fun firstMiniRound(choice: Boolean)

    // JNI: Give additional cards to both players
    external fun giveCardsToPlayers()

    // JNI: Count piles and update points
    external fun countPiles()

    // JNI: Get player 1 hand as [suit, rank] pairs
    external fun getP1Hand(): IntArray

    // JNI: Get player 2 hand as [suit, rank] pairs
    external fun getP2Hand(): IntArray

    // JNI: Get board cards as [suit, rank] pairs
    external fun getBoard(): IntArray

    // Helper methods to convert to NativeCard lists
    fun getP1HandAsCards(): List<NativeCard> {
        val handData = getP1Hand()
        val cards = mutableListOf<NativeCard>()
        
        for (i in handData.indices step 2) {
            if (i + 1 < handData.size) {
                cards.add(NativeCard(handData[i], handData[i + 1]))
            }
        }
        return cards
    }

    fun getP2HandAsCards(): List<NativeCard> {
        val handData = getP2Hand()
        val cards = mutableListOf<NativeCard>()
        
        for (i in handData.indices step 2) {
            if (i + 1 < handData.size) {
                cards.add(NativeCard(handData[i], handData[i + 1]))
            }
        }
        return cards
    }

    fun getBoardAsCards(): List<NativeCard> {
        val boardData = getBoard()
        val cards = mutableListOf<NativeCard>()
        
        for (i in boardData.indices step 2) {
            if (i + 1 < boardData.size) {
                cards.add(NativeCard(boardData[i], boardData[i + 1]))
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