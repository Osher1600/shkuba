#pragma once
#include "hand.h"
#include "card.h"
#include "deck.h"
#include "board.h"

const int NUM_OF_HAND = 3;
const int NUM_OF_BOARD = 4;
enum players { P1, P2 };

class Round
{
public:
	Round(players firstPlayer);
	int getP1Points();
	int getP2Points();

	void addToP1Pile(Card cardToAdd);
	void addToP2Pile(Card cardToAdd);

	void countPiles();	//counts both players piles and add the points to the p1/p2Points. there are get functionts for those.
	void firstMiniRound(bool choice);
	void giveCardsToPlayers();

	// Getter methods for accessing game components
	Hand& getP1Hand() { return p1Hand; }
	Hand& getP2Hand() { return p2Hand; }
	Board& getBoard() { return m_board; }


private:
	Deck roundDeck;
	int p1Points;
	int p2Points;

	Hand p1Hand;
	Hand p2Hand;
	std::vector<Card> p1Pile;
	std::vector<Card> p2Pile; //this is the computer (machine) pile. in future versions we may want to have 2v2
	Board m_board;
	
	Card m_startCard;
	players m_firstPlayer;
};