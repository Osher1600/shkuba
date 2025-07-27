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

void GameBot::playCard(Hand botHand, Board board)
{
	// Implement the logic for the bot to decide which cards to play
	if (board.getBoardSize() == 0)
	{
		botDropCard(botHand, board);
		return; // If no cards on the board, drop the lowest card
	}
	int sumBoard = 0;
	int sevenInHandIndex = -1;
	int sixInHandIndex = -1;
	int sevenOnBoardIndex = -1;
	int sixOnBoardIndex = -1;
	int matchIndexBoardDiamond = -1;
	int matchIndexHandDiamond = -1;
	int matchIndexBoard = -1;
	int matchIndexHand = -1;
	int bestHandIdx = -1;
	std::vector<int> bestCombo;
	for (int i = 0; i < board.getBoardSize(); ++i)
	{
		sumBoard += board.getCardByIndex(i).getRank();

		for (int j = 0; j < botHand.getHandSize(); ++j)
		{
			if (botHand.getCardByIndex(j).getRank() == board.getCardByIndex(i).getRank())
			{
				if (board.getCardByIndex(i).getRank() == 7)
				{
					if (board.getCardByIndex(i).getSuit() == Card::D||botHand.getCardByIndex(j).getSuit()==Card::D) // Prefer 7 of Diamonds if available
					{
						sevenInHandIndex = j;
						sevenOnBoardIndex = i;
					}
					else if (sevenInHandIndex == -1) // If no 7 of Diamonds, just play any 7
					{
						sevenInHandIndex = j;
						sevenOnBoardIndex = i;
					}
				}
				else if (board.getCardByIndex(i).getRank() == 6)
				{
					if (board.getCardByIndex(i).getSuit() == Card::D|| botHand.getCardByIndex(j).getSuit()==Card::D) // Prefer 6 of Diamonds if available
					{
						sixOnBoardIndex = i;
						sixInHandIndex = j;
					}
				}
				else if (board.getCardByIndex(i).getSuit() == Card::D || botHand.getCardByIndex(j).getSuit() == Card::D)
				{
					matchIndexBoardDiamond = i; // Match found for Diamonds on board
					matchIndexHandDiamond = j; // Match found for Diamonds in hand
				}
				else
				{
					matchIndexBoard = i; // Match found for other suits on board
					matchIndexHand = j; // Match found for other suits in hand
				}
				
				int maxComboSize = 0;
				bool foundComboWith7InHand = false;
				bool foundComboWith7OnBoard = false;

				for (int handIdx = 0; handIdx < botHand.getHandSize(); ++handIdx) {
					Card handCard = botHand.getCardByIndex(handIdx);
					int boardSize = board.getBoardSize();
					int combos = 1 << boardSize;

					for (int mask = 1; mask < combos; ++mask) {
						std::vector<int> combo;
						int sum = 0;
						bool comboHas7OnBoard = false;
						for (int b = 0; b < boardSize; ++b) {
							if (mask & (1 << b)) {
								Card boardCard = board.getCardByIndex(b);
								sum += boardCard.getRank();
								combo.push_back(b);
								if (boardCard.getRank() == 7) {
									comboHas7OnBoard = true;
								}
							}
						}
						if (combo.size() <= 1) continue;

						if (sum == handCard.getRank()) {
							// 1priority: 7 in hand	
							if (handCard.getRank() == 7) {
								if (!foundComboWith7InHand || combo.size() > maxComboSize) {
									bestHandIdx = handIdx;
									bestCombo = combo;
									maxComboSize = combo.size();
									foundComboWith7InHand = true;
								}
							}
							// 2priority: 7 on board 
							else if (comboHas7OnBoard) {
								if (!foundComboWith7InHand && (!foundComboWith7OnBoard || combo.size() > maxComboSize)) {
									bestHandIdx = handIdx;
									bestCombo = combo;
									maxComboSize = combo.size();
									foundComboWith7OnBoard = true;
								}
							}
							// 3priority: other combos
							else if (!foundComboWith7InHand && !foundComboWith7OnBoard) {
								if (combo.size() > maxComboSize) {
									bestHandIdx = handIdx;
									bestCombo = combo;
									maxComboSize = combo.size();
								}
							}
							else if (!foundComboWith7InHand && !foundComboWith7OnBoard && combo.size() > maxComboSize) {
								bestHandIdx = handIdx;
								bestCombo = combo;
								maxComboSize = combo.size();
							}
						}
					}
				}
				for (int i = 0; i < botHand.getHandSize(); ++i) 
				{
					if (sumBoard == botHand.getCardByIndex(i).getRank()) 
					{
						std::vector<int> v(i);
						std::iota(v.begin(), v.end(), 1);
						botHand.playCard(j, v, board); // Play the card that matches the board
						return; // Exit after playing a card
					}
				}
				if (sevenInHandIndex != -1) 
				{
					botHand.playCard(sevenInHandIndex, { sevenOnBoardIndex }, board); // Play the 7 that matches the board
					return;
				}
				else if (bestHandIdx != -1) {
					botHand.playCard(bestHandIdx, bestCombo, board);
					return;
				}
				else if (sixInHandIndex != -1)
				{
					botHand.playCard(sixInHandIndex, { sixOnBoardIndex }, board); // Play the 6 that matches the board
					return;
				}
				else if(matchIndexBoardDiamond != -1) 
				{
					botHand.playCard(matchIndexHandDiamond, { matchIndexBoardDiamond }, board); // Play the card that matches Diamonds
					return;
				}
				else if (matchIndexBoard != -1) 
				{
					botHand.playCard(matchIndexHand, { matchIndexBoard }, board); // Play the card that matches other suits
					return;
				}
				else 
				{
					botDropCard(botHand, board); // If no matches found, drop the lowest card
				}
				
				
			}
		}

		
	}
	
}