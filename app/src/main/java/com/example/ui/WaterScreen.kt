package com.example.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun WaterScreen(viewModel: MainViewModel) {
    val waterConsumed by viewModel.waterConsumed.collectAsStateWithLifecycle()
    val dailyGoal by viewModel.waterGoal.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Water Intake Tracker", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        androidx.compose.foundation.layout.Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { (waterConsumed.toFloat() / dailyGoal).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth(0.6f).height(240.dp),
                strokeWidth = 16.dp
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "$waterConsumed ml", style = MaterialTheme.typography.headlineLarge)
                Text(text = "of $dailyGoal ml", style = MaterialTheme.typography.bodyLarge)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { viewModel.addWater(250) }) {
                Text("+250 ml")
            }
            Button(onClick = { viewModel.addWater(500) }) {
                Text("+500 ml")
            }
        }
    }
}
