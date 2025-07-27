#include "deck.h"
#include <algorithm>
#include <random>

Deck::Deck()
{
    for (int suit = 0; suit < 4; ++suit)
    {
        for (int rank = 1; rank <= 13; ++rank)
        {
            m_cards.push_back(Card(static_cast<Card::suit>(suit), rank));
        }
    }
    shuffleDeck();
}

void Deck::shuffleDeck()
{
    std::random_device rd;
    std::mt19937 gen(rd());
    std::shuffle(m_cards.begin(), m_cards.end(), gen);
}

Card Deck::draw()
{
    if (m_cards.empty())
    {
        throw std::runtime_error("Cannot draw from empty deck");
    }
    Card card = m_cards.back();
    m_cards.pop_back();
    return card;
}