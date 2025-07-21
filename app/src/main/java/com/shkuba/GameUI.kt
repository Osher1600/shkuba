package com.dinari.shkuba


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.horizontalScroll
import com.dinari.shkuba.R

// Data models
sealed class Suit(val symbol: String) {
    object Spades : Suit("♠")
    object Diamonds : Suit("♦")
    object Clubs : Suit("♣")
    object Hearts : Suit("♥")
}

data class Card(val value: String, val suit: Suit) {
    override fun toString(): String = "$value${suit.symbol}"
}

data class Player(val name: String, val hand: List<Card>)

data class GameState(
    val players: List<Player>,
    val tableCards: List<Card>,
    val currentPlayerIndex: Int
)

// UI Components
@Composable
fun CardView(
    card: Card,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .size(70.dp, 100.dp),
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
                    text = card.value,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = when (card.suit) {
                        is Suit.Hearts, is Suit.Diamonds -> Color(0xFFDC3545)
                        else -> Color(0xFF212529)
                    }
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = card.suit.symbol,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 20.sp
                    ),
                    color = when (card.suit) {
                        is Suit.Hearts, is Suit.Diamonds -> Color(0xFFDC3545)
                        else -> Color(0xFF212529)
                    }
                )
            }
        }
    }
}

@Composable
fun GameScreen(gameState: GameState, onPlayCard: (Card) -> Unit) {
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
                color = cardTextPrimary(),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // Table section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .shadow(8.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardSurface())
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
                        color = cardTextPrimary()
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
                colors = CardDefaults.cardColors(containerColor = cardSurface())
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
                        color = cardTextPrimary()
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        gameState.players[gameState.currentPlayerIndex].hand.forEach { card ->
                            CardView(
                                card = card,
                                onClick = { onPlayCard(card) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TableView(cards: List<Card>) {
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
fun PlayerHandView(player: Player, onPlayCard: (Card) -> Unit) {
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
    onPvp: () -> Unit,
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
                        cardBackground(),
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
                color = cardTextPrimary()
            )
            Spacer(modifier = Modifier.height(48.dp))

            MenuButton(
                text = startGameLabel,
                onClick = onStartGame,
                gradient = Brush.horizontalGradient(
                    colors = listOf(cardPrimary(), cardPrimaryLight())
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            MenuButton(
                text = optionsLabel,
                onClick = onOptions,
                gradient = Brush.horizontalGradient(
                    colors = listOf(cardAccentOrange(), cardAccentOrange().copy(alpha = 0.8f))
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
            Spacer(modifier = Modifier.height(16.dp))
            MenuButton(
                text = stringResource(id = R.string.pvp),
                onClick = onPvp,
                gradient = Brush.horizontalGradient(
                    colors = listOf(cardAccentGreen(), cardAccentGreen().copy(alpha = 0.8f))
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
                containerColor = cardSurface()
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
                    color = cardTextPrimary()
                )
                Spacer(modifier = Modifier.height(32.dp))

                MenuButton(
                    text = mainMenuLabel,
                    onClick = onMainMenu,
                    gradient = Brush.horizontalGradient(
                        colors = listOf(cardPrimary(), cardPrimaryLight())
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                MenuButton(
                    text = optionsLabel,
                    onClick = onOptions,
                    gradient = Brush.horizontalGradient(
                        colors = listOf(cardAccentOrange(), cardAccentOrange().copy(alpha = 0.8f))
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                MenuButton(
                    text = backToGameLabel,
                    onClick = onBackToGame,
                    gradient = Brush.horizontalGradient(
                        colors = listOf(cardAccentGreen(), cardAccentGreen().copy(alpha = 0.8f))
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
    val colorScheme = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        colorScheme.background,
                        colorScheme.surface
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
                stringResource(id = R.string.options),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp,
                    letterSpacing = (-0.5).sp
                ),
                color = colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(48.dp))

            // Theme Switch
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(id = R.string.dark_mode),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = colorScheme.onSurface
                    )
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { onToggleTheme() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = colorScheme.primary,
                            checkedTrackColor = colorScheme.primary.copy(alpha = 0.5f),
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
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        stringResource(id = R.string.language),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = colorScheme.onSurface
                    )
                    languageOptions.forEach { lang ->
                        val isSelected = lang == selectedLanguage
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .background(
                                    if (isSelected) colorScheme.primary else Color.Transparent,
                                    RoundedCornerShape(24.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) colorScheme.primary else colorScheme.outline,
                                    shape = RoundedCornerShape(24.dp)
                                )
                                .clickable { onLanguageChange(lang) }
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                lang,
                                color = if (isSelected) colorScheme.onPrimary else colorScheme.onSurface,
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
                text = stringResource(id = R.string.back),
                onClick = onBack,
                gradient = Brush.horizontalGradient(
                    colors = listOf(colorScheme.secondary, colorScheme.secondary.copy(alpha = 0.8f))
                )
            )
        }
    }
}

@Composable
fun cardTextPrimary() = MaterialTheme.colorScheme.onSurface
@Composable
fun cardSurface() = MaterialTheme.colorScheme.surface
@Composable
fun cardBackground() = MaterialTheme.colorScheme.background
@Composable
fun cardPrimary() = MaterialTheme.colorScheme.primary
@Composable
fun cardPrimaryLight() = MaterialTheme.colorScheme.primaryContainer
@Composable
fun cardAccentOrange() = MaterialTheme.colorScheme.secondary
@Composable
fun cardAccentGreen() = MaterialTheme.colorScheme.tertiary
