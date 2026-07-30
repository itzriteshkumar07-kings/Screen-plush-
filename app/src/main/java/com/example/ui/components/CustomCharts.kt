package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppCategory
import com.example.data.model.AppUsageInfo
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseRed
import com.example.ui.theme.AmberOrange

@Composable
fun CategoryPieChart(
    apps: List<AppUsageInfo>,
    modifier: Modifier = Modifier
) {
    val categoryTotals = remember(apps) {
        AppCategory.values().map { cat ->
            val total = apps.filter { it.category == cat }.sumOf { it.todayUsageMs }
            cat to total
        }.filter { it.second > 0 }
    }

    val totalTime = categoryTotals.sumOf { it.second }.toFloat()
    val categoryColors = remember {
        mapOf(
            AppCategory.SOCIAL to RoseRed,
            AppCategory.ENTERTAINMENT to AmberOrange,
            AppCategory.PRODUCTIVITY to IndigoPrimary,
            AppCategory.EDUCATION to CyanAccent,
            AppCategory.GAMING to Color(0xFFA855F7),
            AppCategory.UTILITIES to EmeraldGreen,
            AppCategory.OTHER to Color(0xFF64748B)
        )
    }

    var selectedCategory by remember { mutableStateOf<AppCategory?>(null) }
    val progress = remember { Animatable(0f) }

    LaunchedEffect(apps) {
        progress.animateTo(1f, animationSpec = tween(1000))
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(220.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                if (totalTime <= 0f) return@Canvas
                var startAngle = -90f
                val strokeWidth = 32.dp.toPx()

                categoryTotals.forEach { (cat, duration) ->
                    val sweepAngle = (duration.toFloat() / totalTime) * 360f * progress.value
                    val color = categoryColors[cat] ?: Color.Gray

                    drawArc(
                        color = color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                        size = Size(size.width - strokeWidth, size.height - strokeWidth),
                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
                    )
                    startAngle += sweepAngle
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = selectedCategory?.displayName ?: "Categories",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                val displayTimeMin = if (selectedCategory != null) {
                    (categoryTotals.find { it.first == selectedCategory }?.second ?: 0L) / (1000 * 60)
                } else {
                    (totalTime / (1000 * 60)).toLong()
                }
                Text(
                    text = "${displayTimeMin / 60}h ${displayTimeMin % 60}m",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Legend tags grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            categoryTotals.forEach { (cat, duration) ->
                val color = categoryColors[cat] ?: Color.Gray
                val min = duration / (1000 * 60)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { selectedCategory = if (selectedCategory == cat) null else cat }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${cat.displayName} (${min}m)",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = if (selectedCategory == cat) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun UsageBarChart(
    dataMap: Map<String, Long>,
    modifier: Modifier = Modifier,
    barColor: Color = IndigoPrimary
) {
    if (dataMap.isEmpty()) return
    val maxVal = remember(dataMap) { maxOf(1L, dataMap.values.maxOrNull() ?: 1L).toFloat() }
    val animateVal = remember { Animatable(0f) }

    LaunchedEffect(dataMap) {
        animateVal.animateTo(1f, animationSpec = tween(800))
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val barWidth = size.width / (dataMap.size * 1.8f)
                val spacing = (size.width - (barWidth * dataMap.size)) / (dataMap.size + 1)

                var x = spacing
                dataMap.values.forEach { valMs ->
                    val barHeight = (valMs.toFloat() / maxVal) * (size.height - 20.0f) * animateVal.value
                    val top = size.height - barHeight

                    drawRoundRect(
                        color = barColor,
                        topLeft = Offset(x, top),
                        size = Size(barWidth, barHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f)
                    )
                    x += barWidth + spacing
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            dataMap.keys.forEach { label ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun UsageHeatmap(
    modifier: Modifier = Modifier
) {
    val days = listOf("M", "T", "W", "T", "F", "S", "S")
    val weeks = 4

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Usage Heatmap (Past 30 Days)",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (w in 0 until weeks) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (d in 0 until 7) {
                        val level = (d * w + 3) % 5
                        val color = when (level) {
                            0 -> MaterialTheme.colorScheme.surfaceVariant
                            1 -> IndigoPrimary.copy(alpha = 0.3f)
                            2 -> IndigoPrimary.copy(alpha = 0.55f)
                            3 -> IndigoPrimary.copy(alpha = 0.8f)
                            else -> IndigoPrimary
                        }
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(color)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TimelineWaveChart(
    hourlyMap: Map<Int, Long>,
    modifier: Modifier = Modifier
) {
    val maxVal = remember(hourlyMap) { maxOf(1L, hourlyMap.values.maxOrNull() ?: 1L).toFloat() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stepX = size.width / 23f
            val path = Path()

            path.moveTo(0f, size.height)
            for (h in 0..23) {
                val valMs = hourlyMap[h] ?: 0L
                val x = h * stepX
                val y = size.height - ((valMs.toFloat() / maxVal) * (size.height - 15f))
                if (h == 0) {
                    path.lineTo(x, y)
                } else {
                    val prevX = (h - 1) * stepX
                    val prevValMs = hourlyMap[h - 1] ?: 0L
                    val prevY = size.height - ((prevValMs.toFloat() / maxVal) * (size.height - 15f))
                    val cx = (prevX + x) / 2f
                    path.cubicTo(cx, prevY, cx, y, x, y)
                }
            }
            path.lineTo(size.width, size.height)
            path.close()

            drawPath(
                path = path,
                color = CyanAccent.copy(alpha = 0.4f)
            )
        }
    }
}
