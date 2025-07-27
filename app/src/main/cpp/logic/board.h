#pragma once
#include "deck.h"

class Board {
public:
	Board();
	void addToBoard(Card card);
	void removeCards(std::vector<int> cardsToTake);
	int getBoardSize();
	std::vector<Card> getBoard();
	Card getCardByIndex(int i);

private:

	std::vector<Card> cardsOnBoard;
};