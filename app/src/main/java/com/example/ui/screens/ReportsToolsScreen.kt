package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.DailyGoal
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.IndigoPrimary
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.ReportFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReportsToolsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.dashboardStats.collectAsState()
    val notificationLogs by viewModel.notificationLogs.collectAsState()
    val goal = remember { DailyGoal() }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Export Reports Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reports_exporter_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = IndigoPrimary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Export & Share Usage Reports",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Generate comprehensive screen time reports in PDF, CSV, or JSON formats.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.exportAndShareReport(ReportFormat.PDF) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("export_pdf_button")
                        ) {
                            Text("PDF")
                        }
                        Button(
                            onClick = { viewModel.exportAndShareReport(ReportFormat.CSV) },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("export_csv_button")
                        ) {
                            Text("CSV")
                        }
                        Button(
                            onClick = { viewModel.exportAndShareReport(ReportFormat.JSON) },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("export_json_button")
                        ) {
                            Text("JSON")
                        }
                    }
                }
            }
        }

        // Goal & Streak Tracking Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("streak_tracker_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = IndigoPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Streaks & Wellbeing Goals",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "🔥 ${goal.currentStreakDays} Days Streak",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = IndigoPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Current Streak", style = MaterialTheme.typography.labelSmall)
                                Text("${goal.currentStreakDays} Days", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Best Record", style = MaterialTheme.typography.labelSmall)
                                Text("${goal.bestStreakDays} Days", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Battery Usage Correlation Card
        item {
            stats?.let { currentStats ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.BatteryChargingFull, contentDescription = null, tint = EmeraldGreen)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Battery & Display Correlation",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Battery Level: ${currentStats.batteryPercent}% • Display consuming ~35% total power",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Recent Notifications History Log
        item {
            Text(
                text = "Recent Notifications Log",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (notificationLogs.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "No notification events captured yet.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(notificationLogs.take(5)) { log ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = log.appName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            val tf = SimpleDateFormat("hh:mm a", Locale.getDefault())
                            Text(
                                text = tf.format(Date(log.timestamp)),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (log.title.isNotBlank()) {
                            Text(text = log.title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}
