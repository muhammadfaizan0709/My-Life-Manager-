package com.example.ui

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.util.Calendar

@Composable
fun AlarmScreen(viewModel: MainViewModel) {
    val alarms by viewModel.alarms.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showLabelDialog by remember { mutableStateOf(false) }
    var pendingHour by remember { mutableStateOf(0) }
    var pendingMinute by remember { mutableStateOf(0) }
    var alarmLabel by remember { mutableStateOf("") }

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            pendingHour = hourOfDay
            pendingMinute = minute
            showLabelDialog = true
        },
        Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
        Calendar.getInstance().get(Calendar.MINUTE),
        false
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { timePickerDialog.show() }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Alarm")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Text("Alarms", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(16.dp))
            
            if (alarms.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No alarms set.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(alarms, key = { it.id }) { alarm ->
                        val timeString = String.format("%02d:%02d", if (alarm.hour % 12 == 0) 12 else alarm.hour % 12, alarm.minute)
                        val amPm = if (alarm.hour >= 12) "PM" else "AM"
                        
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
                                    Row(verticalAlignment = Alignment.Bottom) {
                                        Text(timeString, style = MaterialTheme.typography.displayMedium)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(amPm, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
                                    }
                                    if (alarm.label.isNotBlank()) {
                                        Text(alarm.label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Switch(
                                        checked = alarm.isEnabled,
                                        onCheckedChange = { viewModel.toggleAlarm(alarm) }
                                    )
                                    IconButton(onClick = { viewModel.deleteAlarm(alarm.id) }) {
                                        Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showLabelDialog) {
            AlertDialog(
                onDismissRequest = { showLabelDialog = false },
                title = { Text("Alarm Label") },
                text = {
                    OutlinedTextField(
                        value = alarmLabel,
                        onValueChange = { alarmLabel = it },
                        label = { Text("Optional Label") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.addAlarm(pendingHour, pendingMinute, alarmLabel)
                        showLabelDialog = false
                        alarmLabel = ""
                    }) { Text("Save") }
                },
                dismissButton = {
                    TextButton(onClick = { showLabelDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}
