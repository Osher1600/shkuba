#pragma once
#include <vector>
#include "deck.h"
#include "board.h"



class Hand {
public:
	enum status{STATUS_OK,STATUS_ERROR_NOT_FIT, STATUS_ERROR_CARD_EXIST};
	
	Hand();
	status playCard(int cardIndex, std::vector<int> cardsToTake, Board& myBoard);
	status dropCard(int cardIndex,Board& myBoard);	//now only returns OK, in later versions we will check if possible.
	void addToHand(Card cardToAdd);
	Card getCardByIndex(int i) const;
	int getHandSize() const;
	
	

private:
	std::vector<Card> cardsInHand;

};