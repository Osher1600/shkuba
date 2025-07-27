#include "hand.h"

Hand::Hand()
{
}

Hand::status Hand::playCard(int cardIndex, std::vector<int> cardsToTake, Board& myBoard)
{
    if (cardIndex < 0 || cardIndex >= cardsInHand.size())
    {
        return STATUS_ERROR_NOT_FIT;
    }

    Card playedCard = cardsInHand[cardIndex];
    
    // Add played card to board pile (for the player who played it)
    // Remove cards from board that were taken
    myBoard.removeCards(cardsToTake);
    
    // Remove the played card from hand
    cardsInHand.erase(cardsInHand.begin() + cardIndex);
    
    return STATUS_OK;
}

Hand::status Hand::dropCard(int cardIndex, Board& myBoard)
{
    if (cardIndex < 0 || cardIndex >= cardsInHand.size())
    {
        return STATUS_ERROR_NOT_FIT;
    }

    Card droppedCard = cardsInHand[cardIndex];
    myBoard.addToBoard(droppedCard);
    cardsInHand.erase(cardsInHand.begin() + cardIndex);
    
    return STATUS_OK;
}

void Hand::addToHand(Card cardToAdd)
{
    cardsInHand.push_back(cardToAdd);
}

Card Hand::getCardByIndex(int i) const
{
    return cardsInHand[i];
}

int Hand::getHandSize() const
{
    return cardsInHand.size();
}