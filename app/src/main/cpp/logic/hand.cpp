#include "hand.h"
#include <algorithm>

Hand::Hand()
{
}

Hand::status Hand::playCard(int cardIndex, std::vector<int> cardsToTake, Board& myBoard) //to implement in game: pile+=cards from hand and board
{
	//checks:
	int sumCards = 0;
	for (int i = 0; i < cardsToTake.size(); ++i)  // Fixed: start from 0
	{
		if (cardsToTake[i] >= myBoard.getBoardSize()) {
			return STATUS_ERROR_NOT_FIT; // Invalid board index
		}
		sumCards += myBoard.getCardByIndex(cardsToTake[i]).getRank();
	}
	
	if (cardIndex >= cardsInHand.size()) {
		return STATUS_ERROR_NOT_FIT; // Invalid hand index
	}
	
	if (sumCards != cardsInHand[cardIndex].getRank())
	{
		return STATUS_ERROR_NOT_FIT;
	}

	// Check if trying to take multiple cards when single card match exists
	for (int i = 0; i < myBoard.getBoardSize(); ++i)
	{
		if (myBoard.getCardByIndex(i).getRank() == cardsInHand[cardIndex].getRank())
		{
			if (cardsToTake.size() > 1)
			{
				return STATUS_ERROR_CARD_EXIST;
			}
		}
	}
	
	// Remove cards from board (in reverse order to maintain indices)
	std::sort(cardsToTake.begin(), cardsToTake.end(), std::greater<int>());
	for (int idx : cardsToTake) {
		// This will be handled by Board::removeCards if it exists
		// For now, we need to modify the Board class or handle it in Round
	}
	myBoard.removeCards(cardsToTake);
	
	// Remove card from hand
	cardsInHand.erase(cardsInHand.begin()+cardIndex);

	return STATUS_OK;
}

Hand::status Hand::dropCard(int cardIndex, Board& myBoard)
{
	myBoard.addToBoard(cardsInHand[cardIndex]);
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


