package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

data class WorldCity(val name: String, val timeZoneId: String)

@Composable
fun WorldClockScreen() {
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = System.currentTimeMillis()
            delay(1000)
        }
    }

    val defaultCities = remember {
        listOf(
            WorldCity("Local Time", TimeZone.getDefault().id),
            WorldCity("New York (America)", "America/New_York"),
            WorldCity("London (Europe)", "Europe/London"),
            WorldCity("Paris (Europe)", "Europe/Paris"),
            WorldCity("Dubai (Asia)", "Asia/Dubai"),
            WorldCity("Tokyo (Asia)", "Asia/Tokyo"),
            WorldCity("Sydney (Australia)", "Australia/Sydney")
        )
    }

    val allTimeZones = remember {
        TimeZone.getAvailableIDs()
            .filter { it.contains("/") }
            .map { id ->
                val parts = id.split("/")
                val region = parts[0].replace("_", " ")
                val city = parts.subList(1, parts.size).joinToString(", ").replace("_", " ")
                WorldCity("$city ($region)", id)
            }
            .distinctBy { it.name }
            .sortedBy { it.name }
    }

    val displayList = if (searchQuery.isBlank()) {
        defaultCities
    } else {
        allTimeZones.filter {
            it.name.contains(searchQuery, ignoreCase = true) || 
            it.timeZoneId.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Public, contentDescription = "World", modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Text("World Clock", style = MaterialTheme.typography.headlineMedium)
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text("Search country, city, or timezone...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(displayList, key = { it.timeZoneId + it.name }) { city ->
                    val tz = TimeZone.getTimeZone(city.timeZoneId)
                    val timeFormat = SimpleDateFormat("HH:mm:ss")
                    timeFormat.timeZone = tz
                    
                    val dateFormat = SimpleDateFormat("EEE, MMM dd")
                    dateFormat.timeZone = tz
                    
                    val timeString = timeFormat.format(Date(currentTime))
                    val dateString = dateFormat.format(Date(currentTime))
                    
                    val offset = tz.getOffset(currentTime) / (1000 * 60 * 60)
                    val offsetString = if (offset >= 0) "GMT+$offset" else "GMT$offset"

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(24.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(city.name, style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("$offsetString • $dateString", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(timeString, style = MaterialTheme.typography.displaySmall)
                        }
                    }
                }
            }
        }
    }
}
