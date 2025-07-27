package com.shkuba

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.horizontalScroll
import com.shkuba.native.NativeCard

// Define missing constants
val TextPrimaryLight = Color.Black
val SurfaceLight = Color.White
val BackgroundLight = Color.LightGray
val Primary = Color.Blue
val PrimaryLight = Color.Cyan
val AccentOrange = Color(0xFFFFA500)
val AccentGreen = Color(0xFF00FF00)
val CardBorderLight = Color.Gray

// Data models for native integration
data class GameCard(val suit: Int, val rank: Int) {
    fun getSuitSymbol(): String = when (suit) {
        0 -> "♠" // Spades
        1 -> "♥" // Hearts  
        2 -> "♦" // Diamonds
        3 -> "♣" // Clubs
        else -> "?"
    }
    
    fun getRankString(): String = when (rank) {
        1 -> "A"
        11 -> "J"
        12 -> "Q"
        13 -> "K"
        else -> rank.toString()
    }
    
    override fun toString(): String = "${getRankString()}${getSuitSymbol()}"
}

data class Player(val name: String, val hand: List<GameCard>)

data class GameState(
    val players: List<Player>,
    val tableCards: List<GameCard>,
    val currentPlayerIndex: Int
)

// UI Components
@Composable
fun CardView(
    card: GameCard,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .size(70.dp, 100.dp)
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .border(
                    width = 1.dp,
                    color = Color.Gray,
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(4.dp)
            ) {
                Text(
                    text = card.getRankString(),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = when (card.suit) {
                        1, 2 -> Color(0xFFDC3545) // Hearts, Diamonds
                        else -> Color(0xFF212529) // Spades, Clubs
                    }
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = card.getSuitSymbol(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 20.sp
                    ),
                    color = when (card.suit) {
                        1, 2 -> Color(0xFFDC3545) // Hearts, Diamonds
                        else -> Color(0xFF212529) // Spades, Clubs
                    }
                )
            }
        }
    }
}

@Composable
fun GameScreen(gameState: GameState, onPlayCard: (GameCard) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8F0FE))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Shkuba Card Game",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                color = TextPrimaryLight,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            
            // Opponent's hand section (show face down cards)
            if (gameState.players.size > 1) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceLight)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "${gameState.players[1].name}'s Hand (${gameState.players[1].hand.size} cards)",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = TextPrimaryLight
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            repeat(gameState.players[1].hand.size) {
                                // Show face down cards
                                Box(
                                    modifier = Modifier
                                        .size(70.dp, 100.dp)
                                        .background(
                                            Color(0xFF6C5CE7),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = Color.Gray,
                                            shape = RoundedCornerShape(12.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "?",
                                        style = MaterialTheme.typography.headlineLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 32.sp
                                        ),
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Current player indicator
            Text(
                "Current Player: ${gameState.players[gameState.currentPlayerIndex].name}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = if (gameState.currentPlayerIndex == 0) AccentGreen else AccentOrange,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // Table section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .shadow(8.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Table",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = TextPrimaryLight
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        gameState.tableCards.forEach { card ->
                            CardView(card)
                        }
                    }
                }
            }

            // Player's hand section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .shadow(8.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "${gameState.players[gameState.currentPlayerIndex].name}'s Hand",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = TextPrimaryLight
                    )
                    
                    // Check if game is over
                    val currentPlayerHand = gameState.players[gameState.currentPlayerIndex].hand
                    if (currentPlayerHand.isEmpty()) {
                        Text(
                            "🎉 ${gameState.players[gameState.currentPlayerIndex].name} Wins! 🎉",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = AccentGreen,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            currentPlayerHand.forEach { card ->
                                CardView(
                                    card = card,
                                    onClick = if (gameState.currentPlayerIndex == 0) { { onPlayCard(card) } } else null
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TableView(cards: List<GameCard>) {
    Column {
        Text("Table:", style = MaterialTheme.typography.titleMedium)
        Row {
            cards.forEach { card ->
                CardView(card)
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
fun PlayerHandView(player: Player, onPlayCard: (GameCard) -> Unit) {
    Column {
        Text("${player.name}'s Hand:", style = MaterialTheme.typography.titleMedium)
        Row {
            player.hand.forEach { card ->
                CardView(card, onClick = { onPlayCard(card) })
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
fun MainMenu(
    onStartGame: () -> Unit,
    onOptions: () -> Unit,
    onExit: () -> Unit,
    startGameLabel: String,
    optionsLabel: String,
    exitLabel: String,
    titleLabel: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        BackgroundLight,
                        Color(0xFFE8F0FE)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                titleLabel,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 42.sp,
                    letterSpacing = (-1).sp
                ),
                color = TextPrimaryLight
            )
            Spacer(modifier = Modifier.height(48.dp))

            MenuButton(
                text = startGameLabel,
                onClick = onStartGame,
                gradient = Brush.horizontalGradient(
                    colors = listOf(Primary, PrimaryLight)
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            MenuButton(
                text = optionsLabel,
                onClick = onOptions,
                gradient = Brush.horizontalGradient(
                    colors = listOf(AccentOrange, AccentOrange.copy(alpha = 0.8f))
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            MenuButton(
                text = exitLabel,
                onClick = onExit,
                gradient = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF6C757D), Color(0xFF495057))
                )
            )
        }
    }
}

@Composable
fun MenuButton(
    text: String,
    onClick: () -> Unit,
    gradient: Brush
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale = if (isPressed) 0.95f else 1f

    Box(
        modifier = Modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .fillMaxWidth()
            .height(56.dp)
            .background(gradient, RoundedCornerShape(28.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(28.dp)
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            ),
            color = Color.White
        )
    }
}

@Composable
fun InGameMenu(
    onMainMenu: () -> Unit,
    onBackToGame: () -> Unit,
    onOptions: () -> Unit,
    mainMenuLabel: String,
    backToGameLabel: String,
    optionsLabel: String,
    titleLabel: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x99000000))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onBackToGame
            )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .align(Alignment.Center)
                .clickable(enabled = false) { },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = SurfaceLight
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    titleLabel,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextPrimaryLight
                )
                Spacer(modifier = Modifier.height(32.dp))

                MenuButton(
                    text = mainMenuLabel,
                    onClick = onMainMenu,
                    gradient = Brush.horizontalGradient(
                        colors = listOf(Primary, PrimaryLight)
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                MenuButton(
                    text = optionsLabel,
                    onClick = onOptions,
                    gradient = Brush.horizontalGradient(
                        colors = listOf(AccentOrange, AccentOrange.copy(alpha = 0.8f))
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                MenuButton(
                    text = backToGameLabel,
                    onClick = onBackToGame,
                    gradient = Brush.horizontalGradient(
                        colors = listOf(AccentGreen, AccentGreen.copy(alpha = 0.8f))
                    )
                )
            }
        }
    }
}

@Composable
fun OptionsScreen(
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    onBack: () -> Unit,
    selectedLanguage: String,
    onLanguageChange: (String) -> Unit,
    languageOptions: List<String>
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        BackgroundLight,
                        Color(0xFFE8F0FE)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Options",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp,
                    letterSpacing = (-0.5).sp
                ),
                color = TextPrimaryLight
            )
            Spacer(modifier = Modifier.height(48.dp))

            // Theme Switch
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Dark Mode",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = TextPrimaryLight
                    )
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { onToggleTheme() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Primary,
                            checkedTrackColor = PrimaryLight.copy(alpha = 0.5f),
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color.LightGray
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Language Selection
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Language",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = TextPrimaryLight
                    )
                    languageOptions.forEach { lang ->
                        val isSelected = lang == selectedLanguage
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .background(
                                    if (isSelected) Primary else Color.Transparent,
                                    RoundedCornerShape(24.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) Primary else CardBorderLight,
                                    shape = RoundedCornerShape(24.dp)
                                )
                                .clickable { onLanguageChange(lang) }
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                lang,
                                color = if (isSelected) Color.White else TextPrimaryLight,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            MenuButton(
                text = "Back",
                onClick = onBack,
                gradient = Brush.horizontalGradient(
                    colors = listOf(AccentGreen, AccentGreen.copy(alpha = 0.8f))
                )
            )
        }
    }
}
