package com.shkuba

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shkuba.ui.theme.ShkubaTheme
import java.util.Locale
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShkubaTheme {
                MainScreen(onExit = { finish() })
            }
        }
    }
}

@Composable
fun MainScreen(onExit: () -> Unit) {
    val context = LocalContext.current
    val showMenu = remember { mutableStateOf(true) }
    val showOptions = remember { mutableStateOf(false) }
    val isDarkMode = remember { mutableStateOf(false) }
    val gameState = remember {
        mutableStateOf(
            GameState(
                players = listOf(),
                tableCards = listOf(),
                currentPlayerIndex = 0
            )
        )
    }
    val showInGameMenu = remember { mutableStateOf(false) }
    val supportedLanguages = listOf("English", "Hebrew", "Hindi")
    val languageToLocale = mapOf(
        "English" to Locale("en"),
        "Hebrew" to Locale("iw"),
        "Hindi" to Locale("hi")
    )
    val currentLocale = Locale.getDefault()
    val selectedLanguage = remember { mutableStateOf(
        languageToLocale.entries.find { it.value.language == currentLocale.language }?.key ?: "English"
    ) }

    Surface(modifier = Modifier.fillMaxSize()) {
        when {
            showOptions.value -> {
                OptionsScreen(
                    isDarkMode = isDarkMode.value,
                    onToggleTheme = { isDarkMode.value = !isDarkMode.value },
                    onBack = { showOptions.value = false },
                    selectedLanguage = selectedLanguage.value,
                    onLanguageChange = { lang: String ->
                        selectedLanguage.value = lang
                        val locale = languageToLocale[lang] ?: Locale("en")
                        val resources = context.resources
                        val config = Configuration(resources.configuration)
                        config.setLocale(locale)
                        context.createConfigurationContext(config)
                    },
                    languageOptions = supportedLanguages
                )
            }
            showMenu.value -> {
                MainMenu(
                    onStartGame = {
                        val player = Player("You", listOf(
                            Card("A", Suit.Spades),
                            Card("7", Suit.Diamonds),
                            Card("K", Suit.Clubs),
                            Card("3", Suit.Hearts)
                        ))
                        val opponent = Player("Opponent", listOf())
                        val tableCards = listOf(
                            Card("2", Suit.Spades),
                            Card("J", Suit.Diamonds)
                        )
                        gameState.value = GameState(
                            players = listOf(player, opponent),
                            tableCards = tableCards,
                            currentPlayerIndex = 0
                        )
                        showMenu.value = false
                    },
                    onOptions = { showOptions.value = true },
                    onExit = onExit,
                    startGameLabel = stringResource(R.string.start_game),
                    optionsLabel = stringResource(R.string.options),
                    exitLabel = stringResource(R.string.exit),
                    titleLabel = stringResource(R.string.app_name)
                )
            }
            showInGameMenu.value -> {
                InGameMenu(
                    onMainMenu = {
                        showMenu.value = true
                        showInGameMenu.value = false
                    },
                    onBackToGame = { showInGameMenu.value = false },
                    onOptions = {
                        showOptions.value = true
                        showInGameMenu.value = false
                    },
                    mainMenuLabel = stringResource(R.string.main_menu),
                    backToGameLabel = stringResource(R.string.back_to_game),
                    optionsLabel = stringResource(R.string.options),
                    titleLabel = stringResource(R.string.game_menu)
                )
            }
            else -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    GameScreen(gameState = gameState.value, onPlayCard = { /* TODO: Play card logic */ })
                    Button(
                        onClick = { showInGameMenu.value = true },
                        modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
                    ) {
                        Text(stringResource(R.string.options))
                    }
                }
            }
        }
    }
}
