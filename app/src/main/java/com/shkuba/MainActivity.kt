package com.shkuba

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shkuba.ui.theme.ShkubaTheme
import java.util.Locale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.key
import com.dinari.shkuba.Board
import com.dinari.shkuba.CardGui
import com.dinari.shkuba.GameScreen
import com.dinari.shkuba.GameState
import com.dinari.shkuba.InGameMenu
import com.dinari.shkuba.MainMenu
import com.dinari.shkuba.OptionsScreen
import com.dinari.shkuba.Player
import com.dinari.shkuba.R
import com.dinari.shkuba.Suit
import com.shkuba.ui.PvpPlayerListScreen
import com.shkuba.network.NetworkService

class MainActivity : ComponentActivity() {
    companion object {
        init {
            System.loadLibrary("shkuba") // Replace with your actual library name if different
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode = remember { mutableStateOf(false) }
            val board = Board()
            val localeState = remember { mutableStateOf(Locale.getDefault()) }
            val context = LocalContext.current
            val config = Configuration(context.resources.configuration)
            config.setLocale(localeState.value)
            val localizedContext = context.createConfigurationContext(config)
            CompositionLocalProvider(LocalContext provides localizedContext) {
                ShkubaTheme(darkTheme = isDarkMode.value, dynamicColor = false) {
                    MainScreen(
                        onExit = { finish() },
                        isDarkMode = isDarkMode,
                        localeState = localeState
                    )
                }
            }
        }
        // Example: Connect player with a hardcoded name
        NetworkService.connectPlayer("Player1")
    }
}

@Composable
fun MainScreen(onExit: () -> Unit, isDarkMode: MutableState<Boolean>, localeState: MutableState<Locale>) {
    val showMenu = remember { mutableStateOf(true) }
    val showOptions = remember { mutableStateOf(false) }
    val gameManager = remember { GameManager() }
    val gameState = remember { mutableStateOf(gameManager.getGameStatus()) }
    val showInGameMenu = remember { mutableStateOf(false) }
    val showPvpList = remember { mutableStateOf(false) }
    val supportedLanguages = listOf("English", "Hebrew", "Hindi")
    val languageToLocale = mapOf(
        "English" to Locale("en"),
        "Hebrew" to Locale("iw"),
        "Hindi" to Locale("hi")
    )
    val selectedLanguage = remember { mutableStateOf(
        languageToLocale.entries.find { it.value.language == localeState.value.language }?.key ?: "English"
    ) }

    // Update game state whenever needed
    fun updateGameState() {
        gameState.value = gameManager.getGameStatus()
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        when {
            showOptions.value -> {
                key(localeState.value) {
                    OptionsScreen(
                        isDarkMode = isDarkMode.value,
                        onToggleTheme = { isDarkMode.value = !isDarkMode.value },
                        onBack = { showOptions.value = false },
                        selectedLanguage = selectedLanguage.value,
                        onLanguageChange = { lang: String ->
                            selectedLanguage.value = lang
                            val locale = languageToLocale[lang] ?: Locale("en")
                            localeState.value = locale
                        },
                        languageOptions = supportedLanguages
                    )
                }
            }
            showPvpList.value -> {
                PvpPlayerListScreen(onBack = { showPvpList.value = false; showMenu.value = true })
            }
            showMenu.value -> {
                MainMenu(
                    onStartGame = {
                        gameManager.startNewGame()
                        updateGameState()
                        showMenu.value = false
                    },
                    onOptions = { showOptions.value = true },
                    onExit = onExit,
                    onPvp = { showMenu.value = false; showPvpList.value = true },
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
                    GameScreenWithLogic(
                        gameStatus = gameState.value,
                        gameManager = gameManager,
                        onUpdateGameState = { updateGameState() }
                    )
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
