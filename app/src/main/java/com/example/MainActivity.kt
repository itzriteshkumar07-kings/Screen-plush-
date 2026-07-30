package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.screens.*
import com.example.ui.theme.ScreenTimeTrackerTheme
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by viewModel.themeMode.collectAsState()

            ScreenTimeTrackerTheme(themeMode = themeMode) {
                ScreenTimeTrackerApp(viewModel = viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshData()
    }
}

enum class ScreenTab(val route: String, val title: String, val icon: ImageVector) {
    DASHBOARD("dashboard", "Dashboard", Icons.Default.Dashboard),
    APPS("apps", "Apps", Icons.Default.Apps),
    ANALYTICS("analytics", "Analytics", Icons.Default.BarChart),
    LIMITS("limits", "Limits & Focus", Icons.Default.Timer),
    REPORTS("reports", "Reports", Icons.Default.Assessment),
    SETTINGS("settings", "Settings", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenTimeTrackerApp(viewModel: MainViewModel) {
    var currentTab by remember { mutableStateOf(ScreenTab.DASHBOARD) }
    val selectedAppDetail by viewModel.selectedAppDetail.collectAsState()

    if (selectedAppDetail != null) {
        AppDetailScreen(
            app = selectedAppDetail!!,
            viewModel = viewModel,
            onBack = { viewModel.selectedAppDetail.value = null }
        )
    } else {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = currentTab.title,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    actions = {
                        IconButton(onClick = { viewModel.refreshData() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh Data")
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar(
                    modifier = Modifier.testTag("bottom_navigation_bar")
                ) {
                    ScreenTab.values().forEach { tab ->
                        NavigationBarItem(
                            selected = currentTab == tab,
                            onClick = { currentTab = tab },
                            icon = { Icon(imageVector = tab.icon, contentDescription = tab.title) },
                            label = { Text(tab.title) },
                            modifier = Modifier.testTag("nav_item_${tab.route}")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    ScreenTab.DASHBOARD -> DashboardScreen(
                        viewModel = viewModel,
                        onAppSelected = { viewModel.selectedAppDetail.value = it }
                    )
                    ScreenTab.APPS -> AppsListScreen(
                        viewModel = viewModel,
                        onAppSelected = { viewModel.selectedAppDetail.value = it }
                    )
                    ScreenTab.ANALYTICS -> AnalyticsScreen(
                        viewModel = viewModel,
                        onAppSelected = { viewModel.selectedAppDetail.value = it }
                    )
                    ScreenTab.LIMITS -> LimitsFocusScreen(viewModel = viewModel)
                    ScreenTab.REPORTS -> ReportsToolsScreen(viewModel = viewModel)
                    ScreenTab.SETTINGS -> SettingsScreen(viewModel = viewModel)
                }
            }
        }
    }
}
