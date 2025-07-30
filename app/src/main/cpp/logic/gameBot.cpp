#include "gameBot.h"

GameBot::GameBot()
{
}

void GameBot::botDropCard(Hand& botHand, Board& board)	//will improve later
{
	int minCard = 0;
	for (int i = 1; i < botHand.getHandSize(); ++i)
	{
		if (botHand.getCardByIndex(i).getRank() < botHand.getCardByIndex(minCard).getRank())
		{
			minCard = i;
		}
	}
	botHand.dropCard(minCard,board);
}

void GameBot::playCard(Hand& botHand, Board& board)
{
	// Simplified bot logic: try to match cards or drop lowest
	if (board.getBoardSize() == 0) {
		botDropCard(botHand, board);
		return;
	}

	// First try to find exact matches (card rank equals board card rank)
	for (int handIdx = 0; handIdx < botHand.getHandSize(); ++handIdx) {
		Card handCard = botHand.getCardByIndex(handIdx);
		
		for (int boardIdx = 0; boardIdx < board.getBoardSize(); ++boardIdx) {
			Card boardCard = board.getCardByIndex(boardIdx);
			
			if (handCard.getRank() == boardCard.getRank()) {
				// Found a match, play it
				std::vector<int> cardsToTake = { boardIdx };
				Hand::status result = botHand.playCard(handIdx, cardsToTake, board);
				if (result == Hand::STATUS_OK) {
					return; // Successfully played a card
				}
			}
		}
	}

	// Try to find combinations that sum to a hand card
	for (int handIdx = 0; handIdx < botHand.getHandSize(); ++handIdx) {
		Card handCard = botHand.getCardByIndex(handIdx);
		int targetSum = handCard.getRank();
		
		// Try all possible combinations of board cards
		int boardSize = board.getBoardSize();
		if (boardSize > 1) {
			// Check pairs first
			for (int i = 0; i < boardSize; ++i) {
				for (int j = i + 1; j < boardSize; ++j) {
					int sum = board.getCardByIndex(i).getRank() + board.getCardByIndex(j).getRank();
					if (sum == targetSum) {
						std::vector<int> cardsToTake = { i, j };
						Hand::status result = botHand.playCard(handIdx, cardsToTake, board);
						if (result == Hand::STATUS_OK) {
							return; // Successfully played a card
						}
					}
				}
			}
		}
	}

	// If no matches found, drop the lowest card
	botDropCard(botHand, board);
}
