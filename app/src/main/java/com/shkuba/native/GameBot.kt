package com.shkuba.native

/**
 * Native wrapper for C++ GameBot class - sophisticated AI opponent
 */
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

    // JNI: Bot drops lowest card when no strategy available
    external fun botDropCard(handHandle: Long, boardHandle: Long)

    // JNI: Bot plays card with sophisticated strategy
    external fun playCard(handHandle: Long, boardHandle: Long)

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