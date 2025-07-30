package com.dinari.shkuba

class GameManager {
    private var round: Round? = null
    private var gameBot: GameBot? = null
    private var currentPlayerTurn = Round.P1 // Player 1 starts
    private var gamePhase = GamePhase.SETUP
    private var totalP1Score = 0
    private var totalP2Score = 0
    private var isRoundComplete = false
    
    enum class GamePhase {
        SETUP,           // Initial game setup
        FIRST_CHOICE,    // First mini-round choice
        PLAYING,         // Main game playing
        ROUND_END,       // Round finished, counting points
        GAME_END         // Game finished (one player reached winning score)
    }

    data class GameStatus(
        val playerCards: List<CardGui>,
        val botCards: List<CardGui>,
        val tableCards: List<CardGui>,
        val isPlayerTurn: Boolean,
        val gamePhase: GamePhase,
        val p1Score: Int,
        val p2Score: Int,
        val totalP1Score: Int,
        val totalP2Score: Int,
        val winner: Int? = null, // null = no winner, 0 = P1, 1 = P2
        val message: String = ""
    )

    fun startNewGame() {
        // Start new round with P1 going first
        round = Round(Round.P1)
        gameBot = GameBot()
        currentPlayerTurn = Round.P1
        gamePhase = GamePhase.FIRST_CHOICE
        totalP1Score = 0
        totalP2Score = 0
        isRoundComplete = false
    }

    fun startNewRound(firstPlayer: Int) {
        round = Round(firstPlayer)
        if (gameBot == null) {
            gameBot = GameBot()
        }
        currentPlayerTurn = firstPlayer
        gamePhase = GamePhase.FIRST_CHOICE
        isRoundComplete = false
    }

    fun makeFirstChoice(choice: Boolean) {
        round?.let { r ->
            r.firstMiniRound(choice)
            gamePhase = GamePhase.PLAYING
            // If it's bot's turn after first choice, make bot move
            if (currentPlayerTurn == Round.P2) {
                makeBotMove()
            }
        }
    }

    fun playCard(cardIndex: Int, cardsToTake: IntArray): Boolean {
        round?.let { r ->
            if (gamePhase != GamePhase.PLAYING) return false
            if (currentPlayerTurn != Round.P1) return false

            val status = r.playCard(Round.P1, cardIndex, cardsToTake)
            if (status == Round.STATUS_OK) {
                // Add the played card and captured cards to player's pile
                // The C++ logic should handle this, but we need to make sure
                
                // Check if round is complete (no cards in hands)
                checkRoundCompletion(r)
                
                // Switch to bot turn
                currentPlayerTurn = Round.P2
                makeBotMove()
                return true
            }
        }
        return false
    }

    fun dropCard(cardIndex: Int): Boolean {
        round?.let { r ->
            if (gamePhase != GamePhase.PLAYING) return false
            if (currentPlayerTurn != Round.P1) return false

            val status = r.dropCard(Round.P1, cardIndex)
            if (status == Round.STATUS_OK) {
                // Check if round is complete
                checkRoundCompletion(r)
                
                // Switch to bot turn
                currentPlayerTurn = Round.P2
                makeBotMove()
                return true
            }
        }
        return false
    }

    private fun checkRoundCompletion(round: Round) {
        val p1HandEmpty = round.getP1HandCards().isEmpty()
        val p2HandEmpty = round.getP2HandCards().isEmpty()
        
        if (p1HandEmpty && p2HandEmpty) {
            endRound()
        } else if (p1HandEmpty || p2HandEmpty) {
            // Try to give more cards if deck is not empty
            // For now, assume deck might be empty and end round
            // In full implementation, you'd check deck status
            try {
                round.giveCardsToPlayers()
            } catch (e: Exception) {
                // Deck might be empty, end round
                endRound()
            }
        }
    }

    private fun makeBotMove() {
        round?.let { r ->
            try {
                gameBot?.makeMove(r)
                
                // Check if round is complete
                checkRoundCompletion(r)
                
                // Switch back to player turn
                currentPlayerTurn = Round.P1
            } catch (e: Exception) {
                // Handle bot move error, maybe just switch turns
                currentPlayerTurn = Round.P1
            }
        }
    }

    private fun endRound() {
        round?.let { r ->
            r.countPiles()
            val p1RoundScore = r.getP1Points()
            val p2RoundScore = r.getP2Points()
            
            totalP1Score += p1RoundScore
            totalP2Score += p2RoundScore
            
            gamePhase = GamePhase.ROUND_END
            
            // Check for game winner (first to reach a certain score, e.g., 11)
            if (totalP1Score >= 11 || totalP2Score >= 11) {
                gamePhase = GamePhase.GAME_END
            }
        }
    }

    fun getGameStatus(): GameStatus {
        val r = round
        return if (r != null) {
            val winner = when {
                gamePhase == GamePhase.GAME_END && totalP1Score > totalP2Score -> 0
                gamePhase == GamePhase.GAME_END && totalP2Score > totalP1Score -> 1
                else -> null
            }
            
            val message = when (gamePhase) {
                GamePhase.SETUP -> "Starting new game..."
                GamePhase.FIRST_CHOICE -> "Choose whether to take the start card"
                GamePhase.PLAYING -> if (currentPlayerTurn == Round.P1) "Your turn" else "Bot's turn"
                GamePhase.ROUND_END -> "Round complete! P1: ${r.getP1Points()}, P2: ${r.getP2Points()}"
                GamePhase.GAME_END -> "Game Over! Winner: ${if (winner == 0) "You" else "Bot"}"
            }

            GameStatus(
                playerCards = r.getP1HandCards(),
                botCards = r.getP2HandCards(),
                tableCards = r.getBoardCardsAsGui(),
                isPlayerTurn = currentPlayerTurn == Round.P1,
                gamePhase = gamePhase,
                p1Score = r.getP1Points(),
                p2Score = r.getP2Points(),
                totalP1Score = totalP1Score,
                totalP2Score = totalP2Score,
                winner = winner,
                message = message
            )
        } else {
            GameStatus(
                playerCards = emptyList(),
                botCards = emptyList(),
                tableCards = emptyList(),
                isPlayerTurn = true,
                gamePhase = GamePhase.SETUP,
                p1Score = 0,
                p2Score = 0,
                totalP1Score = totalP1Score,
                totalP2Score = totalP2Score,
                message = "Game not started"
            )
        }
    }

    fun continueToNextRound() {
        if (gamePhase == GamePhase.ROUND_END) {
            // Determine who goes first next round (could be winner of last round)
            val nextFirstPlayer = if (round!!.getP1Points() > round!!.getP2Points()) Round.P1 else Round.P2
            startNewRound(nextFirstPlayer)
        }
    }
}