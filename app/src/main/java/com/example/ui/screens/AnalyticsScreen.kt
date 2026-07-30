package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppUsageInfo
import com.example.ui.components.CategoryPieChart
import com.example.ui.components.UsageBarChart
import com.example.ui.components.UsageHeatmap
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseRed
import com.example.ui.viewmodel.MainViewModel

@Composable
fun AnalyticsScreen(
    viewModel: MainViewModel,
    onAppSelected: (AppUsageInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.dashboardStats.collectAsState()
    val apps by viewModel.filteredApps.collectAsState()
    val currentStats = stats ?: return

    var selectedReportTab by remember { mutableStateOf(0) } // 0: Daily, 1: Weekly, 2: Monthly

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Wellbeing & Productivity Scores
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("wellbeing_scores_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Digital Wellbeing Index",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(IndigoPrimary.copy(alpha = 0.12f))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "Productivity Score",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${currentStats.productivityScore}/100",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = IndigoPrimary
                            )
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(CyanAccent.copy(alpha = 0.12f))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "Entertainment Score",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${currentStats.entertainmentScore}/100",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = CyanAccent
                            )
                        }
                    }
                }
            }
        }

        // Category Pie Chart
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("pie_chart_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Category Distribution",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CategoryPieChart(apps = apps)
                }
            }
        }

        // Timeline Bar Chart Comparison
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bar_chart_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    TabRow(selectedTabIndex = selectedReportTab) {
                        Tab(
                            selected = selectedReportTab == 0,
                            onClick = { selectedReportTab = 0 },
                            text = { Text("Daily") }
                        )
                        Tab(
                            selected = selectedReportTab == 1,
                            onClick = { selectedReportTab = 1 },
                            text = { Text("Weekly") }
                        )
                        Tab(
                            selected = selectedReportTab == 2,
                            onClick = { selectedReportTab = 2 },
                            text = { Text("Monthly") }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val comparisonData = when (selectedReportTab) {
                        0 -> mapOf(
                            "Today" to currentStats.totalTimeTodayMs,
                            "Yesterday" to currentStats.totalTimeYesterdayMs,
                            "Avg Daily" to currentStats.averageDailyUsageMs
                        )
                        1 -> mapOf(
                            "W1" to (currentStats.weeklyTotalTimeMs * 0.22f).toLong(),
                            "W2" to (currentStats.weeklyTotalTimeMs * 0.28f).toLong(),
                            "W3" to (currentStats.weeklyTotalTimeMs * 0.24f).toLong(),
                            "W4" to (currentStats.weeklyTotalTimeMs * 0.26f).toLong()
                        )
                        else -> mapOf(
                            "Jan" to (currentStats.monthlyTotalTimeMs * 0.18f).toLong(),
                            "Feb" to (currentStats.monthlyTotalTimeMs * 0.22f).toLong(),
                            "Mar" to (currentStats.monthlyTotalTimeMs * 0.20f).toLong(),
                            "Apr" to (currentStats.monthlyTotalTimeMs * 0.24f).toLong(),
                            "May" to (currentStats.monthlyTotalTimeMs * 0.16f).toLong()
                        )
                    }

                    UsageBarChart(dataMap = comparisonData)
                }
            }
        }

        // Usage Heatmap
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("heatmap_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    UsageHeatmap()
                }
            }
        }

        // Most Used vs Least Used Rankings
        item {
            Text(
                text = "Most Used Apps",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        items(apps.take(3)) { app ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                onClick = { onAppSelected(app) }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = app.appName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    val min = app.todayUsageMs / (1000 * 60)
                    Text(
                        text = "${min / 60}h ${min % 60}m",
                        style = MaterialTheme.typography.labelMedium,
                        color = IndigoPrimary
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Least Used Apps",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        items(apps.takeLast(3).reversed()) { app ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                onClick = { onAppSelected(app) }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = app.appName,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.weight(1f)
                    )
                    val min = app.todayUsageMs / (1000 * 60)
                    Text(
                        text = "${min}m",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
