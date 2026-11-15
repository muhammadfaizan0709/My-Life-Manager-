package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.models.WaterRecord
import com.example.data.models.WeightRecord
import com.example.data.models.MoodRecord
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

@Composable
fun AnalyticsScreen(viewModel: MainViewModel) {
    val waterRecords by viewModel.allWaterRecords.collectAsStateWithLifecycle()
    val weightRecords by viewModel.weightRecords.collectAsStateWithLifecycle()
    val moodRecords by viewModel.moodRecords.collectAsStateWithLifecycle()

    Scaffold { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Insights, contentDescription = "Analytics", modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(16.dp))
                Text("Visual Analytics", style = MaterialTheme.typography.headlineMedium)
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    WaterChartCard(waterRecords)
                }
                item {
                    WeightChartCard(weightRecords)
                }
                item {
                    MoodChartCard(moodRecords)
                }
            }
        }
    }
}

@Composable
fun WaterChartCard(records: List<WaterRecord>) {
    Card(
        modifier = Modifier.fillMaxWidth().height(260.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp).fillMaxSize()) {
            Text("Water Intake History (ml)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            if (records.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No water data yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                return@Column
            }

            // Aggregate by date string
            val aggregated = records.groupBy { it.dateString }
                .mapValues { entry -> entry.value.sumOf { it.amountMl } }
                .toList()
                .takeLast(7) // Show last 7 days

            val maxWater = aggregated.maxOfOrNull { it.second }?.toFloat()?.coerceAtLeast(2000f) ?: 2000f
            val barColor = MaterialTheme.colorScheme.primary

            Canvas(modifier = Modifier.fillMaxSize()) {
                val barWidth = size.width / (aggregated.size * 2f)
                val space = barWidth
                
                aggregated.forEachIndexed { index, pair ->
                    val x = index * (barWidth + space) + space / 2
                    val height = (pair.second / maxWater) * size.height
                    val y = size.height - height

                    drawRoundRect(
                        color = barColor,
                        topLeft = Offset(x, y),
                        size = Size(barWidth, height),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
                    )
                }
            }
        }
    }
}

@Composable
fun WeightChartCard(records: List<WeightRecord>) {
    Card(
        modifier = Modifier.fillMaxWidth().height(260.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp).fillMaxSize()) {
            Text("Weight Trends (kg)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            if (records.size < 2) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Need more data for trend line", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                return@Column
            }

            val recentRecords = records.takeLast(10)
            val maxWeight = recentRecords.maxOf { it.weightKg }
            val minWeight = recentRecords.minOf { it.weightKg }
            val range = max(maxWeight - minWeight, 5f)
            val lineColor = MaterialTheme.colorScheme.tertiary

            Canvas(modifier = Modifier.fillMaxSize().padding(vertical = 16.dp)) {
                val stepX = size.width / (recentRecords.size - 1).coerceAtLeast(1)
                val path = Path()

                recentRecords.forEachIndexed { index, record ->
                    val x = index * stepX
                    // Invert Y so higher weight is at the top
                    val normalizedY = 1f - ((record.weightKg - minWeight) / range)
                    val y = normalizedY * size.height

                    if (index == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }
                    drawCircle(color = lineColor, radius = 6.dp.toPx(), center = Offset(x, y))
                }

                drawPath(
                    path = path,
                    color = lineColor,
                    style = Stroke(width = 4.dp.toPx())
                )
            }
        }
    }
}

@Composable
fun MoodChartCard(records: List<MoodRecord>) {
    Card(
        modifier = Modifier.fillMaxWidth().height(260.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp).fillMaxSize()) {
            Text("Mood History", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            if (records.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No mood data yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                return@Column
            }

            val recentRecords = records.takeLast(7)
            val moodMap = mapOf("Happy" to 3f, "Neutral" to 2f, "Sad" to 1f)
            val lineColor = MaterialTheme.colorScheme.secondary

            Canvas(modifier = Modifier.fillMaxSize().padding(vertical = 16.dp)) {
                val stepX = size.width / (recentRecords.size - 1).coerceAtLeast(1)
                val path = Path()

                recentRecords.forEachIndexed { index, record ->
                    val x = index * stepX
                    val moodValue = moodMap[record.mood] ?: 2f
                    // 1f to 3f range
                    val normalizedY = 1f - ((moodValue - 1f) / 2f)
                    val y = normalizedY * size.height

                    if (index == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }
                    drawCircle(color = lineColor, radius = 6.dp.toPx(), center = Offset(x, y))
                }

                drawPath(
                    path = path,
                    color = lineColor,
                    style = Stroke(width = 4.dp.toPx())
                )
            }
        }
    }
}
