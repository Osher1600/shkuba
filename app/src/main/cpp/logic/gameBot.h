#pragma once
#include "card.h"
#include "hand.h"


class GameBot
{

public:
	GameBot();
	std::vector<Card> playCard(Hand botHand, Board board); //this will add to the playCard func in Hand class.

private:
	
};
