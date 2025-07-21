#include "round.h"

Round::Round(players firstPlayer) : roundDeck(), p1Points(0),p2Points(0), p1Hand(), p2Hand(), m_startCard(roundDeck.draw()),m_firstPlayer(firstPlayer)
{
    
}

int Round::getP1Points()
{
    return p1Points;
}

int Round::getP2Points()
{
    return p2Points;
}

void Round::addToP1Pile(Card cardToAdd)
{
    p1Pile.push_back(cardToAdd);
}

void Round::addToP2Pile(Card cardToAdd)
{
    p2Pile.push_back(cardToAdd);
}

void Round::countPiles()    
{
    int diamonds = 0;
    int sevens = 0;
    int sixes = 0;
    int cards = 0;
   
    cards = p2Pile.size();
    for (int i = 0; i < cards; ++i)
    {
        if (p1Pile[i].getSuit() == Card::D)
        {
            ++diamonds;
            if (p1Pile[i].getRank() == 7) //only for 7 diamond
            {
                ++p1Points;
            }
            else
            {
                ++p2Points;
            }
        }
        if (p1Pile[i].getRank() == 7)
        {
            ++sevens;
        }
        else if (p1Pile[i].getRank() == 6)
        {
            ++sixes;
        }
    }

    if (sevens > 2 || (sevens == 2 && sixes > 2))
    {
        ++p1Points;
    }
    else if (sevens < 2 || (sevens == 2 && sixes < 2))
    {
        ++p2Points;
    }
    if (cards > 20)
    {
        ++p1Points;
    }
    else if (cards < 20)
    {
        ++p2Points;
    }
    if (diamonds > 5)
    {
        ++p1Points;
    }
    else if (diamonds < 5)
    {
        ++p2Points;
    }

}

void Round::firstMiniRound(bool choice)  //aka first mini-round
{
    if (choice == false)
    {
        m_board.addToBoard(m_startCard);
        for (int i = 0; i < NUM_OF_HAND; ++i)
        {
            p1Hand.addToHand(roundDeck.draw());
            p2Hand.addToHand(roundDeck.draw());
            m_board.addToBoard(roundDeck.draw());
        }
    }
    else
    {
        if (m_firstPlayer == P1)
        {
            p1Hand.addToHand(m_startCard);
            p2Hand.addToHand(roundDeck.draw());
        }
        else
        {
            p2Hand.addToHand(m_startCard);
            p1Hand.addToHand(roundDeck.draw());
        }
        for (int i = 0; i < NUM_OF_HAND-1; ++i)
        {
            p1Hand.addToHand(roundDeck.draw());
            p2Hand.addToHand(roundDeck.draw());
        }
        for (int i = 0; i < NUM_OF_BOARD; ++i)
        {
            m_board.addToBoard(roundDeck.draw());
        }

    }
    
}

void Round::giveCardsToPlayers()
{
    for (int i = 0; i < NUM_OF_HAND; ++i)
    {
        p1Hand.addToHand(roundDeck.draw());
        p2Hand.addToHand(roundDeck.draw());
    }


}
