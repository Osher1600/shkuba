package com.dinari.shkuba

class Board {
    private var nativeHandle: Long = 0

    init {
        nativeHandle = nativeCreate()
    }

    // Expose native handle for JNI interactions
    val nativeHandle: Long get() = this.nativeHandle

    fun getBoardSize(): Int {
        return getBoardSizeNative()
    }

    fun addToBoard(suit: Int, rank: Int) {
        addToBoardNative(suit, rank)
    }

    fun addToBoard(card: NativeCard) {
        addToBoardNative(card.getSuit(), card.getRank())
    }

    fun getBoard(): List<NativeCard> {
        val boardData = getBoardNative()
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
    private external fun addToBoardNative(suit: Int, rank: Int)
    private external fun getBoardNative(): IntArray

    companion object {
        init {
            System.loadLibrary("shkuba")
        }
    }
}
