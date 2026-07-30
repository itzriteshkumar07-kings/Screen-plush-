package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppUsageInfo
import com.example.ui.components.CategoryBadge
import com.example.ui.components.StatCard
import com.example.ui.components.TrendBadge
import com.example.ui.components.UsageBarChart
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.IndigoPrimary
import com.example.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDetailScreen(
    app: AppUsageInfo,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val limits by viewModel.appLimits.collectAsState()
    val existingLimit = limits.find { it.packageName == app.packageName }

    var limitMinutes by remember(existingLimit) { mutableStateOf(existingLimit?.dailyLimitMinutes ?: 60) }
    var selectedGraphTab by remember { mutableStateOf(0) } // 0: Hourly, 1: Daily, 2: Weekly, 3: Monthly

    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(app.appName, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(IndigoPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = app.appName.take(1).uppercase(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = IndigoPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = app.appName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = app.packageName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CategoryBadge(category = app.category)
                            Spacer(modifier = Modifier.width(8.dp))
                            TrendBadge(trendPercent = app.usageTrendPercent)
                        }
                    }
                }
            }

            // High Level Stats Summary Grid
            Text(
                text = "Screen Time Diagnostics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            val todayMin = app.todayUsageMs / (1000 * 60)
            val yestMin = app.yesterdayUsageMs / (1000 * 60)
            val weekMin = app.weeklyUsageMs / (1000 * 60)
            val monthMin = app.monthlyUsageMs / (1000 * 60)
            val avgMin = app.dailyAverageMs / (1000 * 60)

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(
                    title = "Today Usage",
                    value = "${todayMin / 60}h ${todayMin % 60}m",
                    icon = Icons.Default.Today,
                    subtext = "${String.format("%.1f", app.percentageOfTotal)}% of total",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Yesterday",
                    value = "${yestMin / 60}h ${yestMin % 60}m",
                    icon = Icons.Default.History,
                    iconColor = CyanAccent,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(
                    title = "Weekly Total",
                    value = "${weekMin / 60}h ${weekMin % 60}m",
                    icon = Icons.Default.DateRange,
                    iconColor = EmeraldGreen,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Monthly Total",
                    value = "${monthMin / 60}h ${monthMin % 60}m",
                    icon = Icons.Default.CalendarMonth,
                    iconColor = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f)
                )
            }

            // Session & Timings Grid
            Text(
                text = "Session Details & Activity Timings",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DetailRow(label = "App Launches Today", value = "${app.launchCount} times")
                    DetailRow(label = "Total Sessions", value = "${app.totalSessions}")
                    DetailRow(
                        label = "First Opened",
                        value = if (app.firstOpenedTime > 0) timeFormat.format(Date(app.firstOpenedTime)) else "N/A"
                    )
                    DetailRow(
                        label = "Last Opened",
                        value = if (app.lastOpenedTime > 0) timeFormat.format(Date(app.lastOpenedTime)) else "N/A"
                    )
                    DetailRow(
                        label = "Longest Single Session",
                        value = "${app.longestSessionMs / (1000 * 60)} min"
                    )
                    DetailRow(
                        label = "Shortest Session",
                        value = "${app.shortestSessionMs / 1000} sec"
                    )
                    DetailRow(
                        label = "Foreground Time",
                        value = "${app.foregroundTimeMs / (1000 * 60)} min"
                    )
                    DetailRow(
                        label = "Background Time",
                        value = "${app.backgroundTimeMs / (1000 * 60)} min"
                    )
                }
            }

            // Usage Graphs Section
            Text(
                text = "Usage Trends & Analytics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    TabRow(selectedTabIndex = selectedGraphTab) {
                        Tab(
                            selected = selectedGraphTab == 0,
                            onClick = { selectedGraphTab = 0 },
                            text = { Text("Hourly") }
                        )
                        Tab(
                            selected = selectedGraphTab == 1,
                            onClick = { selectedGraphTab = 1 },
                            text = { Text("Daily") }
                        )
                        Tab(
                            selected = selectedGraphTab == 2,
                            onClick = { selectedGraphTab = 2 },
                            text = { Text("Weekly") }
                        )
                        Tab(
                            selected = selectedGraphTab == 3,
                            onClick = { selectedGraphTab = 3 },
                            text = { Text("Monthly") }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val graphData = when (selectedGraphTab) {
                        0 -> app.hourlyUsageMap.mapKeys { "${it.key}h" }
                        1 -> app.dailyUsageMap
                        2 -> app.weeklyUsageMap
                        else -> app.monthlyUsageMap
                    }

                    UsageBarChart(dataMap = graphData)
                }
            }

            // Set App Limit Card
            Text(
                text = "App Screen Limit",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("app_limit_setting_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daily Screen Limit",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$limitMinutes minutes (${limitMinutes / 60}h ${limitMinutes % 60}m)",
                            style = MaterialTheme.typography.labelLarge,
                            color = IndigoPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Slider(
                        value = limitMinutes.toFloat(),
                        onValueChange = { limitMinutes = it.toInt() },
                        valueRange = 10f..300f,
                        steps = 28,
                        modifier = Modifier.testTag("limit_slider")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        if (existingLimit != null) {
                            OutlinedButton(
                                onClick = { viewModel.removeAppLimit(app.packageName) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("Remove Limit")
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                        }
                        Button(
                            onClick = {
                                viewModel.setAppLimit(app.packageName, app.appName, limitMinutes)
                            },
                            modifier = Modifier.testTag("save_limit_button")
                        ) {
                            Text("Set App Limit")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
