#include "deck.h"

Deck::Deck()
{
	for (int i = 1; 1 <= i <= 10; ++i)
	{
		for (int j = 0; j < 4; ++j)
		{
			Card::suit mySuit = static_cast<Card::suit>(j);
			cards.push_back(Card(mySuit, i));
		}
	}
	shuffleDeck();
}

void Deck::shuffleDeck()
{
	std::random_device rd;
	std::mt19937 g(rd());
	std::shuffle(cards.begin(), cards.end(), g);
}

Card Deck::draw()
{
	Card drawCard = cards.back();
	cards.pop_back();
	return drawCard;

}

