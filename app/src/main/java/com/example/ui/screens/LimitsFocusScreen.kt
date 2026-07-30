package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseRed
import com.example.ui.viewmodel.MainViewModel

@Composable
fun LimitsFocusScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val limits by viewModel.appLimits.collectAsState()
    val focusSessions by viewModel.focusSessions.collectAsState()
    val isTimerRunning by viewModel.isFocusTimerRunning.collectAsState()
    val secondsRemaining by viewModel.focusSecondsRemaining.collectAsState()
    val focusTotalMin by viewModel.focusTotalMinutes.collectAsState()

    var showAddLimitDialog by remember { mutableStateOf(false) }
    var inputPkgName by remember { mutableStateOf("") }
    var inputAppName by remember { mutableStateOf("") }
    var inputLimitMin by remember { mutableStateOf(45) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Pomodoro Focus Timer Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("focus_timer_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Focus Mode & Pomodoro Timer",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Block distracting apps during focus sessions",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    val mins = secondsRemaining / 60
                    val secs = secondsRemaining % 60
                    val timerText = String.format("%02d:%02d", mins, secs)

                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(CircleShape)
                            .background(IndigoPrimary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = timerText,
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = IndigoPrimary,
                                fontSize = 36.sp
                            )
                            Text(
                                text = if (isTimerRunning) "Focusing..." else "Ready",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Preset Buttons
                    if (!isTimerRunning) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf(15, 25, 45, 60).forEach { dur ->
                                FilterChip(
                                    selected = focusTotalMin == dur,
                                    onClick = { viewModel.startFocusTimer(dur) },
                                    label = { Text("${dur}m") },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("focus_preset_${dur}m")
                                )
                            }
                        }
                    } else {
                        Button(
                            onClick = { viewModel.stopFocusTimer() },
                            colors = ButtonDefaults.buttonColors(containerColor = RoseRed),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("stop_focus_button")
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Stop Focus Session")
                        }
                    }
                }
            }
        }

        // Active App Limits Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Configured App Limits (${limits.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(
                    onClick = { showAddLimitDialog = true },
                    modifier = Modifier.testTag("add_limit_fab")
                ) {
                    Icon(Icons.Default.AddCircle, contentDescription = "Add Limit", tint = IndigoPrimary)
                }
            }
        }

        if (limits.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier.padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No app limits set yet. Tap '+' to create custom app screen time limits.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(limits, key = { it.packageName }) { limit ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = limit.appName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Daily Limit: ${limit.dailyLimitMinutes} minutes",
                                style = MaterialTheme.typography.bodySmall,
                                color = IndigoPrimary
                            )
                        }
                        IconButton(onClick = { viewModel.removeAppLimit(limit.packageName) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RoseRed)
                        }
                    }
                }
            }
        }

        // Bedtime Mode Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bedtime_mode_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Bedtime,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Bedtime Mode Schedule",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "10:00 PM - 07:00 AM (Grayscale & Do Not Disturb)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(checked = true, onCheckedChange = {})
                }
            }
        }
    }

    // Add Limit Dialog
    if (showAddLimitDialog) {
        AlertDialog(
            onDismissRequest = { showAddLimitDialog = false },
            title = { Text("Set New App Limit") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = inputAppName,
                        onValueChange = { inputAppName = it },
                        label = { Text("App Name (e.g. Instagram)") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = inputPkgName,
                        onValueChange = { inputPkgName = it },
                        label = { Text("Package Name (e.g. com.instagram.android)") },
                        singleLine = true
                    )
                    Text("Daily Limit: $inputLimitMin minutes")
                    Slider(
                        value = inputLimitMin.toFloat(),
                        onValueChange = { inputLimitMin = it.toInt() },
                        valueRange = 10f..240f,
                        steps = 22
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (inputAppName.isNotBlank() && inputPkgName.isNotBlank()) {
                            viewModel.setAppLimit(inputPkgName, inputAppName, inputLimitMin)
                            showAddLimitDialog = false
                            inputAppName = ""
                            inputPkgName = ""
                        }
                    },
                    modifier = Modifier.testTag("confirm_limit_dialog")
                ) {
                    Text("Save Limit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddLimitDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
