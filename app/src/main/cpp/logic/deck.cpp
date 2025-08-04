#include "deck.h"

const int CARDS_RANGE = 10;
const int CARD_RANKS = 4;

Deck::Deck()
{
	for (int i = 0; i < CARDS_RANGE; ++i)
	{
		for (int j = 0; j < CARD_RANKS; ++j)
		{
			Card::suit mySuit = static_cast<Card::suit>(j);
			cards.push_back(Card(mySuit, i));
		}
	}
	shuffleDeck();
}

Card Deck::draw()
{
	if (cards.empty()) {
		// Return a default card if deck is empty to avoid crash
		return Card(Card::S, 1);
	}
	Card drawCard = cards.back();
	cards.pop_back();
	return drawCard;
}
int Deck::getDeckSize()
{
	return cards.size();
}
void Deck::shuffleDeck()
{
	std::random_device rd;
	std::mt19937 g(rd());
	std::shuffle(cards.begin(), cards.end(), g);
}





