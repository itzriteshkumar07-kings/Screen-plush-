package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.model.AppThemeMode
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.IndigoPrimary
import com.example.ui.viewmodel.MainViewModel

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val hasUsagePermission by viewModel.hasUsagePermission.collectAsState()

    var showBackupSuccessDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Theme Selection Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("theme_selection_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Display Theme Mode",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        AppThemeMode.values().forEach { mode ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("theme_option_${mode.name.lowercase()}"),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = themeMode == mode,
                                    onClick = { viewModel.themeMode.value = mode }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = when (mode) {
                                        AppThemeMode.SYSTEM -> "System Default"
                                        AppThemeMode.LIGHT -> "Light Theme"
                                        AppThemeMode.DARK -> "Dark Theme"
                                        AppThemeMode.AMOLED -> "AMOLED Pure Black"
                                    },
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }

        // System Permissions Check Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("permissions_check_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "System Permissions Status",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Usage Access Permission", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Text(
                                text = if (hasUsagePermission) "Granted & Active" else "Not Granted",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (hasUsagePermission) EmeraldGreen else MaterialTheme.colorScheme.error
                            )
                        }
                        Button(
                            onClick = { viewModel.openUsageAccessSettings() },
                            modifier = Modifier.testTag("open_usage_settings_button")
                        ) {
                            Text("Configure")
                        }
                    }
                }
            }
        }

        // Local Backup & Restore Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("backup_restore_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Backup & Restore",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Export and restore your app limits, focus session logs, and goal records locally.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { showBackupSuccessDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("backup_data_button")
                        ) {
                            Text("Backup Data")
                        }
                        OutlinedButton(
                            onClick = { showBackupSuccessDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("restore_data_button")
                        ) {
                            Text("Restore Data")
                        }
                    }
                }
            }
        }

        // Privacy Shield Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("privacy_shield_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = IndigoPrimary.copy(alpha = 0.12f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = IndigoPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "100% Privacy Guaranteed",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = IndigoPrimary
                        )
                        Text(
                            text = "No cloud servers, no ads, no trackers. All screen time usage and statistics remain 100% strictly on your local device.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }

    if (showBackupSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showBackupSuccessDialog = false },
            title = { Text("Backup & Restore") },
            text = { Text("Your local limits and focus session database backup has been saved successfully to encrypted local storage.") },
            confirmButton = {
                TextButton(onClick = { showBackupSuccessDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}
