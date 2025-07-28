#include <jni.h>
#include <android/log.h>
#include "board.h"
#include "card.h"
#include "deck.h"
#include "hand.h"
#include "round.h"
#include "logic/gameBot.h"

#define LOG_TAG "ShkubaJNI"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C" {

// Board JNI Methods
JNIEXPORT jlong JNICALL Java_com_dinari_shkuba_Board_nativeCreate(JNIEnv* env, jobject thiz) {
    try {
        Board* board = new Board();
        LOGI("Board created successfully");
        return reinterpret_cast<jlong>(board);
    } catch (const std::exception& e) {
        LOGE("Error creating Board: %s", e.what());
        return 0;
    }
}

JNIEXPORT void JNICALL Java_com_dinari_shkuba_Board_nativeDestroy(JNIEnv* env, jobject thiz, jlong handle) {
    Board* board = reinterpret_cast<Board*>(handle);
    if (board) {
        delete board;
        LOGI("Board destroyed successfully");
    }
}

JNIEXPORT jint JNICALL Java_com_dinari_shkuba_Board_getBoardSize(JNIEnv* env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Board* board = reinterpret_cast<Board*>(handle);
    if (board) {
        return static_cast<jint>(board->getBoardSize());
    }
    return 0;
}

JNIEXPORT void JNICALL Java_com_dinari_shkuba_Board_addToBoard(JNIEnv* env, jobject thiz, jint suit, jint rank) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Board* board = reinterpret_cast<Board*>(handle);
    if (board) {
        Card card(static_cast<Card::suit>(suit), rank);
        board->addToBoard(card);
    }
}

JNIEXPORT jintArray JNICALL Java_com_dinari_shkuba_Board_getBoard(JNIEnv* env, jobject thiz) {
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

JNIEXPORT jint JNICALL Java_com_dinari_shkuba_Board_getBoardSizeNative(JNIEnv* env, jobject thiz) {
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
JNIEXPORT jlong JNICALL Java_com_dinari_shkuba_Card_nativeCreate(JNIEnv* env, jobject thiz, jint suit, jint rank) {
    try {
        Card* card = new Card(static_cast<Card::suit>(suit), rank);
        return reinterpret_cast<jlong>(card);
    } catch (const std::exception& e) {
        LOGE("Error creating Card: %s", e.what());
        return 0;
    }
}

JNIEXPORT void JNICALL Java_com_dinari_shkuba_Card_nativeDestroy(JNIEnv* env, jobject thiz, jlong handle) {
    Card* card = reinterpret_cast<Card*>(handle);
    if (card) {
        delete card;
    }
}

JNIEXPORT jint JNICALL Java_com_dinari_shkuba_Card_getSuit(JNIEnv* env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Card* card = reinterpret_cast<Card*>(handle);
    if (card) {
        return static_cast<jint>(card->getSuit());
    }
    return -1;
}

JNIEXPORT jint JNICALL Java_com_dinari_shkuba_Card_getRank(JNIEnv* env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Card* card = reinterpret_cast<Card*>(handle);
    if (card) {
        return static_cast<jint>(card->getRank());
    }
    return -1;
}

// Deck JNI Methods
JNIEXPORT jlong JNICALL Java_com_dinari_shkuba_Deck_nativeCreate(JNIEnv* env, jobject thiz) {
    try {
        Deck* deck = new Deck();
        return reinterpret_cast<jlong>(deck);
    } catch (const std::exception& e) {
        LOGE("Error creating Deck: %s", e.what());
        return 0;
    }
}

JNIEXPORT void JNICALL Java_com_dinari_shkuba_Deck_nativeDestroy(JNIEnv* env, jobject thiz, jlong handle) {
    Deck* deck = reinterpret_cast<Deck*>(handle);
    if (deck) {
        delete deck;
    }
}

JNIEXPORT void JNICALL Java_com_dinari_shkuba_Deck_shuffle(JNIEnv* env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Deck* deck = reinterpret_cast<Deck*>(handle);
    if (deck) {
        deck->shuffleDeck();
    }
}

JNIEXPORT jintArray JNICALL Java_com_dinari_shkuba_Deck_dealCard(JNIEnv* env, jobject thiz) {
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
JNIEXPORT jlong JNICALL Java_com_dinari_shkuba_Hand_nativeCreate(JNIEnv* env, jobject thiz) {
    try {
        Hand* hand = new Hand();
        return reinterpret_cast<jlong>(hand);
    } catch (const std::exception& e) {
        LOGE("Error creating Hand: %s", e.what());
        return 0;
    }
}

JNIEXPORT void JNICALL Java_com_dinari_shkuba_Hand_nativeDestroy(JNIEnv* env, jobject thiz, jlong handle) {
    Hand* hand = reinterpret_cast<Hand*>(handle);
    if (hand) {
        delete hand;
    }
}

JNIEXPORT void JNICALL Java_com_dinari_shkuba_Hand_addCard(JNIEnv* env, jobject thiz, jint suit, jint rank) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Hand* hand = reinterpret_cast<Hand*>(handle);
    if (hand) {
        Card card(static_cast<Card::suit>(suit), rank);
        hand->addToHand(card);
    }
}

// NativeCard JNI Methods
JNIEXPORT jlong JNICALL Java_com_dinari_shkuba_NativeCard_nativeCreate(JNIEnv* env, jobject thiz, jint suit, jint rank) {
    try {
        Card* card = new Card(static_cast<Card::suit>(suit), rank);
        return reinterpret_cast<jlong>(card);
    } catch (const std::exception& e) {
        __android_log_print(ANDROID_LOG_ERROR, "ShkubaJNI", "Error creating NativeCard: %s", e.what());
        return 0;
    }
}

JNIEXPORT void JNICALL Java_com_dinari_shkuba_NativeCard_nativeDestroy(JNIEnv* env, jobject thiz, jlong handle) {
    Card* card = reinterpret_cast<Card*>(handle);
    if (card) {
        delete card;
    }
}

JNIEXPORT jint JNICALL Java_com_dinari_shkuba_NativeCard_nativeGetSuit(JNIEnv* env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Card* card = reinterpret_cast<Card*>(handle);
    if (card) {
        return static_cast<jint>(card->getSuit());
    }
    return -1;
}

JNIEXPORT jint JNICALL Java_com_dinari_shkuba_NativeCard_nativeGetRank(JNIEnv* env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Card* card = reinterpret_cast<Card*>(handle);
    if (card) {
        return card->getRank();
    }
    return -1;
}

// Round JNI Methods
JNIEXPORT jlong JNICALL Java_com_dinari_shkuba_Round_nativeCreate(JNIEnv* env, jobject thiz, jint firstPlayer) {
    try {
        Round* round = new Round(static_cast<players>(firstPlayer));
        LOGI("Round created successfully");
        return reinterpret_cast<jlong>(round);
    } catch (const std::exception& e) {
        LOGE("Error creating Round: %s", e.what());
        return 0;
    }
}

JNIEXPORT void JNICALL Java_com_dinari_shkuba_Round_nativeDestroy(JNIEnv* env, jobject thiz, jlong handle) {
    Round* round = reinterpret_cast<Round*>(handle);
    if (round) {
        delete round;
        LOGI("Round destroyed successfully");
    }
}

JNIEXPORT jint JNICALL Java_com_dinari_shkuba_Round_getP1Points(JNIEnv* env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Round* round = reinterpret_cast<Round*>(handle);
    if (round) {
        return static_cast<jint>(round->getP1Points());
    }
    return 0;
}

JNIEXPORT jint JNICALL Java_com_dinari_shkuba_Round_getP2Points(JNIEnv* env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Round* round = reinterpret_cast<Round*>(handle);
    if (round) {
        return static_cast<jint>(round->getP2Points());
    }
    return 0;
}

JNIEXPORT void JNICALL Java_com_dinari_shkuba_Round_firstMiniRound(JNIEnv* env, jobject thiz, jboolean choice) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Round* round = reinterpret_cast<Round*>(handle);
    if (round) {
        round->firstMiniRound(static_cast<bool>(choice));
    }
}

JNIEXPORT void JNICALL Java_com_dinari_shkuba_Round_giveCardsToPlayers(JNIEnv* env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Round* round = reinterpret_cast<Round*>(handle);
    if (round) {
        round->giveCardsToPlayers();
    }
}

JNIEXPORT void JNICALL Java_com_dinari_shkuba_Round_countPiles(JNIEnv* env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Round* round = reinterpret_cast<Round*>(handle);
    if (round) {
        round->countPiles();
    }
}

// Enhanced Hand JNI Methods
JNIEXPORT jintArray JNICALL Java_com_dinari_shkuba_Hand_getHandCards(JNIEnv* env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Hand* hand = reinterpret_cast<Hand*>(handle);

    if (hand) {
        int handSize = hand->getHandSize();
        jintArray result = env->NewIntArray(handSize * 2);
        jint* elements = env->GetIntArrayElements(result, nullptr);

        for (int i = 0; i < handSize; i++) {
            Card card = hand->getCardByIndex(i);
            elements[i * 2] = static_cast<jint>(card.getSuit());
            elements[i * 2 + 1] = static_cast<jint>(card.getRank());
        }

        env->ReleaseIntArrayElements(result, elements, 0);
        return result;
    }
    return env->NewIntArray(0);
}

JNIEXPORT jint JNICALL Java_com_dinari_shkuba_Hand_getHandSize(JNIEnv* env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Hand* hand = reinterpret_cast<Hand*>(handle);
    if (hand) {
        return static_cast<jint>(hand->getHandSize());
    }
    return 0;
}

JNIEXPORT jintArray JNICALL Java_com_dinari_shkuba_Hand_getCardByIndex(JNIEnv* env, jobject thiz, jint index) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Hand* hand = reinterpret_cast<Hand*>(handle);

    if (hand && index >= 0 && index < hand->getHandSize()) {
        Card card = hand->getCardByIndex(index);
        jintArray result = env->NewIntArray(2);
        jint cardData[2] = {static_cast<jint>(card.getSuit()), static_cast<jint>(card.getRank())};
        env->SetIntArrayRegion(result, 0, 2, cardData);
        return result;
    }
    return env->NewIntArray(0);
}

// Round access methods to get hands and board
JNIEXPORT jintArray JNICALL Java_com_dinari_shkuba_Round_getP1Hand(JNIEnv* env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Round* round = reinterpret_cast<Round*>(handle);

    if (round) {
        Hand& hand = round->getP1Hand();
        int handSize = hand.getHandSize();
        jintArray result = env->NewIntArray(handSize * 2);
        jint* elements = env->GetIntArrayElements(result, nullptr);

        for (int i = 0; i < handSize; i++) {
            Card card = hand.getCardByIndex(i);
            elements[i * 2] = static_cast<jint>(card.getSuit());
            elements[i * 2 + 1] = static_cast<jint>(card.getRank());
        }

        env->ReleaseIntArrayElements(result, elements, 0);
        return result;
    }
    return env->NewIntArray(0);
}

JNIEXPORT jintArray JNICALL Java_com_dinari_shkuba_Round_getP2Hand(JNIEnv* env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Round* round = reinterpret_cast<Round*>(handle);

    if (round) {
        Hand& hand = round->getP2Hand();
        int handSize = hand.getHandSize();
        jintArray result = env->NewIntArray(handSize * 2);
        jint* elements = env->GetIntArrayElements(result, nullptr);

        for (int i = 0; i < handSize; i++) {
            Card card = hand.getCardByIndex(i);
            elements[i * 2] = static_cast<jint>(card.getSuit());
            elements[i * 2 + 1] = static_cast<jint>(card.getRank());
        }

        env->ReleaseIntArrayElements(result, elements, 0);
        return result;
    }
    return env->NewIntArray(0);
}

JNIEXPORT jintArray JNICALL Java_com_dinari_shkuba_Round_getBoard(JNIEnv* env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Round* round = reinterpret_cast<Round*>(handle);

    if (round) {
        Board& board = round->getBoard();
        std::vector<Card> cards = board.getBoard();
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

// Round object access methods for bot integration
JNIEXPORT jlong JNICALL Java_com_dinari_shkuba_Round_getP1HandObject(JNIEnv* env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Round* round = reinterpret_cast<Round*>(handle);

    if (round) {
        return reinterpret_cast<jlong>(&round->getP1Hand());
    }
    return 0;
}

JNIEXPORT jlong JNICALL Java_com_dinari_shkuba_Round_getP2HandObject(JNIEnv* env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Round* round = reinterpret_cast<Round*>(handle);

    if (round) {
        return reinterpret_cast<jlong>(&round->getP2Hand());
    }
    return 0;
}

JNIEXPORT jlong JNICALL Java_com_dinari_shkuba_Round_getBoardObject(JNIEnv* env, jobject thiz) {
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
JNIEXPORT jlong JNICALL Java_com_dinari_shkuba_GameBot_nativeCreate(JNIEnv* env, jobject thiz) {
    try {
        GameBot* bot = new GameBot();
        LOGI("GameBot created successfully");
        return reinterpret_cast<jlong>(bot);
    } catch (const std::exception& e) {
        LOGE("Error creating GameBot: %s", e.what());
        return 0;
    }
}

JNIEXPORT void JNICALL Java_com_dinari_shkuba_GameBot_nativeDestroy(JNIEnv* env, jobject thiz, jlong handle) {
    GameBot* bot = reinterpret_cast<GameBot*>(handle);
    if (bot) {
        delete bot;
        LOGI("GameBot destroyed successfully");
    }
}

JNIEXPORT void JNICALL Java_com_dinari_shkuba_GameBot_playCard(JNIEnv* env, jobject thiz, jlong handHandle, jlong boardHandle) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong botHandle = env->GetLongField(thiz, handleField);
    
    GameBot* bot = reinterpret_cast<GameBot*>(botHandle);
    Hand* hand = reinterpret_cast<Hand*>(handHandle);
    Board* board = reinterpret_cast<Board*>(boardHandle);
    
    if (bot && hand && board) {
        bot->playCard(*hand, *board);
        LOGI("Bot played a card");
    }
}

JNIEXPORT void JNICALL Java_com_dinari_shkuba_GameBot_dropCardNative(JNIEnv* env, jobject thiz, jlong handHandle, jlong boardHandle) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong botHandle = env->GetLongField(thiz, handleField);
    
    GameBot* bot = reinterpret_cast<GameBot*>(botHandle);
    Hand* hand = reinterpret_cast<Hand*>(handHandle);
    Board* board = reinterpret_cast<Board*>(boardHandle);
    
    if (bot && hand && board) {
        bot->botDropCard(*hand, *board);
        LOGI("Bot dropped a card");
    }
}

} // extern "C"
