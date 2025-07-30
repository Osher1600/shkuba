#include "hand.h"

Hand::Hand()
{
}

Hand::status Hand::playCard(int cardIndex, std::vector<int> cardsToTake, Board& myBoard) //to implement in game: pile+=cards from hand and board
{
	//checks:
	int sumCards = 0;
	for (int i = 1; i < cardsToTake.size(); ++i)
	{
		sumCards += myBoard.getCardByIndex(cardsToTake[i]).getRank();
	}
	if (sumCards != cardsInHand[cardIndex].getRank())
	{
		return STATUS_ERROR_NOT_FIT;
	}

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


