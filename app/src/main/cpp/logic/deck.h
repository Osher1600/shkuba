#pragma once
#include <vector>
#include "card.h"

class Deck {
public:
	Deck();
	void shuffleDeck();
	Card draw();

private:
	std::vector<Card> m_cards;
};