package com.example.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun ToolsScreen(onNavigate: (String) -> Unit) {
    val toolModules = listOf(
        ToolModule("Journal", Icons.Filled.Book, "journal"),
        ToolModule("Goals", Icons.Filled.Flag, "goals"),
        ToolModule("Notes", Icons.Filled.Notes, "notes"),
        ToolModule("Habits", Icons.Filled.Repeat, "habits"),
        ToolModule("Quotes", Icons.Filled.FormatQuote, "quotes"),
        ToolModule("Stopwatch", Icons.Filled.Timer, "stopwatch"),
        ToolModule("Flashlight", Icons.Filled.Highlight, "flashlight"),
        ToolModule("Vault", Icons.Filled.Lock, "vault"),
        ToolModule("Alarm", Icons.Filled.Alarm, "alarm"),
        ToolModule("World Clock", Icons.Filled.Public, "world_clock"),
        ToolModule("Settings", Icons.Filled.Settings, "settings")
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Tools & More",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(toolModules) { module ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clickable { onNavigate(module.route) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = module.icon,
                            contentDescription = module.title,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = module.title,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }
}

data class ToolModule(val title: String, val icon: ImageVector, val route: String)
