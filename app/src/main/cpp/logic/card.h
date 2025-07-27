#pragma once



class Card {

public:

	enum suit{S,H,D,C}; //S = spades , H = hearts , D = diamonds , C = clubs

	Card(suit mySuit,int myRank);
	suit getSuit() const;
	int getRank() const;



private:

	suit m_suit;
	int m_rank;



	
};