package com.shkuba.native

import com.shkuba.GameCard

/**
 * Native Game Engine using sophisticated C++ Round and GameBot logic
 */
class GameEngine {
    private var round: Round? = null
    private var gameBot: GameBot? = null
    private var currentPlayerIndex = 0
    private var gameInitialized = false
    
    // Handles for direct access to Round components
    private var p1HandHandle: Long = 0
    private var p2HandHandle: Long = 0
    private var boardHandle: Long = 0
    
    init {
        System.loadLibrary("shkuba")
    }
    
    fun initializeGame(): Triple<List<GameCard>, List<GameCard>, List<GameCard>> {
        if (gameInitialized) return getCurrentGameState()
        
        // Create Round starting with player 1
        round = Round(Round.P1)
        gameBot = GameBot()
        
        // Initialize the first mini-round
        // choice = false means start card goes to board, not to a player
        round?.firstMiniRound(false)
        
        // Get handles for direct access to components
        p1HandHandle = round?.getP1HandHandle() ?: 0
        p2HandHandle = round?.getP2HandHandle() ?: 0  
        boardHandle = round?.getBoardHandle() ?: 0
        
        gameInitialized = true
        currentPlayerIndex = 0 // Player 1 starts
        
        return getCurrentGameState()
    }
    
    fun getCurrentGameState(): Triple<List<GameCard>, List<GameCard>, List<GameCard>> {
        val playerCards = mutableListOf<GameCard>()
        val aiCards = mutableListOf<GameCard>()
        val tableCards = mutableListOf<GameCard>()
        
        if (!gameInitialized || round == null) {
            return Triple(playerCards, aiCards, tableCards)
        }
        
        // Get player 1 hand cards
        if (p1HandHandle != 0L) {
            val p1Hand = createHandWrapper(p1HandHandle)
            repeat(p1Hand.getHandSize()) { index ->
                val cardData = p1Hand.getCardByIndex(index)
                if (cardData.size >= 2) {
                    playerCards.add(GameCard(cardData[0], cardData[1]))
                }
            }
        }
        
        // Get player 2 (AI) hand cards 
        if (p2HandHandle != 0L) {
            val p2Hand = createHandWrapper(p2HandHandle)
            repeat(p2Hand.getHandSize()) { index ->
                val cardData = p2Hand.getCardByIndex(index)
                if (cardData.size >= 2) {
                    aiCards.add(GameCard(cardData[0], cardData[1]))
                }
            }
        }
        
        // Get board cards
        if (boardHandle != 0L) {
            val board = createBoardWrapper(boardHandle)
            val boardData = board.getBoardCards()
            for (i in boardData.indices step 2) {
                if (i + 1 < boardData.size) {
                    tableCards.add(GameCard(boardData[i], boardData[i + 1]))
                }
            }
        }
        
        return Triple(playerCards, aiCards, tableCards)
    }
    
    fun playCard(cardToPlay: GameCard): Boolean {
        if (currentPlayerIndex != 0 || !gameInitialized || p1HandHandle == 0L || boardHandle == 0L) {
            return false
        }
        
        val p1Hand = createHandWrapper(p1HandHandle)
        
        // Find the card in player's hand
        val playerHandSize = p1Hand.getHandSize()
        var cardIndex = -1
        
        for (i in 0 until playerHandSize) {
            val cardData = p1Hand.getCardByIndex(i)
            if (cardData.size >= 2 && cardData[0] == cardToPlay.suit && cardData[1] == cardToPlay.rank) {
                cardIndex = i
                break
            }
        }
        
        if (cardIndex == -1) return false
        
        // Check if player can capture cards with this card
        val capturedCards = findCaptureOpportunities(cardToPlay, boardHandle)
        
        val result = if (capturedCards.isNotEmpty()) {
            // Play card with capturing
            p1Hand.playCard(cardIndex, capturedCards, boardHandle)
        } else {
            // Just drop the card to board
            p1Hand.dropCard(cardIndex, boardHandle)
        }
        
        if (result == 0) { // STATUS_OK
            currentPlayerIndex = 1
            return true
        }
        
        return false
    }
    
    fun executeAITurn(): Boolean {
        if (currentPlayerIndex != 1 || !gameInitialized || gameBot == null || 
            p2HandHandle == 0L || boardHandle == 0L) {
            return false
        }
        
        val p2Hand = createHandWrapper(p2HandHandle)
        val aiHandSize = p2Hand.getHandSize()
        if (aiHandSize == 0) return false
        
        // Use sophisticated GameBot logic
        gameBot?.playCard(p2HandHandle, boardHandle)
        
        currentPlayerIndex = 0
        return true
    }
    
    fun getCurrentPlayer(): Int = currentPlayerIndex
    
    fun isGameOver(): Boolean {
        if (!gameInitialized || p1HandHandle == 0L || p2HandHandle == 0L) return false
        
        val p1Hand = createHandWrapper(p1HandHandle)
        val p2Hand = createHandWrapper(p2HandHandle)
        
        return p1Hand.getHandSize() == 0 || p2Hand.getHandSize() == 0
    }
    
    fun getWinner(): Int? {
        if (!isGameOver()) return null
        
        val p1Hand = createHandWrapper(p1HandHandle)
        val p2Hand = createHandWrapper(p2HandHandle)
        
        return when {
            p1Hand.getHandSize() == 0 -> 0
            p2Hand.getHandSize() == 0 -> 1
            else -> null
        }
    }
    
    fun getPlayerPoints(): Pair<Int, Int> {
        return if (round != null) {
            Pair(round!!.getP1Points(), round!!.getP2Points())
        } else {
            Pair(0, 0)
        }
    }
    
    // Helper function to find capture opportunities for a card
    private fun findCaptureOpportunities(card: GameCard, boardHandle: Long): IntArray {
        val board = createBoardWrapper(boardHandle)
        val captureIndices = mutableListOf<Int>()
        
        // Simple capture logic - in Shkuba, you can capture cards that sum to your card's rank
        // or exact matches
        val boardSize = board.getBoardSize()
        
        // Check for exact rank matches first
        for (i in 0 until boardSize) {
            val boardCardData = board.getCardByIndex(i)
            if (boardCardData.size >= 2 && boardCardData[1] == card.rank) {
                captureIndices.add(i)
                break // Take first match for simplicity
            }
        }
        
        return captureIndices.toIntArray()
    }
    
    // Helper to create Hand wrapper with existing handle
    private fun createHandWrapper(handle: Long): HandWrapper {
        return HandWrapper(handle)
    }
    
    // Helper to create Board wrapper with existing handle  
    private fun createBoardWrapper(handle: Long): BoardWrapper {
        return BoardWrapper(handle)
    }
    
    // Wrapper classes to work with existing native handles
    private class HandWrapper(private val handle: Long) {
        external fun getHandSize(): Int
        external fun getCardByIndex(index: Int): IntArray
        external fun dropCard(cardIndex: Int, boardHandle: Long): Int
        external fun playCard(cardIndex: Int, cardsToTake: IntArray, boardHandle: Long): Int
        
        // Override the native handle access
        private fun getNativeHandle(): Long = handle
    }
    
    private class BoardWrapper(private val handle: Long) {
        external fun getBoardSize(): Int
        external fun getBoardCards(): IntArray
        external fun getCardByIndex(index: Int): IntArray
        
        // Override the native handle access
        private fun getNativeHandle(): Long = handle
    }
}