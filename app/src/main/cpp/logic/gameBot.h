#pragma once
#include "card.h"
#include "hand.h"
#include <numeric> // For std::iota


class GameBot
{

public:
	GameBot();
	void botDropCard(Hand& botHand, Board& board);
	void playCard(Hand botHand, Board board); //this will add to the playCard func in Hand class.
	bool botFirstCardChoice(Card firstCard); //returns true if the bot wants to play first in the first mini-round.

private:
	
};
