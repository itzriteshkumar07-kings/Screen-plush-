package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.ReportExporter
import com.example.data.repository.UsageTrackerRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val usageRepo = UsageTrackerRepository(application)
    private val db = AppDatabase.getDatabase(application)
    private val reportExporter = ReportExporter(application)

    val hasUsagePermission = MutableStateFlow(usageRepo.hasUsageStatsPermission())

    val dashboardStats = MutableStateFlow<DashboardStats?>(null)
    val allAppsUsage = MutableStateFlow<List<AppUsageInfo>>(emptyList())

    // UI state
    val searchQuery = MutableStateFlow("")
    val selectedCategoryFilter = MutableStateFlow<AppCategory?>(null)
    val sortOption = MutableStateFlow(SortOption.USAGE_TIME)
    val themeMode = MutableStateFlow(AppThemeMode.DARK)

    // Selection
    val selectedAppDetail = MutableStateFlow<AppUsageInfo?>(null)

    // Focus Mode Timer State
    val isFocusTimerRunning = MutableStateFlow(false)
    val focusSecondsRemaining = MutableStateFlow(25 * 60)
    val focusTotalMinutes = MutableStateFlow(25)
    private var focusTimerJob: Job? = null

    // Room persistence
    val appLimits: StateFlow<List<AppLimit>> = db.appLimitDao().getAllLimits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val focusSessions: StateFlow<List<FocusSession>> = db.focusSessionDao().getAllFocusSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notificationLogs: StateFlow<List<NotificationLog>> = db.notificationLogDao().getRecentNotifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            hasUsagePermission.value = usageRepo.hasUsageStatsPermission()
            val stats = usageRepo.getDashboardStats()
            val apps = usageRepo.getInstalledAppsUsage()
            dashboardStats.value = stats
            allAppsUsage.value = apps
        }
    }

    fun openUsageAccessSettings() {
        usageRepo.openUsageAccessSettings()
    }

    // Filtered & Sorted Apps
    val filteredApps: StateFlow<List<AppUsageInfo>> = combine(
        allAppsUsage,
        searchQuery,
        selectedCategoryFilter,
        sortOption
    ) { apps, query, category, sort ->
        var list = apps
        if (query.isNotBlank()) {
            list = list.filter {
                it.appName.contains(query, ignoreCase = true) ||
                        it.packageName.contains(query, ignoreCase = true)
            }
        }
        if (category != null) {
            list = list.filter { it.category == category }
        }
        when (sort) {
            SortOption.USAGE_TIME -> list.sortedByDescending { it.todayUsageMs }
            SortOption.APP_NAME -> list.sortedBy { it.appName }
            SortOption.LAST_OPENED -> list.sortedByDescending { it.lastOpenedTime }
            SortOption.LAUNCH_COUNT -> list.sortedByDescending { it.launchCount }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Limits management
    fun setAppLimit(packageName: String, appName: String, minutes: Int) {
        viewModelScope.launch {
            db.appLimitDao().insertOrUpdateLimit(
                AppLimit(
                    packageName = packageName,
                    appName = appName,
                    dailyLimitMinutes = minutes
                )
            )
        }
    }

    fun removeAppLimit(packageName: String) {
        viewModelScope.launch {
            db.appLimitDao().deleteLimitByPackage(packageName)
        }
    }

    // Focus timer controls
    fun startFocusTimer(minutes: Int) {
        focusTotalMinutes.value = minutes
        focusSecondsRemaining.value = minutes * 60
        isFocusTimerRunning.value = true

        focusTimerJob?.cancel()
        focusTimerJob = viewModelScope.launch {
            while (focusSecondsRemaining.value > 0 && isFocusTimerRunning.value) {
                delay(1000L)
                focusSecondsRemaining.value -= 1
            }
            if (focusSecondsRemaining.value == 0) {
                isFocusTimerRunning.value = false
                db.focusSessionDao().insertSession(
                    FocusSession(
                        title = "Focus Session (${minutes}m)",
                        durationMinutes = minutes,
                        startTime = System.currentTimeMillis(),
                        isCompleted = true,
                        appsBlockedCount = appLimits.value.size
                    )
                )
            }
        }
    }

    fun stopFocusTimer() {
        isFocusTimerRunning.value = false
        focusTimerJob?.cancel()
    }

    // Report exporting
    fun exportAndShareReport(format: ReportFormat) {
        val stats = dashboardStats.value ?: return
        val apps = allAppsUsage.value
        val file: File
        val mimeType: String

        when (format) {
            ReportFormat.PDF -> {
                file = reportExporter.generatePdfReport(stats, apps)
                mimeType = "application/pdf"
            }
            ReportFormat.CSV -> {
                file = reportExporter.generateCsvReport(stats, apps)
                mimeType = "text/csv"
            }
            ReportFormat.JSON -> {
                file = reportExporter.generateJsonReport(stats, apps)
                mimeType = "application/json"
            }
        }
        reportExporter.shareFile(file, mimeType)
    }
}

enum class SortOption(val displayName: String) {
    USAGE_TIME("Usage Time"),
    APP_NAME("App Name"),
    LAST_OPENED("Last Opened"),
    LAUNCH_COUNT("Launches")
}

enum class ReportFormat {
    PDF, CSV, JSON
}
