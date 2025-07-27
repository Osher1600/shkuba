#include "board.h"

Board::Board() : cardsOnBoard()//, p1Shkuba(0),p2Shkuba(0)
{
}

void Board::addToBoard(Card card)
{
	cardsOnBoard.push_back(card);
}

void Board::removeCards(std::vector<int> cardsToTake)
{
	for (int i = 0; i < cardsToTake.size(); ++i)
	{
		cardsOnBoard.erase(cardsOnBoard.begin() + cardsToTake[i]);
	}

}

int Board::getBoardSize()
{
	return cardsOnBoard.size();
}

std::vector<Card> Board::getBoard()
{
	return cardsOnBoard;
}



Card Board::getCardByIndex(int i)
{
	return cardsOnBoard[i];
}
