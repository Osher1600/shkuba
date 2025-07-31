#pragma once
#include <vector>
#include <random>
#include "card.h"

class Deck {

public:
	Deck();		//constructor
	void shuffleDeck();
	Card draw();
	int getDeckSize();


private:
	std::vector<Card> cards;

};