package com.expeknow.ariselauncher.ui.screens.drive

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.expeknow.ariselauncher.data.model.DriveItem
import com.expeknow.ariselauncher.data.model.DriveItemType
import com.expeknow.ariselauncher.ui.theme.*

@Composable
fun DriveItemCard(
    item: DriveItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = SurfaceCard
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with type and actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when (item.type) {
                            DriveItemType.QUOTE -> Icons.Default.FormatQuote
                            DriveItemType.IMAGE -> Icons.Default.Image
                            DriveItemType.VIDEO -> Icons.Default.PlayCircle
                        },
                        contentDescription = null,
                        tint = AccentGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = item.type.name,
                        style = MaterialTheme.typography.labelSmall,
                        color = AccentGreen
                    )
                }

                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "More options",
                            tint = Color.White
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = {
                                showMenu = false
                                onEdit()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                showMenu = false
                                onDelete()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Delete, contentDescription = null)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Content based on type
            when (item.type) {
                DriveItemType.QUOTE -> {
                    Text(
                        text = "\"${item.content}\"",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TaskTitle,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    if (item.author.isNotEmpty()) {
                        Text(
                            text = "— ${item.author}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic,
                            color = BannerTextGray
                        )
                    }
                }

                DriveItemType.IMAGE -> {
                    if (item.title.isNotEmpty()) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = TaskTitle,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    AsyncImage(
                        model = item.content,
                        contentDescription = item.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    if (item.description.isNotEmpty()) {
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = BannerTextGray,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                DriveItemType.VIDEO -> {
                    if (item.title.isNotEmpty()) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = TaskTitle,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    // Video preview box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.5f))
                            .border(1.dp, DividerGray, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.PlayCircle,
                                contentDescription = "Video",
                                tint = AccentGreen,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = item.content,
                                style = MaterialTheme.typography.bodySmall,
                                color = BannerTextGray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                    if (item.description.isNotEmpty()) {
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = BannerTextGray,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddDriveItemDialog(
    itemType: DriveItemType,
    editingItem: DriveItem?,
    onDismiss: () -> Unit,
    onTypeSelect: (DriveItemType) -> Unit,
    onSave: (DriveItemType, String, String, String, String) -> Unit
) {
    var selectedType by remember(editingItem) { mutableStateOf(editingItem?.type ?: itemType) }
    var content by remember(editingItem) { mutableStateOf(editingItem?.content ?: "") }
    var title by remember(editingItem) { mutableStateOf(editingItem?.title ?: "") }
    var author by remember(editingItem) { mutableStateOf(editingItem?.author ?: "") }
    var description by remember(editingItem) { mutableStateOf(editingItem?.description ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SurfaceCard
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = if (editingItem != null) "Edit Item" else "Add Motivation",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Type selector
                Text(
                    text = "Type",
                    style = MaterialTheme.typography.labelMedium,
                    color = BannerTextGray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DriveItemType.entries.forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = {
                                selectedType = type
                                onTypeSelect(type)
                            },
                            label = { Text(type.name) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AccentGreen,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Dynamic fields based on type
                when (selectedType) {
                    DriveItemType.QUOTE -> {
                        OutlinedTextField(
                            value = content,
                            onValueChange = { content = it },
                            label = { Text("Quote") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentGreen,
                                focusedLabelColor = AccentGreen,
                                cursorColor = AccentGreen
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = author,
                            onValueChange = { author = it },
                            label = { Text("Author (optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentGreen,
                                focusedLabelColor = AccentGreen,
                                cursorColor = AccentGreen
                            )
                        )
                    }

                    DriveItemType.IMAGE -> {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Title") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentGreen,
                                focusedLabelColor = AccentGreen,
                                cursorColor = AccentGreen
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = content,
                            onValueChange = { content = it },
                            label = { Text("Image URL") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentGreen,
                                focusedLabelColor = AccentGreen,
                                cursorColor = AccentGreen
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description (optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentGreen,
                                focusedLabelColor = AccentGreen,
                                cursorColor = AccentGreen
                            )
                        )
                    }

                    DriveItemType.VIDEO -> {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Title") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentGreen,
                                focusedLabelColor = AccentGreen,
                                cursorColor = AccentGreen
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = content,
                            onValueChange = { content = it },
                            label = { Text("Video URL") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentGreen,
                                focusedLabelColor = AccentGreen,
                                cursorColor = AccentGreen
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description (optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentGreen,
                                focusedLabelColor = AccentGreen,
                                cursorColor = AccentGreen
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = BannerTextGray)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (content.isNotEmpty()) {
                                onSave(selectedType, content, title, author, description)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentGreen,
                            contentColor = Color.Black
                        ),
                        enabled = content.isNotEmpty()
                    ) {
                        Text(if (editingItem != null) "Update" else "Save")
                    }
                }
            }
        }
    }
}