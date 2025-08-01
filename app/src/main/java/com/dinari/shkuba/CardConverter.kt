package com.dinari.shkuba

/**
 * Utility class for converting between native C++ cards and UI CardGui objects
 */
object CardConverter {
    
    fun nativeCardToCardGui(nativeCard: NativeCard): CardGui {
        val suit = when (nativeCard.getSuit()) {
            0 -> Suit.Spades  // S
            1 -> Suit.Hearts  // H  
            2 -> Suit.Diamonds // D
            3 -> Suit.Clubs   // C
            else -> Suit.Spades
        }
        
        val value = when (nativeCard.getRank()) {
            1 -> "A"
            11 -> "J"
            12 -> "Q"
            13 -> "K"
            else -> nativeCard.getRank().toString()
        }
        
        return CardGui(value, suit)
    }
    
    fun cardGuiToSuitRank(cardGui: CardGui): Pair<Int, Int> {
        val suit = when (cardGui.suit) {
            is Suit.Spades -> 0    // S
            is Suit.Hearts -> 1    // H
            is Suit.Diamonds -> 2  // D
            is Suit.Clubs -> 3     // C
        }
        
        val rank = when (cardGui.value) {
            "A" -> 1
            "J" -> 11
            "Q" -> 12
            "K" -> 13
            else -> cardGui.value.toIntOrNull() ?: 1
        }
        
        return Pair(suit, rank)
    }
    
    fun cardGuiMatchesNativeCard(cardGui: CardGui, nativeCard: NativeCard): Boolean {
        val (guiSuit, guiRank) = cardGuiToSuitRank(cardGui)
        return guiSuit == nativeCard.getSuit() && guiRank == nativeCard.getRank()
    }
}