package com.dinari.shkuba.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.dinari.shkuba.R
import com.shkuba.network.NetworkService
import androidx.compose.ui.text.font.FontWeight

@Composable
fun PvpPlayerListScreen(onBack: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    val players by NetworkService.connectedPlayers.collectAsState()
    var alertMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(colorScheme.background, colorScheme.surface)
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
                stringResource(id = R.string.pvp),
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(32.dp))
            if (players.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.no_players_available),
                    style = MaterialTheme.typography.bodyLarge,
                    color = colorScheme.onBackground
                )
            } else {
                players.forEach { player ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable {
                                NetworkService.sendAlert(player)
                                alertMessage = "Alert sent to $player!"
                            },
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
                    ) {
                        Text(
                            text = player,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            color = colorScheme.onSurface
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            if (alertMessage.isNotEmpty()) {
                Text(
                    alertMessage,
                    style = MaterialTheme.typography.bodyLarge,
                    color = colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            Button(onClick = onBack) {
                Text(stringResource(id = R.string.back))
            }
        }
    }
}
