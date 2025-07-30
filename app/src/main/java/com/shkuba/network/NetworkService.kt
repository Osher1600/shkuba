package com.shkuba.network

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object NetworkService {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val playersRef: DatabaseReference = database.getReference("players")
    private val alertsRef: DatabaseReference = database.getReference("alerts")

    private val _connectedPlayers = MutableStateFlow<List<String>>(emptyList())
    val connectedPlayers: StateFlow<List<String>> = _connectedPlayers

    private val _alerts = MutableStateFlow("")
    val alerts: StateFlow<String> = _alerts

    init {
        // Listen for player list changes
        playersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val players = mutableListOf<String>()
                for (child in snapshot.children) {
                    child.getValue(String::class.java)?.let { players.add(it) }
                }
                if (players.isEmpty()) {
                    println("No players available") // Debugging log
                }
                _connectedPlayers.value = players
            }

            override fun onCancelled(error: DatabaseError) {
                // Log the error for debugging
                println("Error fetching players: ${error.message}")
            }
        })
        // Listen for alerts
        alertsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val alert = snapshot.getValue(String::class.java) ?: ""
                _alerts.value = alert
            }

            override fun onCancelled(error: DatabaseError) {
                // Log the error for debugging
                println("Error fetching alerts: ${error.message}")
            }
        })
    }

    fun addPlayer(playerName: String) {
        playersRef.child(playerName).setValue(playerName)
    }

    fun removePlayer(playerName: String) {
        playersRef.child(playerName).removeValue()
    }

    fun sendAlert(to: String) {
        alertsRef.setValue("Alert sent to $to!")
    }

    fun connectPlayer(playerName: String) {
        val playerRef = playersRef.child(playerName)
        playerRef.setValue(playerName)

        // Automatically remove player on disconnect
        playerRef.onDisconnect().removeValue()
    }
}
