#include "card.h"

Card::Card(suit mySuit, int myRank):
    m_suit(mySuit),m_rank(myRank)
{
}

Card::suit Card::getSuit() const
{
    return m_suit;
}

int Card::getRank() const
{
    return m_rank;
}


