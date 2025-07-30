#pragma once
#include <vector>
#include <random>
#include "card.h"

class Deck {

public:
	Deck();		//constructor
	void shuffleDeck();
	Card draw();
	bool isEmpty() const { return cards.empty(); }


private:
	std::vector<Card> cards;

};