#include "game.h"

void Game::changeFirstPlayer()
{
	if (firstPlayer == P1)
	{
		firstPlayer = P2;
	}
	else
	{
		firstPlayer = P1;
	}
}

void Game::addToP1Points(int points)
{
	p1Points += points;
}

void Game::addToP2Points(int points)
{
	p2Points += points;
}