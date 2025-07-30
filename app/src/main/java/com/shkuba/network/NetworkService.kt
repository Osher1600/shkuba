package com.shkuba.network

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object NetworkService {
    private val _connectedPlayers = MutableStateFlow<List<String>>(emptyList())
    val connectedPlayers: StateFlow<List<String>> = _connectedPlayers

    private val _alerts = MutableStateFlow("")
    val alerts: StateFlow<String> = _alerts

    fun addPlayer(playerName: String) {
        val currentPlayers = _connectedPlayers.value.toMutableList()
        if (!currentPlayers.contains(playerName)) {
            currentPlayers.add(playerName)
            _connectedPlayers.value = currentPlayers
        }
    }

    fun removePlayer(playerName: String) {
        val currentPlayers = _connectedPlayers.value.toMutableList()
        currentPlayers.remove(playerName)
        _connectedPlayers.value = currentPlayers
    }

    fun sendAlert(to: String) {
        _alerts.value = "Alert sent to $to!"
    }

    fun connectPlayer(playerName: String) {
        addPlayer(playerName)
    }
}
