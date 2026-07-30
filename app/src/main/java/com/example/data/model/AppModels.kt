package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Locale

enum class AppCategory(val displayName: String, val iconName: String) {
    SOCIAL("Social", "share"),
    ENTERTAINMENT("Entertainment", "movie"),
    EDUCATION("Education", "school"),
    GAMING("Gaming", "sports_esports"),
    PRODUCTIVITY("Productivity", "work"),
    UTILITIES("Utilities", "build"),
    OTHER("Other", "apps");

    companion object {
        fun fromPackageName(pkg: String): AppCategory {
            val lower = pkg.lowercase(Locale.ROOT)
            return when {
                lower.contains("instagram") || lower.contains("facebook") || lower.contains("twitter") ||
                        lower.contains("snapchat") || lower.contains("whatsapp") || lower.contains("telegram") ||
                        lower.contains("reddit") || lower.contains("linkedin") || lower.contains("tiktok") -> SOCIAL
                lower.contains("youtube") || lower.contains("netflix") || lower.contains("spotify") ||
                        lower.contains("prime") || lower.contains("twitch") || lower.contains("disney") || lower.contains("hulu") -> ENTERTAINMENT
                lower.contains("game") || lower.contains("pubg") || lower.contains("clash") || lower.contains("candy") || lower.contains("roblox") -> GAMING
                lower.contains("mail") || lower.contains("docs") || lower.contains("sheets") || lower.contains("slack") ||
                        lower.contains("notion") || lower.contains("office") || lower.contains("zoom") || lower.contains("teams") -> PRODUCTIVITY
                lower.contains("duolingo") || lower.contains("coursera") || lower.contains("udemy") || lower.contains("khan") || lower.contains("medium") -> EDUCATION
                else -> UTILITIES
            }
        }
    }
}

data class AppUsageInfo(
    val packageName: String,
    val appName: String,
    val category: AppCategory,
    val todayUsageMs: Long,
    val yesterdayUsageMs: Long,
    val weeklyUsageMs: Long,
    val monthlyUsageMs: Long,
    val dailyAverageMs: Long,
    val launchCount: Int,
    val firstOpenedTime: Long,
    val lastOpenedTime: Long,
    val longestSessionMs: Long,
    val shortestSessionMs: Long,
    val totalSessions: Int,
    val foregroundTimeMs: Long,
    val backgroundTimeMs: Long,
    val percentageOfTotal: Float,
    val usageTrendPercent: Float, // positive = increase, negative = decrease
    val hourlyUsageMap: Map<Int, Long>, // 0..23 -> ms used in that hour
    val dailyUsageMap: Map<String, Long>, // e.g. "Mon" -> ms
    val weeklyUsageMap: Map<String, Long>, // e.g. "W1" -> ms
    val monthlyUsageMap: Map<String, Long> // e.g. "Jan" -> ms
)

data class DashboardStats(
    val totalTimeTodayMs: Long,
    val totalTimeYesterdayMs: Long,
    val weeklyTotalTimeMs: Long,
    val monthlyTotalTimeMs: Long,
    val averageDailyUsageMs: Long,
    val unlockCount: Int,
    val notificationsCount: Int,
    val longestSessionMs: Long,
    val chargingTimeMs: Long,
    val firstUnlockTime: Long,
    val lastPhoneUsageTime: Long,
    val deviceUptimeMs: Long,
    val batteryPercent: Int,
    val isCharging: Boolean,
    val productivityScore: Int, // 0..100
    val entertainmentScore: Int, // 0..100
    val wellbeingScore: Int // 0..100
)

@Entity(tableName = "app_limits")
data class AppLimit(
    @PrimaryKey val packageName: String,
    val appName: String,
    val dailyLimitMinutes: Int,
    val isCategoryLimit: Boolean = false,
    val categoryName: String = "",
    val isEnabled: Boolean = true,
    val isLockedAfterLimit: Boolean = true,
    val warningThresholdPercent: Int = 80
)

@Entity(tableName = "focus_sessions")
data class FocusSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val durationMinutes: Int,
    val startTime: Long,
    val isCompleted: Boolean,
    val appsBlockedCount: Int
)

@Entity(tableName = "notification_logs")
data class NotificationLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val packageName: String,
    val appName: String,
    val title: String,
    val text: String,
    val timestamp: Long
)

@Entity(tableName = "app_open_logs")
data class AppOpenLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val packageName: String,
    val appName: String,
    val timestamp: Long
)

data class DailyGoal(
    val maxDailyScreenTimeMinutes: Int = 180, // 3 hours
    val maxDailyUnlocks: Int = 50,
    val currentStreakDays: Int = 5,
    val bestStreakDays: Int = 12
)

enum class AppThemeMode {
    SYSTEM, LIGHT, DARK, AMOLED
}
