package com.dinari.shkuba

/**
 * Kotlin wrapper for the C++ GameBot class
 * Provides intelligent bot behavior for the Shkuba card game
 */
class GameBot {
    private var nativeHandle: Long = 0

    init {
        nativeHandle = nativeCreate()
    }

    /**
     * Makes the bot play a card using its intelligent strategy
     * @param hand The bot's hand (should be a Hand object)
     * @param board The game board (should be a Board object)
     */
    fun makeMove(hand: Hand, board: Board) {
        playCard(hand.nativeHandle, board.nativeHandle)
    }

    /**
     * Makes the bot drop a card (when it can't make a valid play)
     * @param hand The bot's hand
     * @param board The game board
     */
    fun dropCard(hand: Hand, board: Board) {
        dropCardNative(hand.nativeHandle, board.nativeHandle)
    }

    protected fun finalize() {
        if (nativeHandle != 0L) {
            nativeDestroy(nativeHandle)
            nativeHandle = 0
        }
    }

    // Native methods
    private external fun nativeCreate(): Long
    private external fun nativeDestroy(handle: Long)
    private external fun playCard(handHandle: Long, boardHandle: Long)
    private external fun dropCardNative(handHandle: Long, boardHandle: Long)
}