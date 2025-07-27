package com.shkuba.native

import com.shkuba.GameCard

/**
 * Native Game Engine that interfaces with C++ game logic
 */
class GameEngine {
    private val playerHand = Hand()
    private val aiHand = Hand()
    private val board = Board()
    private val deck = Deck()
    
    private var currentPlayerIndex = 0
    private var gameInitialized = false
    
    init {
        System.loadLibrary("shkuba")
    }
    
    fun initializeGame(): Triple<List<GameCard>, List<GameCard>, List<GameCard>> {
        if (gameInitialized) return getCurrentGameState()
        
        // Shuffle deck
        deck.shuffle()
        
        // Deal initial cards - simplified version of the C++ Round logic
        val playerCards = mutableListOf<GameCard>()
        val aiCards = mutableListOf<GameCard>()
        val tableCards = mutableListOf<GameCard>()
        
        // Deal 4 cards to each player
        repeat(4) {
            val playerCardData = deck.dealCard()
            if (playerCardData.size >= 2) {
                playerHand.addCard(playerCardData[0], playerCardData[1])
                playerCards.add(GameCard(playerCardData[0], playerCardData[1]))
            }
            
            val aiCardData = deck.dealCard()
            if (aiCardData.size >= 2) {
                aiHand.addCard(aiCardData[0], aiCardData[1])
                aiCards.add(GameCard(aiCardData[0], aiCardData[1]))
            }
        }
        
        // Deal 4 cards to table
        repeat(4) {
            val tableCardData = deck.dealCard()
            if (tableCardData.size >= 2) {
                board.addToBoardNative(tableCardData[0], tableCardData[1])
                tableCards.add(GameCard(tableCardData[0], tableCardData[1]))
            }
        }
        
        gameInitialized = true
        currentPlayerIndex = 0
        
        return Triple(playerCards, aiCards, tableCards)
    }
    
    fun getCurrentGameState(): Triple<List<GameCard>, List<GameCard>, List<GameCard>> {
        val playerCards = mutableListOf<GameCard>()
        val aiCards = mutableListOf<GameCard>()
        
        // Get player hand
        repeat(playerHand.getHandSize()) { index ->
            val cardData = playerHand.getCardByIndex(index)
            if (cardData.size >= 2) {
                playerCards.add(GameCard(cardData[0], cardData[1]))
            }
        }
        
        // Get AI hand
        repeat(aiHand.getHandSize()) { index ->
            val cardData = aiHand.getCardByIndex(index)
            if (cardData.size >= 2) {
                aiCards.add(GameCard(cardData[0], cardData[1]))
            }
        }
        
        // Get table cards
        val tableCards = mutableListOf<GameCard>()
        val boardData = board.getBoardCards()
        for (i in boardData.indices step 2) {
            if (i + 1 < boardData.size) {
                tableCards.add(GameCard(boardData[i], boardData[i + 1]))
            }
        }
        
        return Triple(playerCards, aiCards, tableCards)
    }
    
    fun playCard(cardToPlay: GameCard): Boolean {
        if (currentPlayerIndex != 0) return false
        
        // Find the card in player's hand
        val playerHandSize = playerHand.getHandSize()
        var cardIndex = -1
        
        for (i in 0 until playerHandSize) {
            val cardData = playerHand.getCardByIndex(i)
            if (cardData.size >= 2 && cardData[0] == cardToPlay.suit && cardData[1] == cardToPlay.rank) {
                cardIndex = i
                break
            }
        }
        
        if (cardIndex == -1) return false
        
        // For now, just drop the card to the board (simplified)
        // In real Shkuba, there would be complex matching logic
        val result = playerHand.dropCard(cardIndex, board.nativeHandle)
        
        if (result == 0) { // STATUS_OK
            currentPlayerIndex = 1
            return true
        }
        
        return false
    }
    
    fun executeAITurn(): Boolean {
        if (currentPlayerIndex != 1) return false
        
        val aiHandSize = aiHand.getHandSize()
        if (aiHandSize == 0) return false
        
        // Simple AI: drop the first card
        val result = aiHand.dropCard(0, board.nativeHandle)
        
        if (result == 0) { // STATUS_OK
            currentPlayerIndex = 0
            return true
        }
        
        return false
    }
    
    fun getCurrentPlayer(): Int = currentPlayerIndex
    
    fun isGameOver(): Boolean {
        return playerHand.getHandSize() == 0 || aiHand.getHandSize() == 0
    }
    
    fun getWinner(): Int? {
        return when {
            playerHand.getHandSize() == 0 -> 0
            aiHand.getHandSize() == 0 -> 1
            else -> null
        }
    }
}