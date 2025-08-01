package com.dinari.shkuba

class Game {
    private var firstPlayer: Int = Round.P1
    private var p1Points: Int = 0
    private var p2Points: Int = 0

    fun changeFirstPlayer() {
        firstPlayer = if (firstPlayer == Round.P1) Round.P2 else Round.P1
    }

    fun addToP1Points(points: Int) {
        p1Points += points
    }

    fun addToP2Points(points: Int) {
        p2Points += points
    }

    fun getFirstPlayer(): Int = firstPlayer
    fun getP1Points(): Int = p1Points
    fun getP2Points(): Int = p2Points

    fun isGameOver(): Boolean {
        return p1Points >= 21 || p2Points >= 21
    }

    fun getWinner(): String? {
        return when {
            p1Points >= 21 && p2Points >= 21 -> {
                if (p1Points > p2Points) "Player 1" else "Player 2"
            }
            p1Points >= 21 -> "Player 1"
            p2Points >= 21 -> "Player 2"
            else -> null
        }
    }

    fun createNewRound(): Round {
        return Round(firstPlayer)
    }
}