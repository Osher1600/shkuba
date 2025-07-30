#pragma once
#include "round.h"

class Game {
public:
	void changeFirstPlayer();
	void addToP1Points(int points);
	void addToP2Points(int points);

private:

	players firstPlayer;
	int p1Points;
	int p2Points;
};

