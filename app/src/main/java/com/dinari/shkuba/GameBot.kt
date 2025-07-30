package com.dinari.shkuba

class GameBot {
    // Native pointer to the C++ GameBot instance
    private var nativeHandle: Long = 0

    init {
        nativeHandle = nativeCreate()
    }

    // JNI: Create C++ GameBot instance
    private external fun nativeCreate(): Long

    // JNI: Clean up C++ GameBot instance
    private external fun nativeDestroy(handle: Long)

    // Make a move using the bot logic
    external fun makeMove(roundHandle: Long)

    // Make a move with a Round object  
    fun makeMove(round: Round) {
        // We need to pass the Round's native handle to the JNI function
        // For simplicity, let's create a public method in Round to get the handle
        makeMove(round.getNativeHandle())
    }

    protected fun finalize() {
        if (nativeHandle != 0L) {
            nativeDestroy(nativeHandle)
            nativeHandle = 0L
        }
    }

    companion object {
        init {
            System.loadLibrary("shkuba")
        }
    }
}