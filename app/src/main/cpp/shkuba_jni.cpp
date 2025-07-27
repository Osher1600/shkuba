#include <jni.h>
#include <android/log.h>
#include "logic/board.h"
#include "logic/card.h"
#include "logic/deck.h"
#include "logic/hand.h"
#include "logic/round.h"
#include "logic/gameBot.h"

#define LOG_TAG "ShkubaJNI"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C" {

// Board JNI Methods
JNIEXPORT jlong JNICALL Java_com_shkuba_Board_nativeCreate(JNIEnv* env, jobject thiz) {
    try {
        Board* board = new Board();
        LOGI("Board created successfully");
        return reinterpret_cast<jlong>(board);
    } catch (const std::exception& e) {
        LOGE("Error creating Board: %s", e.what());
        return 0;
    }
}

JNIEXPORT void JNICALL Java_com_shkuba_Board_nativeDestroy(JNIEnv* env, jobject thiz, jlong handle) {
    Board* board = reinterpret_cast<Board*>(handle);
    if (board) {
        delete board;
        LOGI("Board destroyed successfully");
    }
}

JNIEXPORT jint JNICALL Java_com_shkuba_Board_getBoardSize(JNIEnv* env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Board* board = reinterpret_cast<Board*>(handle);
    if (board) {
        return static_cast<jint>(board->getBoardSize());
    }
    return 0;
}

JNIEXPORT void JNICALL Java_com_shkuba_Board_addToBoardJNI(JNIEnv* env, jobject thiz, jint suit, jint rank) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Board* board = reinterpret_cast<Board*>(handle);
    if (board) {
        Card card(static_cast<Card::suit>(suit), rank);
        board->addToBoard(card);
    }
}

JNIEXPORT jintArray JNICALL Java_com_shkuba_Board_getBoardJNI(JNIEnv* env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Board* board = reinterpret_cast<Board*>(handle);

    if (board) {
        std::vector<Card> cards = board->getBoard();
        jintArray result = env->NewIntArray(cards.size() * 2);
        jint* elements = env->GetIntArrayElements(result, nullptr);

        for (size_t i = 0; i < cards.size(); i++) {
            elements[i * 2] = static_cast<jint>(cards[i].getSuit());
            elements[i * 2 + 1] = static_cast<jint>(cards[i].getRank());
        }

        env->ReleaseIntArrayElements(result, elements, 0);
        return result;
    }
    return env->NewIntArray(0);
}

JNIEXPORT jint JNICALL Java_com_shkuba_Board_getBoardSizeNative(JNIEnv* env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Board* board = reinterpret_cast<Board*>(handle);
    if (board) {
        return static_cast<jint>(board->getBoardSize());
    }
    return 0;
}

// Card JNI Methods
JNIEXPORT jlong JNICALL Java_com_shkuba_NativeCard_nativeCreate(JNIEnv* env, jobject thiz, jint suit, jint rank) {
    try {
        Card* card = new Card(static_cast<Card::suit>(suit), rank);
        return reinterpret_cast<jlong>(card);
    } catch (const std::exception& e) {
        LOGE("Error creating Card: %s", e.what());
        return 0;
    }
}

JNIEXPORT void JNICALL Java_com_shkuba_NativeCard_nativeDestroy(JNIEnv* env, jobject thiz, jlong handle) {
    Card* card = reinterpret_cast<Card*>(handle);
    if (card) {
        delete card;
    }
}

JNIEXPORT jint JNICALL Java_com_shkuba_NativeCard_nativeGetSuit(JNIEnv* env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Card* card = reinterpret_cast<Card*>(handle);
    if (card) {
        return static_cast<jint>(card->getSuit());
    }
    return -1;
}

JNIEXPORT jint JNICALL Java_com_shkuba_NativeCard_nativeGetRank(JNIEnv* env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Card* card = reinterpret_cast<Card*>(handle);
    if (card) {
        return card->getRank();
    }
    return -1;
}

// Deck JNI Methods
JNIEXPORT jlong JNICALL Java_com_shkuba_Deck_nativeCreate(JNIEnv* env, jobject thiz) {
    try {
        Deck* deck = new Deck();
        return reinterpret_cast<jlong>(deck);
    } catch (const std::exception& e) {
        LOGE("Error creating Deck: %s", e.what());
        return 0;
    }
}

JNIEXPORT void JNICALL Java_com_shkuba_Deck_nativeDestroy(JNIEnv* env, jobject thiz, jlong handle) {
    Deck* deck = reinterpret_cast<Deck*>(handle);
    if (deck) {
        delete deck;
    }
}

JNIEXPORT void JNICALL Java_com_shkuba_Deck_shuffle(JNIEnv* env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Deck* deck = reinterpret_cast<Deck*>(handle);
    if (deck) {
        deck->shuffleDeck();
    }
}

JNIEXPORT jintArray JNICALL Java_com_shkuba_Deck_dealCard(JNIEnv* env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Deck* deck = reinterpret_cast<Deck*>(handle);

    if (deck) {
        try {
            Card card = deck->draw();
            jintArray result = env->NewIntArray(2);
            jint cardData[2] = {static_cast<jint>(card.getSuit()), static_cast<jint>(card.getRank())};
            env->SetIntArrayRegion(result, 0, 2, cardData);
            return result;
        } catch (const std::exception& e) {
            LOGE("Error dealing card: %s", e.what());
            return env->NewIntArray(0);
        }
    }
    return env->NewIntArray(0);
}

// Hand JNI Methods
JNIEXPORT jlong JNICALL Java_com_shkuba_Hand_nativeCreate(JNIEnv* env, jobject thiz) {
    try {
        Hand* hand = new Hand();
        return reinterpret_cast<jlong>(hand);
    } catch (const std::exception& e) {
        LOGE("Error creating Hand: %s", e.what());
        return 0;
    }
}

JNIEXPORT void JNICALL Java_com_shkuba_Hand_nativeDestroy(JNIEnv* env, jobject thiz, jlong handle) {
    Hand* hand = reinterpret_cast<Hand*>(handle);
    if (hand) {
        delete hand;
    }
}

JNIEXPORT void JNICALL Java_com_shkuba_Hand_addCard(JNIEnv* env, jobject thiz, jint suit, jint rank) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Hand* hand = reinterpret_cast<Hand*>(handle);
    if (hand) {
        Card card(static_cast<Card::suit>(suit), rank);
        hand->addToHand(card);
    }
}

JNIEXPORT jint JNICALL Java_com_shkuba_Hand_getHandSize(JNIEnv* env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Hand* hand = reinterpret_cast<Hand*>(handle);
    if (hand) {
        return static_cast<jint>(hand->getHandSize());
    }
    return 0;
}

JNIEXPORT jintArray JNICALL Java_com_shkuba_Hand_getCardByIndex(JNIEnv* env, jobject thiz, jint index) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Hand* hand = reinterpret_cast<Hand*>(handle);

    if (hand && index >= 0 && index < hand->getHandSize()) {
        try {
            Card card = hand->getCardByIndex(index);
            jintArray result = env->NewIntArray(2);
            jint cardData[2] = {static_cast<jint>(card.getSuit()), static_cast<jint>(card.getRank())};
            env->SetIntArrayRegion(result, 0, 2, cardData);
            return result;
        } catch (const std::exception& e) {
            LOGE("Error getting card by index: %s", e.what());
            return env->NewIntArray(0);
        }
    }
    return env->NewIntArray(0);
}

JNIEXPORT jint JNICALL Java_com_shkuba_Hand_dropCard(JNIEnv* env, jobject thiz, jint cardIndex, jlong boardHandle) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Hand* hand = reinterpret_cast<Hand*>(handle);
    Board* board = reinterpret_cast<Board*>(boardHandle);
    
    if (hand && board) {
        Hand::status result = hand->dropCard(cardIndex, *board);
        return static_cast<jint>(result);
    }
    return static_cast<jint>(Hand::STATUS_ERROR_NOT_FIT);
}

JNIEXPORT jint JNICALL Java_com_shkuba_Hand_playCard(JNIEnv* env, jobject thiz, jint cardIndex, jintArray cardsToTake, jlong boardHandle) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Hand* hand = reinterpret_cast<Hand*>(handle);
    Board* board = reinterpret_cast<Board*>(boardHandle);
    
    if (hand && board) {
        jsize length = env->GetArrayLength(cardsToTake);
        jint* cards = env->GetIntArrayElements(cardsToTake, nullptr);
        
        std::vector<int> cardsVector;
        for (int i = 0; i < length; i++) {
            cardsVector.push_back(cards[i]);
        }
        
        Hand::status result = hand->playCard(static_cast<int>(cardIndex), cardsVector, *board);
        
        env->ReleaseIntArrayElements(cardsToTake, cards, 0);
        return static_cast<jint>(result);
    }
    return static_cast<jint>(Hand::STATUS_ERROR_NOT_FIT);
}

// Round JNI Methods
JNIEXPORT jlong JNICALL Java_com_shkuba_native_Round_nativeCreate(JNIEnv* env, jobject thiz, jint firstPlayer) {
    try {
        players player = static_cast<players>(firstPlayer);
        Round* round = new Round(player);
        LOGI("Round created successfully");
        return reinterpret_cast<jlong>(round);
    } catch (const std::exception& e) {
        LOGE("Error creating Round: %s", e.what());
        return 0;
    }
}

JNIEXPORT void JNICALL Java_com_shkuba_native_Round_nativeDestroy(JNIEnv* env, jobject thiz, jlong handle) {
    Round* round = reinterpret_cast<Round*>(handle);
    if (round) {
        delete round;
        LOGI("Round destroyed successfully");
    }
}

JNIEXPORT jint JNICALL Java_com_shkuba_native_Round_getP1Points(JNIEnv* env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Round* round = reinterpret_cast<Round*>(handle);
    if (round) {
        return static_cast<jint>(round->getP1Points());
    }
    return 0;
}

JNIEXPORT jint JNICALL Java_com_shkuba_native_Round_getP2Points(JNIEnv* env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Round* round = reinterpret_cast<Round*>(handle);
    if (round) {
        return static_cast<jint>(round->getP2Points());
    }
    return 0;
}

JNIEXPORT void JNICALL Java_com_shkuba_native_Round_firstMiniRound(JNIEnv* env, jobject thiz, jboolean choice) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Round* round = reinterpret_cast<Round*>(handle);
    if (round) {
        round->firstMiniRound(static_cast<bool>(choice));
    }
}

JNIEXPORT void JNICALL Java_com_shkuba_native_Round_giveCardsToPlayers(JNIEnv* env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Round* round = reinterpret_cast<Round*>(handle);
    if (round) {
        round->giveCardsToPlayers();
    }
}

JNIEXPORT jlong JNICALL Java_com_shkuba_native_Round_getP1HandHandle(JNIEnv* env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Round* round = reinterpret_cast<Round*>(handle);
    if (round) {
        return reinterpret_cast<jlong>(&round->getP1Hand());
    }
    return 0;
}

JNIEXPORT jlong JNICALL Java_com_shkuba_native_Round_getP2HandHandle(JNIEnv* env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Round* round = reinterpret_cast<Round*>(handle);
    if (round) {
        return reinterpret_cast<jlong>(&round->getP2Hand());
    }
    return 0;
}

JNIEXPORT jlong JNICALL Java_com_shkuba_native_Round_getBoardHandle(JNIEnv* env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Round* round = reinterpret_cast<Round*>(handle);
    if (round) {
        return reinterpret_cast<jlong>(&round->getBoard());
    }
    return 0;
}

// GameBot JNI Methods  
JNIEXPORT jlong JNICALL Java_com_shkuba_native_GameBot_nativeCreate(JNIEnv* env, jobject thiz) {
    try {
        GameBot* bot = new GameBot();
        LOGI("GameBot created successfully");
        return reinterpret_cast<jlong>(bot);
    } catch (const std::exception& e) {
        LOGE("Error creating GameBot: %s", e.what());
        return 0;
    }
}

JNIEXPORT void JNICALL Java_com_shkuba_native_GameBot_nativeDestroy(JNIEnv* env, jobject thiz, jlong handle) {
    GameBot* bot = reinterpret_cast<GameBot*>(handle);
    if (bot) {
        delete bot;
        LOGI("GameBot destroyed successfully");
    }
}

JNIEXPORT void JNICALL Java_com_shkuba_native_GameBot_botDropCard(JNIEnv* env, jobject thiz, jlong handHandle, jlong boardHandle) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    GameBot* bot = reinterpret_cast<GameBot*>(handle);
    Hand* hand = reinterpret_cast<Hand*>(handHandle);
    Board* board = reinterpret_cast<Board*>(boardHandle);
    
    if (bot && hand && board) {
        bot->botDropCard(*hand, *board);
    }
}

JNIEXPORT void JNICALL Java_com_shkuba_native_GameBot_playCard(JNIEnv* env, jobject thiz, jlong handHandle, jlong boardHandle) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    GameBot* bot = reinterpret_cast<GameBot*>(handle);
    Hand* hand = reinterpret_cast<Hand*>(handHandle);
    Board* board = reinterpret_cast<Board*>(boardHandle);
    
    if (bot && hand && board) {
        bot->playCard(*hand, *board);
    }
}

// Helper methods for wrapper classes that use existing handles
JNIEXPORT jint JNICALL Java_com_shkuba_native_GameEngine_00024HandWrapper_getHandSize(JNIEnv* env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jmethodID getHandleMethod = env->GetMethodID(cls, "getNativeHandle", "()J");
    jlong handle = env->CallLongMethod(thiz, getHandleMethod);
    Hand* hand = reinterpret_cast<Hand*>(handle);
    if (hand) {
        return static_cast<jint>(hand->getHandSize());
    }
    return 0;
}

JNIEXPORT jintArray JNICALL Java_com_shkuba_native_GameEngine_00024HandWrapper_getCardByIndex(JNIEnv* env, jobject thiz, jint index) {
    jclass cls = env->GetObjectClass(thiz);
    jmethodID getHandleMethod = env->GetMethodID(cls, "getNativeHandle", "()J");
    jlong handle = env->CallLongMethod(thiz, getHandleMethod);
    Hand* hand = reinterpret_cast<Hand*>(handle);
    if (hand && index >= 0 && index < hand->getHandSize()) {
        try {
            Card card = hand->getCardByIndex(static_cast<int>(index));
            jintArray result = env->NewIntArray(2);
            jint cardData[2] = {static_cast<jint>(card.getSuit()), static_cast<jint>(card.getRank())};
            env->SetIntArrayRegion(result, 0, 2, cardData);
            return result;
        } catch (const std::exception& e) {
            LOGE("Error getting card by index: %s", e.what());
            return env->NewIntArray(0);
        }
    }
    return env->NewIntArray(0);
}

JNIEXPORT jint JNICALL Java_com_shkuba_native_GameEngine_00024HandWrapper_dropCard(JNIEnv* env, jobject thiz, jint cardIndex, jlong boardHandle) {
    jclass cls = env->GetObjectClass(thiz);
    jmethodID getHandleMethod = env->GetMethodID(cls, "getNativeHandle", "()J");
    jlong handle = env->CallLongMethod(thiz, getHandleMethod);
    Hand* hand = reinterpret_cast<Hand*>(handle);
    Board* board = reinterpret_cast<Board*>(boardHandle);
    
    if (hand && board) {
        Hand::status result = hand->dropCard(cardIndex, *board);
        return static_cast<jint>(result);
    }
    return static_cast<jint>(Hand::STATUS_ERROR_NOT_FIT);
}

JNIEXPORT jint JNICALL Java_com_shkuba_native_GameEngine_00024HandWrapper_playCard(JNIEnv* env, jobject thiz, jint cardIndex, jintArray cardsToTake, jlong boardHandle) {
    jclass cls = env->GetObjectClass(thiz);
    jmethodID getHandleMethod = env->GetMethodID(cls, "getNativeHandle", "()J");
    jlong handle = env->CallLongMethod(thiz, getHandleMethod);
    Hand* hand = reinterpret_cast<Hand*>(handle);
    Board* board = reinterpret_cast<Board*>(boardHandle);
    
    if (hand && board) {
        jsize length = env->GetArrayLength(cardsToTake);
        jint* cards = env->GetIntArrayElements(cardsToTake, nullptr);
        
        std::vector<int> cardsVector;
        for (int i = 0; i < length; i++) {
            cardsVector.push_back(cards[i]);
        }
        
        Hand::status result = hand->playCard(static_cast<int>(cardIndex), cardsVector, *board);
        
        env->ReleaseIntArrayElements(cardsToTake, cards, 0);
        return static_cast<jint>(result);
    }
    return static_cast<jint>(Hand::STATUS_ERROR_NOT_FIT);
}

JNIEXPORT jint JNICALL Java_com_shkuba_native_GameEngine_00024BoardWrapper_getBoardSize(JNIEnv* env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jmethodID getHandleMethod = env->GetMethodID(cls, "getNativeHandle", "()J");
    jlong handle = env->CallLongMethod(thiz, getHandleMethod);
    Board* board = reinterpret_cast<Board*>(handle);
    if (board) {
        return static_cast<jint>(board->getBoardSize());
    }
    return 0;
}

JNIEXPORT jintArray JNICALL Java_com_shkuba_native_GameEngine_00024BoardWrapper_getBoardCards(JNIEnv* env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jmethodID getHandleMethod = env->GetMethodID(cls, "getNativeHandle", "()J");
    jlong handle = env->CallLongMethod(thiz, getHandleMethod);
    Board* board = reinterpret_cast<Board*>(handle);
    if (board) {
        try {
            std::vector<Card> cards = board->getBoard();
            jintArray result = env->NewIntArray(cards.size() * 2);
            
            jint* cardData = new jint[cards.size() * 2];
            for (size_t i = 0; i < cards.size(); i++) {
                cardData[i * 2] = static_cast<jint>(cards[i].getSuit());
                cardData[i * 2 + 1] = static_cast<jint>(cards[i].getRank());
            }
            
            env->SetIntArrayRegion(result, 0, cards.size() * 2, cardData);
            delete[] cardData;
            return result;
        } catch (const std::exception& e) {
            LOGE("Error getting board cards: %s", e.what());
            return env->NewIntArray(0);
        }
    }
    return env->NewIntArray(0);
}

JNIEXPORT jintArray JNICALL Java_com_shkuba_native_GameEngine_00024BoardWrapper_getCardByIndex(JNIEnv* env, jobject thiz, jint index) {
    jclass cls = env->GetObjectClass(thiz);
    jmethodID getHandleMethod = env->GetMethodID(cls, "getNativeHandle", "()J");
    jlong handle = env->CallLongMethod(thiz, getHandleMethod);
    Board* board = reinterpret_cast<Board*>(handle);
    if (board && index >= 0 && index < board->getBoardSize()) {
        try {
            Card card = board->getCardByIndex(static_cast<int>(index));
            jintArray result = env->NewIntArray(2);
            jint cardData[2] = {static_cast<jint>(card.getSuit()), static_cast<jint>(card.getRank())};
            env->SetIntArrayRegion(result, 0, 2, cardData);
            return result;
        } catch (const std::exception& e) {
            LOGE("Error getting board card by index: %s", e.what());
            return env->NewIntArray(0);
        }
    }
    return env->NewIntArray(0);
}

} // extern "C"