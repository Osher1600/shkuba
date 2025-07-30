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
    int p1Diamonds = 0;
    int p1Sevens = 0;
    int p1Sixes = 0;
    int p1Cards = p1Pile.size();
    
    int p2Diamonds = 0;
    int p2Sevens = 0;
    int p2Sixes = 0;
    int p2Cards = p2Pile.size();
   
    // Count P1 pile
    for (int i = 0; i < p1Cards; ++i)
    {
        if (p1Pile[i].getSuit() == Card::D)
        {
            ++p1Diamonds;
            if (p1Pile[i].getRank() == 7) // 7 of diamonds gives P1 a point
            {
                ++p1Points;
            }
        }
        if (p1Pile[i].getRank() == 7)
        {
            ++p1Sevens;
        }
        else if (p1Pile[i].getRank() == 6)
        {
            ++p1Sixes;
        }
    }

    // Count P2 pile  
    for (int i = 0; i < p2Cards; ++i)
    {
        if (p2Pile[i].getSuit() == Card::D)
        {
            ++p2Diamonds;
        }
        if (p2Pile[i].getRank() == 7)
        {
            ++p2Sevens;
        }
        else if (p2Pile[i].getRank() == 6)
        {
            ++p2Sixes;
        }
    }

    // Total counts for comparison
    int totalSevens = p1Sevens + p2Sevens;
    int totalSixes = p1Sixes + p2Sixes;
    int totalCards = p1Cards + p2Cards;
    int totalDiamonds = p1Diamonds + p2Diamonds;

    // Award points based on who has more
    if (totalSevens > 2 || (totalSevens == 2 && totalSixes > 2))
    {
        if (p1Sevens > p2Sevens || (p1Sevens == p2Sevens && p1Sixes > p2Sixes))
        {
            ++p1Points;
        }
        else
        {
            ++p2Points;
        }
    }
    
    if (totalCards > 20)
    {
        if (p1Cards > p2Cards)
        {
            ++p1Points;
        }
        else
        {
            ++p2Points;
        }
    }
    
    if (totalDiamonds > 5)
    {
        if (p1Diamonds > p2Diamonds)
        {
            ++p1Points;
        }
        else
        {
            ++p2Points;
        }
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
