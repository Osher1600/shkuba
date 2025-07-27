#include "board.h"
#include <algorithm>

Board::Board()
{
}

void Board::addToBoard(Card card)
{
    cardsOnBoard.push_back(card);
}

void Board::removeCards(std::vector<int> cardsToTake)
{
    std::sort(cardsToTake.rbegin(), cardsToTake.rend());
    for (int index : cardsToTake)
    {
        if (index >= 0 && index < cardsOnBoard.size())
        {
            cardsOnBoard.erase(cardsOnBoard.begin() + index);
        }
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