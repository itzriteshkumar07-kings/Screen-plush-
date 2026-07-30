package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppCategory
import com.example.data.model.AppUsageInfo
import com.example.ui.components.AppUsageItemRow
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.SortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppsListScreen(
    viewModel: MainViewModel,
    onAppSelected: (AppUsageInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    val apps by viewModel.filteredApps.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategoryFilter.collectAsState()
    val currentSort by viewModel.sortOption.collectAsState()

    var showSortMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Search Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.searchQuery.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("app_search_input"),
            placeholder = { Text("Search installed apps...") },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Sorting & Filter Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${apps.size} Apps Found",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Box {
                FilterChip(
                    selected = true,
                    onClick = { showSortMenu = true },
                    label = { Text("Sort: ${currentSort.displayName}") },
                    leadingIcon = { Icon(Icons.Default.Sort, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.testTag("sort_dropdown_button")
                )

                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false }
                ) {
                    SortOption.values().forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.displayName) },
                            onClick = {
                                viewModel.sortOption.value = option
                                showSortMenu = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Category Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { viewModel.selectedCategoryFilter.value = null },
                    label = { Text("All") },
                    modifier = Modifier.testTag("category_chip_all")
                )
            }
            items(AppCategory.values()) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = {
                        viewModel.selectedCategoryFilter.value =
                            if (selectedCategory == category) null else category
                    },
                    label = { Text(category.displayName) },
                    modifier = Modifier.testTag("category_chip_${category.name.lowercase()}")
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Apps List
        if (apps.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Apps,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No matching apps found",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(apps, key = { it.packageName }) { app ->
                    AppUsageItemRow(
                        app = app,
                        onClick = { onAppSelected(app) }
                    )
                }
            }
        }
    }
}
