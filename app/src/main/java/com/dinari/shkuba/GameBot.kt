package com.dinari.shkuba

class GameBot {
    // Native pointer to the C++ GameBot instance
    private var nativeHandle: Long = 0

    init {
        nativeHandle = nativeCreate()
    }

    // JNI: Create C++ GameBot instance
    private external fun nativeCreate(): Long

    // JNI: Clean up C++ GameBot instance
    private external fun nativeDestroy(handle: Long)

    // TODO: Add methods for botDropCard and playCard when JNI is implemented
    // For now, we'll implement game logic in Kotlin

    fun makeMove(hand: Hand, board: Board): BotMove {
        // Simple bot logic: try to play a card, otherwise drop one
        val handSize = hand.getHandSize()
        if (handSize == 0) {
            return BotMove.NoMove
        }

        // Try to play first card (simplified logic)
        val cardIndex = 0
        val cardsToTake = intArrayOf() // Empty for now
        val result = hand.playCard(cardIndex, cardsToTake, board.nativeHandle)
        
        return if (result == Hand.STATUS_OK) {
            BotMove.PlayCard(cardIndex, cardsToTake.toList())
        } else {
            // Try to drop the card instead
            hand.dropCard(cardIndex, board.nativeHandle)
            BotMove.DropCard(cardIndex)
        }
    }

    protected fun finalize() {
        if (nativeHandle != 0L) {
            nativeDestroy(nativeHandle)
            nativeHandle = 0L
        }
    }

    sealed class BotMove {
        object NoMove : BotMove()
        data class PlayCard(val cardIndex: Int, val cardsToTake: List<Int>) : BotMove()
        data class DropCard(val cardIndex: Int) : BotMove()
    }

    companion object {
        init {
            System.loadLibrary("shkuba")
        }
    }
}