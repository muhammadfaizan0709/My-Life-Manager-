package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun StopwatchScreen() {
    var isRunning by remember { mutableStateOf(false) }
    var elapsedMillis by remember { mutableStateOf(0L) }
    var lastTick by remember { mutableStateOf(0L) }

    LaunchedEffect(isRunning) {
        if (isRunning) {
            lastTick = System.currentTimeMillis()
            while (isRunning) {
                delay(10) // Update every 10ms for smooth UI
                val now = System.currentTimeMillis()
                elapsedMillis += (now - lastTick)
                lastTick = now
            }
        }
    }

    val minutes = (elapsedMillis / 60000) % 60
    val seconds = (elapsedMillis / 1000) % 60
    val millis = (elapsedMillis % 1000) / 10

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Stopwatch", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = String.format("%02d:%02d.%02d", minutes, seconds, millis),
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(48.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            Button(
                onClick = { 
                    isRunning = !isRunning
                    if (isRunning) lastTick = System.currentTimeMillis() 
                },
                modifier = Modifier.size(80.dp),
                shape = CircleShape
            ) {
                Icon(if (isRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow, contentDescription = "Start/Pause")
            }
            
            Button(
                onClick = {
                    isRunning = false
                    elapsedMillis = 0L
                },
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Icon(Icons.Filled.Refresh, contentDescription = "Reset")
            }
        }
    }
}
