package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AppUsageInfo
import com.example.ui.components.AppUsageItemRow
import com.example.ui.components.StatCard
import com.example.ui.components.TimelineWaveChart
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.IndigoPrimary
import com.example.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onAppSelected: (AppUsageInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.dashboardStats.collectAsState()
    val apps by viewModel.filteredApps.collectAsState()
    val hasPermission by viewModel.hasUsagePermission.collectAsState()

    val currentStats = stats ?: return

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Permission Banner if not granted
        if (!hasPermission) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("permission_banner"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Usage Access Required",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Grant Usage Access permission to track exact system usage for all apps in real-time.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.openUsageAccessSettings() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.testTag("grant_permission_button")
                        ) {
                            Text("Grant Permission in Settings")
                        }
                    }
                }
            }
        }

        // Hero Card - Screen Time Overview
        item {
            val totalMin = currentStats.totalTimeTodayMs / (1000 * 60)
            val yestMin = currentStats.totalTimeYesterdayMs / (1000 * 60)
            val hours = totalMin / 60
            val mins = totalMin % 60

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_screen_time_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF381E72), Color(0xFF2B2930))
                            ),
                            shape = RoundedCornerShape(28.dp)
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "TODAY'S SCREEN TIME",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = "${hours}h ${mins}m",
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        val targetMin = 240L // 4 hrs goal
                        val progressPct = (totalMin.toFloat() / targetMin.toFloat()).coerceIn(0f, 1f)
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Daily Goal: 4h 00m",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                                Text(
                                    text = "${(progressPct * 100).toInt()}% used",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { progressPct },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = Color.White,
                                trackColor = Color.White.copy(alpha = 0.3f)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Yesterday: ${yestMin / 60}h ${yestMin % 60}m",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            val diff = totalMin - yestMin
                            val trendText = if (diff >= 0) "+${diff}m vs yesterday" else "${diff}m vs yesterday"
                            Text(
                                text = trendText,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Focus Hero Banner Art
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_focus_hero_1785402099445),
                        contentDescription = "Focus Banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color.Black.copy(alpha = 0.7f), Color.Transparent)
                                )
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Column {
                            Text(
                                text = "Digital Wellbeing Focus",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Take back control of your phone time today",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }

        // Quick Metrics Grid
        item {
            Text(
                text = "Usage Diagnostics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatCard(
                        title = "Screen Unlocks",
                        value = "${currentStats.unlockCount} times",
                        icon = Icons.Default.LockOpen,
                        subtext = "Pickups today",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Notifications",
                        value = "${currentStats.notificationsCount}",
                        icon = Icons.Default.Notifications,
                        subtext = "Alerts received",
                        iconColor = CyanAccent,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    val avgMin = currentStats.averageDailyUsageMs / (1000 * 60)
                    StatCard(
                        title = "Daily Average",
                        value = "${avgMin / 60}h ${avgMin % 60}m",
                        icon = Icons.Default.Equalizer,
                        subtext = "Past 7 days",
                        iconColor = EmeraldGreen,
                        modifier = Modifier.weight(1f)
                    )
                    val longestMin = currentStats.longestSessionMs / (1000 * 60)
                    StatCard(
                        title = "Longest Session",
                        value = "${longestMin} min",
                        icon = Icons.Default.Timer,
                        subtext = "Continuous usage",
                        iconColor = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                    StatCard(
                        title = "First Unlock",
                        value = timeFormat.format(Date(currentStats.firstUnlockTime)),
                        icon = Icons.Default.WbSunny,
                        subtext = "Morning start",
                        iconColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Last Usage",
                        value = timeFormat.format(Date(currentStats.lastPhoneUsageTime)),
                        icon = Icons.Default.NightsStay,
                        subtext = "Most recent",
                        iconColor = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Peak Hourly Activity Chart
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
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
                            text = "Peak Hourly Activity",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Icon(
                            imageVector = Icons.Default.ShowChart,
                            contentDescription = null,
                            tint = IndigoPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    val topAppHourly = apps.firstOrNull()?.hourlyUsageMap ?: emptyMap()
                    TimelineWaveChart(hourlyMap = topAppHourly)
                }
            }
        }

        // Top Used Apps Strip
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Most Used Apps Today",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "See All (${apps.size})",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        items(apps.take(5)) { app ->
            AppUsageItemRow(
                app = app,
                onClick = { onAppSelected(app) }
            )
        }
    }
}
