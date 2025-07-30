package com.dinari.shkuba

class Round(firstPlayer: Int) {
    // Native pointer to the C++ Round instance
    private var nativeHandle: Long = 0

    // Player constants matching C++ enum
    companion object {
        const val P1 = 0
        const val P2 = 1
        
        // Hand status constants matching C++ enum
        const val STATUS_OK = 0
        const val STATUS_ERROR_NOT_FIT = 1
        const val STATUS_ERROR_CARD_EXIST = 2

        init {
            System.loadLibrary("shkuba")
        }
    }

    init {
        nativeHandle = nativeCreate(firstPlayer)
    }

    // JNI: Create C++ Round instance
    private external fun nativeCreate(firstPlayer: Int): Long

    // JNI: Clean up C++ Round instance
    private external fun nativeDestroy(handle: Long)

    // Get points for player 1
    external fun getP1Points(): Int

    // Get points for player 2
    external fun getP2Points(): Int

    // Initialize first mini-round
    external fun firstMiniRound(choice: Boolean)

    // Give cards to players
    external fun giveCardsToPlayers()

    // Count piles and update points
    external fun countPiles()

    // Get player 1 hand as array of [suit, rank] pairs
    external fun getP1Hand(): IntArray

    // Get player 2 hand as array of [suit, rank] pairs
    external fun getP2Hand(): IntArray

    // Get board cards as array of [suit, rank] pairs
    external fun getBoardCards(): IntArray

    // Play a card (returns status)
    external fun playCard(player: Int, cardIndex: Int, cardsToTake: IntArray): Int

    // Drop a card (returns status)
    external fun dropCard(player: Int, cardIndex: Int): Int

    // Check if deck is empty
    external fun isDeckEmpty(): Boolean

    // Convert raw card data to CardGui objects
    fun intArrayToCards(cardData: IntArray): List<CardGui> {
        val cards = mutableListOf<CardGui>()
        for (i in cardData.indices step 2) {
            if (i + 1 < cardData.size) {
                val suit = when (cardData[i]) {
                    0 -> Suit.Spades
                    1 -> Suit.Hearts
                    2 -> Suit.Diamonds
                    3 -> Suit.Clubs
                    else -> Suit.Spades
                }
                val rank = cardData[i + 1]
                val value = when (rank) {
                    1 -> "A"
                    11 -> "J"
                    12 -> "Q"
                    13 -> "K"
                    else -> rank.toString()
                }
                cards.add(CardGui(value, suit))
            }
        }
        return cards
    }

    // Get player 1 hand as CardGui objects
    fun getP1HandCards(): List<CardGui> = intArrayToCards(getP1Hand())

    // Get player 2 hand as CardGui objects  
    fun getP2HandCards(): List<CardGui> = intArrayToCards(getP2Hand())

    // Get board cards as CardGui objects
    fun getBoardCardsAsGui(): List<CardGui> = intArrayToCards(getBoardCards())

    // Get native handle for GameBot integration
    fun getNativeHandle(): Long = nativeHandle

    protected fun finalize() {
        if (nativeHandle != 0L) {
            nativeDestroy(nativeHandle)
            nativeHandle = 0L
        }
    }
}