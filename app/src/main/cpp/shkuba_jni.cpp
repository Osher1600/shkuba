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
        return round->getP1Points();
    }
    return 0;
}

JNIEXPORT jint JNICALL Java_com_dinari_shkuba_Round_getP2Points(JNIEnv* env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Round* round = reinterpret_cast<Round*>(handle);
    if (round) {
        return round->getP2Points();
    }
    return 0;
}

JNIEXPORT void JNICALL Java_com_dinari_shkuba_Round_firstMiniRound(JNIEnv* env, jobject thiz, jboolean choice) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Round* round = reinterpret_cast<Round*>(handle);
    if (round) {
        round->firstMiniRound(choice);
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

// Get player 1 hand cards
JNIEXPORT jintArray JNICALL Java_com_dinari_shkuba_Round_getP1Hand(JNIEnv* env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Round* round = reinterpret_cast<Round*>(handle);

    if (round) {
        const Hand& hand = round->getP1Hand();
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

// Get player 2 hand cards
JNIEXPORT jintArray JNICALL Java_com_dinari_shkuba_Round_getP2Hand(JNIEnv* env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Round* round = reinterpret_cast<Round*>(handle);

    if (round) {
        const Hand& hand = round->getP2Hand();
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

// Get board cards
JNIEXPORT jintArray JNICALL Java_com_dinari_shkuba_Round_getBoardCards(JNIEnv* env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Round* round = reinterpret_cast<Round*>(handle);

    if (round) {
        const Board& board = round->getBoard();
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

// Player action: play card with pile management
JNIEXPORT jint JNICALL Java_com_dinari_shkuba_Round_playCard(JNIEnv* env, jobject thiz, jint player, jint cardIndex, jintArray cardsToTake) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Round* round = reinterpret_cast<Round*>(handle);

    if (round) {
        jint* takeArray = env->GetIntArrayElements(cardsToTake, nullptr);
        jsize takeSize = env->GetArrayLength(cardsToTake);
        std::vector<int> cardsToTakeVec(takeArray, takeArray + takeSize);
        env->ReleaseIntArrayElements(cardsToTake, takeArray, 0);

        Hand& hand = (player == 0) ? round->getP1Hand() : round->getP2Hand();
        Board& board = round->getBoard();
        
        // Store cards that will be captured before playing
        Card playedCard = hand.getCardByIndex(cardIndex);
        std::vector<Card> capturedCards;
        for (int idx : cardsToTakeVec) {
            if (idx < board.getBoardSize()) {
                capturedCards.push_back(board.getCardByIndex(idx));
            }
        }
        
        Hand::status result = hand.playCard(cardIndex, cardsToTakeVec, board);
        
        if (result == Hand::STATUS_OK) {
            // Add played card and captured cards to appropriate pile
            if (player == 0) {
                round->addToP1Pile(playedCard);
                for (const Card& captured : capturedCards) {
                    round->addToP1Pile(captured);
                }
            } else {
                round->addToP2Pile(playedCard);
                for (const Card& captured : capturedCards) {
                    round->addToP2Pile(captured);
                }
            }
        }
        
        return static_cast<jint>(result);
    }
    return -1;
}

// Player action: drop card
JNIEXPORT jint JNICALL Java_com_dinari_shkuba_Round_dropCard(JNIEnv* env, jobject thiz, jint player, jint cardIndex) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Round* round = reinterpret_cast<Round*>(handle);

    if (round) {
        Hand& hand = (player == 0) ? round->getP1Hand() : round->getP2Hand();
        Board& board = round->getBoard();
        
        Hand::status result = hand.dropCard(cardIndex, board);
        return static_cast<jint>(result);
    }
    return -1;
}

// Check if deck is empty
JNIEXPORT jboolean JNICALL Java_com_dinari_shkuba_Round_isDeckEmpty(JNIEnv* env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    Round* round = reinterpret_cast<Round*>(handle);
    if (round) {
        return round->isDeckEmpty();
    }
    return true; // Assume empty if error
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

// Bot makes a move
JNIEXPORT void JNICALL Java_com_dinari_shkuba_GameBot_makeMove(JNIEnv* env, jobject thiz, jlong roundHandle) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID handleField = env->GetFieldID(cls, "nativeHandle", "J");
    jlong handle = env->GetLongField(thiz, handleField);
    GameBot* bot = reinterpret_cast<GameBot*>(handle);
    Round* round = reinterpret_cast<Round*>(roundHandle);

    if (bot && round) {
        Hand& botHand = round->getP2Hand();
        Board& board = round->getBoard();
        bot->playCard(botHand, board);
    }
}

} // extern "C"
