package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.CardBlue
import com.example.ui.theme.CardGreen
import com.example.ui.theme.CardPink
import com.example.ui.theme.CardPurple
import com.example.ui.theme.OnCardBlue
import com.example.ui.theme.OnCardGreen
import com.example.ui.theme.OnCardPink
import com.example.ui.theme.OnCardPurple
import com.example.ui.theme.OnPrimaryLight
import com.example.ui.theme.PrimaryLight

@Composable
fun DashboardScreen(viewModel: MainViewModel) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val journalEntries by viewModel.journalEntries.collectAsStateWithLifecycle()
    val water by viewModel.waterConsumed.collectAsStateWithLifecycle()
    val waterGoal by viewModel.waterGoal.collectAsStateWithLifecycle()
    val habits by viewModel.habits.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()

    val pendingTasks = tasks.count { !it.isCompleted }
    val completedTasks = tasks.count { it.isCompleted }
    val totalTasks = tasks.size
    
    val completedHabits = habits.count { it.isCompletedToday }
    val totalHabits = habits.size
    
    val safeWaterGoal = if (waterGoal > 0) waterGoal else 1
    val waterProgress = (water.toFloat() / safeWaterGoal).coerceIn(0f, 1f)
    
    // Calculate a truly composite daily progress score based on real records
    var totalScoreWeight = 0f
    var earnedScore = 0f
    
    // Task weight
    if (totalTasks > 0) {
        totalScoreWeight += 1f
        earnedScore += (completedTasks.toFloat() / totalTasks)
    }
    
    // Habit weight
    if (totalHabits > 0) {
        totalScoreWeight += 1f
        earnedScore += (completedHabits.toFloat() / totalHabits)
    }
    
    // Water weight
    totalScoreWeight += 1f
    earnedScore += waterProgress

    val progress = if (totalScoreWeight > 0f && !earnedScore.isNaN()) earnedScore / totalScoreWeight else 0f
    val safeProgress = progress.coerceIn(0f, 1f)
    val progressPercent = (safeProgress * 100).toInt()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PrimaryLight),
                    contentAlignment = Alignment.Center
                ) {
                    val initials = userName.take(2).uppercase()
                    Text(initials, color = OnPrimaryLight, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("LIFE MANAGER", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Hello, $userName", style = MaterialTheme.typography.titleLarge)
                }
            }
            IconButton(onClick = { /* TODO Settings */ }) {
                Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = MaterialTheme.colorScheme.onSurface)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Hero Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = PrimaryLight)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Daily Progress", style = MaterialTheme.typography.labelMedium, color = OnPrimaryLight.copy(alpha = 0.8f))
                Spacer(modifier = Modifier.height(4.dp))
                Text("$progressPercent% Done", style = MaterialTheme.typography.headlineMedium, color = OnPrimaryLight)
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(
                    progress = { safeProgress },
                    modifier = Modifier.fillMaxWidth(0.6f).height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = OnPrimaryLight,
                    trackColor = OnPrimaryLight.copy(alpha = 0.2f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text("$pendingTasks tasks left for today", style = MaterialTheme.typography.labelMedium, color = OnPrimaryLight)
            }
        }

        // 2x2 Grid Stats
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                title = "Tasks",
                value = "$completedTasks",
                subtitle = "Goal: $totalTasks",
                bgColor = CardGreen,
                contentColor = OnCardGreen,
                icon = "✓",
                modifier = Modifier.weight(1f)
            )
            
            
            StatCard(
                title = "Water",
                value = "${water}ml",
                subtitle = "Goal: ${waterGoal}ml",
                bgColor = CardBlue,
                contentColor = OnCardBlue,
                icon = "💧",
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                title = "Habits",
                value = "$completedHabits",
                subtitle = "Active: ${habits.size}",
                bgColor = CardPink,
                contentColor = OnCardPink,
                icon = "🔁",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Journal",
                value = "${journalEntries.size}",
                subtitle = "Total Entries",
                bgColor = CardPurple,
                contentColor = OnCardPurple,
                icon = "📝",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    bgColor: Color,
    contentColor: Color,
    icon: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Surface(
                    color = Color.White.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = icon, fontSize = 16.dp.value.sp)
                    }
                }
                Text(text = title.uppercase(), style = MaterialTheme.typography.labelSmall, color = contentColor.copy(alpha = 0.6f))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = contentColor)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = contentColor.copy(alpha = 0.7f))
        }
    }
}
