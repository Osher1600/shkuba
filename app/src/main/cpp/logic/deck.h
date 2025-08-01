#pragma once
#include <vector>
#include <random>
#include "card.h"

class Deck {

public:
	Deck();		//constructor
	
	Card draw();
	int getDeckSize();


private:
	std::vector<Card> cards;
	void shuffleDeck();
};