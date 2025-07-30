package com.dinari.shkuba

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

data class GameUIState(
    val isGameActive: Boolean = false,
    val currentPlayer: Int = Round.P1,
    val playerHand: List<CardGui> = emptyList(),
    val botHand: List<CardGui> = emptyList(),
    val tableCards: List<CardGui> = emptyList(),
    val gameScore: Pair<Int, Int> = Pair(0, 0), // (P1, P2)
    val roundScore: Pair<Int, Int> = Pair(0, 0), // (P1, P2)
    val gameMessage: String = "",
    val isPlayerTurn: Boolean = true,
    val gamePhase: GamePhase = GamePhase.NOT_STARTED,
    val winner: String? = null
)

enum class GamePhase {
    NOT_STARTED,
    FIRST_MINI_ROUND,
    PLAYING_CARDS,
    GIVING_CARDS,
    ROUND_COMPLETE,
    GAME_OVER
}

class GameViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GameUIState())
    val uiState: StateFlow<GameUIState> = _uiState.asStateFlow()

    private var game: Game = Game()
    private var currentRound: Round? = null
    private var deck: Deck = Deck()
    private var playerHand: Hand = Hand()
    private var botHand: Hand = Hand()
    private var board: Board = Board()
    private var gameBot: GameBot = GameBot()

    fun startNewGame() {
        viewModelScope.launch {
            // Reset game state
            game = Game()
            startNewRound()
        }
    }

    private suspend fun startNewRound() {
        try {
            // Create new round with current first player
            currentRound = game.createNewRound()
            
            // Create new deck and shuffle
            deck = Deck()
            deck.shuffle()
            
            // Reset hands and board
            playerHand = Hand()
            botHand = Hand()
            board = Board()
            
            updateUIState {
                it.copy(
                    isGameActive = true,
                    gamePhase = GamePhase.FIRST_MINI_ROUND,
                    currentPlayer = game.getFirstPlayer(),
                    gameScore = Pair(game.getP1Points(), game.getP2Points()),
                    gameMessage = "Starting new round..."
                )
            }
            
            // Execute first mini round
            executeFirstMiniRound()
            
        } catch (e: Exception) {
            updateUIState {
                it.copy(gameMessage = "Error starting round: ${e.message}")
            }
        }
    }

    private suspend fun executeFirstMiniRound() {
        try {
            currentRound?.firstMiniRound(true) // Choice parameter - can be made configurable
            
            updateUIState {
                it.copy(
                    gamePhase = GamePhase.PLAYING_CARDS,
                    gameMessage = "First mini round complete. Starting card play..."
                )
            }
            
            // Give initial cards to players
            giveCardsToPlayers()
            
        } catch (e: Exception) {
            updateUIState {
                it.copy(gameMessage = "Error in first mini round: ${e.message}")
            }
        }
    }

    private suspend fun giveCardsToPlayers() {
        try {
            currentRound?.giveCardsToPlayers()
            
            // Deal cards from deck to hands (3 cards each as per NUM_OF_HAND)
            repeat(3) {
                // Deal to player
                val playerCardData = deck.dealCard()
                if (playerCardData.size == 2) {
                    playerHand.addCard(playerCardData[0], playerCardData[1])
                }
                
                // Deal to bot
                val botCardData = deck.dealCard()
                if (botCardData.size == 2) {
                    botHand.addCard(botCardData[0], botCardData[1])
                }
            }
            
            // Add cards to board (4 cards as per NUM_OF_BOARD)
            repeat(4) {
                val cardData = deck.dealCard()
                if (cardData.size == 2) {
                    board.addToBoard(cardData[0], cardData[1])
                }
            }
            
            updateGameUI()
            
            // Start player turns
            if (game.getFirstPlayer() == Round.P1) {
                updateUIState {
                    it.copy(
                        isPlayerTurn = true,
                        gameMessage = "Your turn! Play a card or drop one."
                    )
                }
            } else {
                processBotTurn()
            }
            
        } catch (e: Exception) {
            updateUIState {
                it.copy(gameMessage = "Error giving cards: ${e.message}")
            }
        }
    }

    fun playCard(cardGui: CardGui) {
        viewModelScope.launch {
            if (!_uiState.value.isPlayerTurn || _uiState.value.gamePhase != GamePhase.PLAYING_CARDS) {
                return@launch
            }
            
            try {
                // Find card index in player hand
                val cardIndex = findCardIndexInHand(cardGui, playerHand)
                if (cardIndex == -1) {
                    updateUIState {
                        it.copy(gameMessage = "Card not found in hand")
                    }
                    return@launch
                }
                
                // Try to play the card (simplified - empty cardsToTake for now)
                val result = playerHand.playCard(cardIndex, intArrayOf(), board)
                
                if (result == Hand.STATUS_OK) {
                    updateUIState {
                        it.copy(
                            gameMessage = "Card played successfully!",
                            isPlayerTurn = false
                        )
                    }
                    
                    updateGameUI()
                    checkHandsAndContinue()
                } else {
                    updateUIState {
                        it.copy(gameMessage = "Cannot play this card. Try dropping it instead.")
                    }
                }
                
            } catch (e: Exception) {
                updateUIState {
                    it.copy(gameMessage = "Error playing card: ${e.message}")
                }
            }
        }
    }

    fun dropCard(cardGui: CardGui) {
        viewModelScope.launch {
            if (!_uiState.value.isPlayerTurn || _uiState.value.gamePhase != GamePhase.PLAYING_CARDS) {
                return@launch
            }
            
            try {
                val cardIndex = findCardIndexInHand(cardGui, playerHand)
                if (cardIndex == -1) {
                    updateUIState {
                        it.copy(gameMessage = "Card not found in hand")
                    }
                    return@launch
                }
                
                val result = playerHand.dropCard(cardIndex, board)
                
                if (result == Hand.STATUS_OK) {
                    updateUIState {
                        it.copy(
                            gameMessage = "Card dropped to table",
                            isPlayerTurn = false
                        )
                    }
                    
                    updateGameUI()
                    checkHandsAndContinue()
                } else {
                    updateUIState {
                        it.copy(gameMessage = "Cannot drop card")
                    }
                }
                
            } catch (e: Exception) {
                updateUIState {
                    it.copy(gameMessage = "Error dropping card: ${e.message}")
                }
            }
        }
    }

    private suspend fun processBotTurn() {
        updateUIState {
            it.copy(
                gameMessage = "Bot is thinking...",
                isPlayerTurn = false
            )
        }
        
        delay(1000) // Add delay for better UX
        
        try {
            val botMove = gameBot.makeMove(botHand, board)
            
            when (botMove) {
                is GameBot.BotMove.PlayCard -> {
                    updateUIState {
                        it.copy(gameMessage = "Bot played a card")
                    }
                }
                is GameBot.BotMove.DropCard -> {
                    updateUIState {
                        it.copy(gameMessage = "Bot dropped a card")
                    }
                }
                is GameBot.BotMove.NoMove -> {
                    updateUIState {
                        it.copy(gameMessage = "Bot has no moves")
                    }
                }
            }
            
            updateGameUI()
            checkHandsAndContinue()
            
        } catch (e: Exception) {
            updateUIState {
                it.copy(gameMessage = "Error in bot turn: ${e.message}")
            }
        }
    }

    private suspend fun checkHandsAndContinue() {
        val playerHandSize = playerHand.getHandSize()
        val botHandSize = botHand.getHandSize()
        
        if (playerHandSize == 0 && botHandSize == 0) {
            // Both hands empty - give new cards or end round
            if (deckHasCards()) {
                updateUIState {
                    it.copy(
                        gamePhase = GamePhase.GIVING_CARDS,
                        gameMessage = "Hands empty, dealing new cards..."
                    )
                }
                delay(1000)
                giveCardsToPlayers()
            } else {
                // Deck is empty - end round
                endRound()
            }
        } else {
            // Continue with next player turn
            val nextPlayerTurn = !_uiState.value.isPlayerTurn
            updateUIState {
                it.copy(isPlayerTurn = nextPlayerTurn)
            }
            
            if (!nextPlayerTurn) {
                // Bot's turn
                delay(500)
                processBotTurn()
            } else {
                updateUIState {
                    it.copy(gameMessage = "Your turn!")
                }
            }
        }
    }

    private suspend fun endRound() {
        try {
            currentRound?.countPiles()
            
            val roundP1Points = currentRound?.getP1Points() ?: 0
            val roundP2Points = currentRound?.getP2Points() ?: 0
            
            game.addToP1Points(roundP1Points)
            game.addToP2Points(roundP2Points)
            
            updateUIState {
                it.copy(
                    gamePhase = GamePhase.ROUND_COMPLETE,
                    roundScore = Pair(roundP1Points, roundP2Points),
                    gameScore = Pair(game.getP1Points(), game.getP2Points()),
                    gameMessage = "Round complete! P1: $roundP1Points, P2: $roundP2Points"
                )
            }
            
            delay(2000)
            
            if (game.isGameOver()) {
                endGame()
            } else {
                // Change first player and start new round
                game.changeFirstPlayer()
                startNewRound()
            }
            
        } catch (e: Exception) {
            updateUIState {
                it.copy(gameMessage = "Error ending round: ${e.message}")
            }
        }
    }

    private fun endGame() {
        val winner = game.getWinner()
        updateUIState {
            it.copy(
                gamePhase = GamePhase.GAME_OVER,
                winner = winner,
                gameMessage = "Game Over! Winner: ${winner ?: "Tie"}"
            )
        }
    }

    private fun findCardIndexInHand(cardGui: CardGui, hand: Hand): Int {
        val handSize = hand.getHandSize()
        for (i in 0 until handSize) {
            val handCard = hand.getCardAsNativeCard(i)
            if (handCard != null && CardConverter.cardGuiMatchesNativeCard(cardGui, handCard)) {
                return i
            }
        }
        return -1
    }

    private fun updateGameUI() {
        val playerCards = playerHand.getAllCards().map { CardConverter.nativeCardToCardGui(it) }
        val botCards = botHand.getAllCards().map { CardConverter.nativeCardToCardGui(it) }
        val tableCards = board.getBoard().map { CardConverter.nativeCardToCardGui(it) }
        
        updateUIState { currentState ->
            currentState.copy(
                playerHand = playerCards,
                botHand = botCards,
                tableCards = tableCards
            )
        }
    }

    private fun deckHasCards(): Boolean {
        // Simple check - try to deal a card and see if it succeeds
        return try {
            val cardData = deck.dealCard()
            cardData.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }

    private inline fun updateUIState(update: (GameUIState) -> GameUIState) {
        _uiState.value = update(_uiState.value)
    }
}