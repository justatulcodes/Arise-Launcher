package com.expeknow.ariselauncher.ui.screens.drive

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.expeknow.ariselauncher.data.model.DriveItemType
import com.expeknow.ariselauncher.ui.theme.*

@Composable
fun DriveScreen(
    navController: NavController,
    viewModel: DriveViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            var tempTitle = ""
            var tempDescription = ""
            viewModel.onEvent(DriveEvent.SaveImageFromUri(it, tempTitle, tempDescription))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            DriveHeader()

            DriveFilterChips(
                selectedFilter = state.selectedFilter,
                onFilterSelect = { filter ->
                    viewModel.onEvent(DriveEvent.SelectFilter(filter))
                }
            )

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AccentGreen)
                }
            } else if (state.filteredItems.isEmpty()) {
                EmptyDriveState(hasFilter = state.selectedFilter != null)
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(
                        items = state.filteredItems,
                        key = { it.id }
                    ) { item ->
                        DriveItemCard(
                            item = item,
                            onEdit = { viewModel.onEvent(DriveEvent.StartEditItem(item)) },
                            onDelete = { viewModel.onEvent(DriveEvent.DeleteItem(item.id)) },
                            onVideoClick = { videoUrl ->
                                viewModel.onEvent(DriveEvent.OpenVideo(videoUrl))
                            }
                        )
                    }
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { viewModel.onEvent(DriveEvent.ShowAddDialog) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = AccentGreen,
            contentColor = Color.Black
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add motivation")
        }
    }

    // Add/Edit Dialog
    if (state.showAddDialog) {
        AddDriveItemDialog(
            itemType = state.selectedItemType,
            editingItem = state.editingItem,
            onDismiss = { viewModel.onEvent(DriveEvent.HideAddDialog) },
            onTypeSelect = { viewModel.onEvent(DriveEvent.SelectItemType(it)) },
            onSave = { type, content, title, author, description ->
                if (state.editingItem != null) {
                    viewModel.onEvent(
                        DriveEvent.UpdateItem(
                            state.editingItem!!.copy(
                                type = type,
                                content = content,
                                title = title,
                                author = author,
                                description = description
                            )
                        )
                    )
                } else {
                    viewModel.onEvent(
                        DriveEvent.AddItem(
                            type = type,
                            content = content,
                            title = title,
                            author = author,
                            description = description
                        )
                    )
                }
            },
            onImagePick = {
                imagePickerLauncher.launch("image/*")
            },
            isSavingImage = state.isSavingImage
        )
    }
}

@Composable
private fun DriveFilterChips(
    selectedFilter: DriveItemType?,
    onFilterSelect: (DriveItemType?) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Black
    ) {
        LazyRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedFilter == null,
                    onClick = { onFilterSelect(null) },
                    label = { Text("All") },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = SurfaceCard,
                        labelColor = Color.White,
                        selectedContainerColor = AccentGreen,
                        selectedLabelColor = Color.Black
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selectedFilter == null,
                        borderColor = DividerGray,
                        selectedBorderColor = AccentGreen
                    )
                )
            }

            items(DriveItemType.entries) { type ->
                FilterChip(
                    selected = selectedFilter == type,
                    onClick = { onFilterSelect(type) },
                    label = {
                        Text(
                            when (type) {
                                DriveItemType.QUOTE -> "Quotes"
                                DriveItemType.IMAGE -> "Images"
                                DriveItemType.VIDEO -> "Videos"
                                DriveItemType.TODO -> "Bucket List"
                            }
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = SurfaceCard,
                        labelColor = Color.White,
                        selectedContainerColor = AccentGreen,
                        selectedLabelColor = Color.Black
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selectedFilter == type,
                        borderColor = DividerGray,
                        selectedBorderColor = AccentGreen
                    )
                )
            }
        }
    }
}

@Composable
private fun DriveHeader() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "YOUR DRIVE",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                ),
                color = Color.White
            )
            Text(
                text = "Stay motivated with quotes, images, and videos",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.6f),
            )
        }
    }
}

@Composable
private fun EmptyDriveState(hasFilter: Boolean = false) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (hasFilter) "No items in this category" else "No motivational content yet",
                style = MaterialTheme.typography.titleMedium,
                color = TaskTitle,
                textAlign = TextAlign.Center
            )
            Text(
                text = if (hasFilter) {
                    "Try selecting a different filter or add new content"
                } else {
                    "Add quotes, images, or videos to keep yourself motivated"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = BannerTextGray,
                modifier = Modifier.padding(horizontal = 24.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}