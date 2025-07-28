package com.dinari.shkuba

class Board {
    var nativeHandle: Long = 0
        private set

    constructor() {
        nativeHandle = nativeCreate()
    }

    // Constructor that takes an existing native handle (for bot integration)
    constructor(existingHandle: Long) {
        nativeHandle = existingHandle
    }

    fun getBoardSize(): Int {
        return getBoardSizeNative()
    }

    fun addCardToBoard(suit: Int, rank: Int) {
        addToBoard(suit, rank)
    }

    fun getBoardCards(): IntArray {
        return getBoard()
    }

    // Helper method to convert IntArray to NativeCard list
    fun getBoardAsCards(): List<NativeCard> {
        val boardData = getBoardCards()
        val cards = mutableListOf<NativeCard>()
        
        for (i in boardData.indices step 2) {
            if (i + 1 < boardData.size) {
                cards.add(NativeCard(boardData[i], boardData[i + 1]))
            }
        }
        return cards
    }

    fun destroy() {
        nativeDestroy(nativeHandle)
    }

    // Native methods
    private external fun nativeCreate(): Long
    private external fun nativeDestroy(handle: Long)
    private external fun getBoardSizeNative(): Int
    external fun addToBoard(suit: Int, rank: Int)
    external fun getBoard(): IntArray

    companion object {
        init {
            System.loadLibrary("shkuba")
        }
    }
}
