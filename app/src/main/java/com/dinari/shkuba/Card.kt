package com.dinari.shkuba

class NativeCard(suit: Int, rank: Int) {
    // Native pointer to the C++ Card instance
    private var nativeHandle: Long = 0

    init {
        try {
            nativeHandle = nativeCreate(suit, rank)
        } catch (e: UnsatisfiedLinkError) {
            android.util.Log.e("NativeCard", "Failed to create native card: ${e.message}")
            nativeHandle = 0
        }
    }

    // Secondary constructor for convenience
    constructor(suit: Suit, rank: Int) : this(suit.ordinal, rank)

    // JNI: Create C++ Card instance
    private external fun nativeCreate(suit: Int, rank: Int): Long

    // JNI: Clean up C++ Card instance
    private external fun nativeDestroy(handle: Long)

    // JNI: Get card suit
    private external fun nativeGetSuit(): Int

    // JNI: Get card rank
    private external fun nativeGetRank(): Int

    fun getSuit(): Int {
        return try {
            if (nativeHandle != 0L) nativeGetSuit() else 0
        } catch (e: UnsatisfiedLinkError) {
            0 // Default to SPADES
        }
    }
    
    fun getRank(): Int {
        return try {
            if (nativeHandle != 0L) nativeGetRank() else 1
        } catch (e: UnsatisfiedLinkError) {
            1 // Default to ACE
        }
    }

    // Helper functions for Kotlin convenience
    fun getSuitEnum(): Suit = Suit.entries[getSuit()]

    override fun toString(): String = "${getSuitEnum().name}${getRank()}"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NativeCard) return false
        return this.getSuit() == other.getSuit() && this.getRank() == other.getRank()
    }

    override fun hashCode(): Int = getSuit() * 31 + getRank()

    protected fun finalize() {
        if (nativeHandle != 0L) {
            nativeDestroy(nativeHandle)
            nativeHandle = 0L
        }
    }

    enum class Suit {
        SPADES,   // S = 0
        HEARTS,   // H = 1
        DIAMONDS, // D = 2
        CLUBS     // C = 3
    }

    companion object {
        // Library loaded in MainActivity
    }
}
