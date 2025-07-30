 round logic:
 for a 2 player,  1 human and 1 bot, the "main" should consist the following:
 construct a Round with the first player (to be changed each round)
 construct a deck
 call function "firstMiniRound"
 call functions of turn to each player, for the human it is playCard or dropCard from class Hand according to what he chooses, for gameBot its the func playCard from GameBot
 after first mini round (P1Hand and P2Hand is empty) call func giveCardsToPlayers
 repeat play cards and give cards until deck is empty
 call countPiles and add to player score in game
 change first player after each round in function "changeFirstPlayer"
 if player score is 21 or more game ends and show winner
