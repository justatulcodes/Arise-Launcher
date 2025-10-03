package com.expeknow.ariselauncher.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.expeknow.ariselauncher.data.model.*

@Composable
fun TaskDetailsHeader(
    onBackClick: () -> Unit,
    theme: TaskDetailsTheme
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onBackClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White.copy(alpha = 0.6f)
            ),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                Icons.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            "TASK DETAILS",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.2.sp
        )
    }
}

@Composable
fun PointsRewardSection(
    points: Int,
    theme: TaskDetailsTheme
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .border(
                    2.dp,
                    theme.border,
                    RoundedCornerShape(16.dp)
                )
                .background(
                    Color.Transparent,
                    RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 32.dp, vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.EmojiEvents,
                        contentDescription = "Trophy",
                        tint = theme.accent,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "$points",
                        style = MaterialTheme.typography.displayMedium,
                        color = theme.accent,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "POINTS REWARD",
                    style = MaterialTheme.typography.labelSmall,
                    color = theme.textSecondary,
                    letterSpacing = 1.sp,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun EnhancedTaskDetailsCard(
    task: Task,
    onToggleTask: (String) -> Unit,
    theme: TaskDetailsTheme,
    isEditingTitle: Boolean = false,
    isEditingDescription: Boolean = false,
    editingTitleText: String = "",
    editingDescriptionText: String = "",
    onStartEditingTitle: () -> Unit = {},
    onStartEditingDescription: () -> Unit = {},
    onUpdateTitleText: (String) -> Unit = {},
    onUpdateDescriptionText: (String) -> Unit = {},
    onSaveTitle: () -> Unit = {},
    onSaveDescription: () -> Unit = {},
    onCancelEditing: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .background(
                theme.surface,
                RoundedCornerShape(12.dp)
            )
            .border(
                1.dp,
                theme.border,
                RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = { onToggleTask(task.id) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color.White,
                        uncheckedColor = Color.White.copy(alpha = 0.4f),
                        checkmarkColor = Color.Black
                    ),
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Title - editable
                if (isEditingTitle) {
                    val focusRequester = remember { FocusRequester() }

                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = editingTitleText,
                            onValueChange = onUpdateTitleText,
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(focusRequester),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = theme.accent,
                                unfocusedBorderColor = theme.border,
                                cursorColor = theme.accent
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Row {
                            IconButton(
                                onClick = onSaveTitle,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Filled.Check,
                                    contentDescription = "Save",
                                    tint = Color.Green,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            IconButton(
                                onClick = onCancelEditing,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Filled.Close,
                                    contentDescription = "Cancel",
                                    tint = Color.Red,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    LaunchedEffect(Unit) {
                        focusRequester.requestFocus()
                    }
                } else {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            task.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (task.isCompleted) Color.White.copy(alpha = 0.5f) else Color.White,
                            fontWeight = FontWeight.SemiBold,
                            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(
                            onClick = onStartEditingTitle,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Filled.Edit,
                                contentDescription = "Edit Title",
                                tint = Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Category and Priority Tags
            Row {
                TaskTag(
                    text = task.category.name,
                    theme = theme
                )
                Spacer(modifier = Modifier.width(8.dp))
                TaskTag(
                    text = "PRIORITY ${task.priority}",
                    theme = theme
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Description - editable
            if (isEditingDescription) {
                val focusRequester = remember { FocusRequester() }

                Column {
                    OutlinedTextField(
                        value = editingDescriptionText,
                        onValueChange = onUpdateDescriptionText,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = theme.accent,
                            unfocusedBorderColor = theme.border,
                            cursorColor = theme.accent
                        ),
                        minLines = 3,
                        maxLines = 6,
                        placeholder = {
                            Text(
                                "Enter task description...",
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = onCancelEditing,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Color.Red
                            )
                        ) {
                            Text("Cancel")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        TextButton(
                            onClick = onSaveDescription,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Color.Green
                            )
                        ) {
                            Text("Save")
                        }
                    }
                }

                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        task.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFFD1D5DB),
                        lineHeight = 24.sp,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = onStartEditingDescription,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = "Edit Description",
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ResourcesLinksSection(
    links: List<TaskLink>,
    expandedLinkId: String?,
    onExpandLink: (String?) -> Unit,
    onOpenLink: (TaskLink) -> Unit,
    theme: TaskDetailsTheme,
    onAddLink: () -> Unit = {},
    onRemoveLink: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .background(
                theme.surface,
                RoundedCornerShape(12.dp)
            )
            .border(
                1.dp,
                theme.border,
                RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.Link,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "RESOURCES & LINKS",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
            }

            IconButton(
                onClick = onAddLink,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add Link",
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        if (links.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))

            links.forEach { link ->
                ResourceLinkItem(
                    link = link,
                    isExpanded = expandedLinkId == link.id,
                    onExpandClick = {
                        onExpandLink(if (expandedLinkId == link.id) null else link.id)
                    },
                    onOpenClick = { onOpenLink(link) },
                    onRemoveClick = { onRemoveLink(link.id) },
                    theme = theme
                )
                if (link != links.last()) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        } else {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "No links added yet. Click + to add resources.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun ResourceLinkItem(
    link: TaskLink,
    isExpanded: Boolean,
    onExpandClick: () -> Unit,
    onOpenClick: () -> Unit,
    onRemoveClick: () -> Unit,
    theme: TaskDetailsTheme
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color.White.copy(alpha = 0.05f),
                RoundedCornerShape(8.dp)
            )
            .border(
                1.dp,
                Color.White.copy(alpha = 0.1f),
                RoundedCornerShape(8.dp)
            )
            .padding(12.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Thumbnail or Icon
                Box(
                    modifier = Modifier
                        .size(48.dp, 32.dp)
                        .background(
                            Color.White.copy(alpha = 0.1f),
                            RoundedCornerShape(8.dp)
                        )
                        .clip(RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (link.thumbnail != null) {
                        // In a real app, you'd load the image here
                        Text(
                            getLinkEmoji(link.type),
                            fontSize = 16.sp
                        )
                    } else {
                        Icon(
                            getLinkIcon(link.type),
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        link.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    AssistChip(
                        onClick = { },
                        label = {
                            Text(
                                link.type.name,
                                fontSize = 10.sp
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = Color.White.copy(alpha = 0.1f),
                            labelColor = Color.White.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.height(24.dp)
                    )

                    if (isExpanded && link.description != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            link.description,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.6f),
                            lineHeight = 16.sp
                        )
                    }
                }

                IconButton(
                    onClick = onOpenClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Filled.OpenInNew,
                        contentDescription = "Open Link",
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(14.dp)
                    )
                }
                IconButton(
                    onClick = onRemoveClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Remove Link",
                        tint = Color.Red,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            if (link.description != null) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = onExpandClick,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.White.copy(alpha = 0.4f)
                    ),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.height(24.dp)
                ) {
                    Text(
                        if (isExpanded) "Show less" else "Show more",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
fun CompletionStatusBanner(
    isCompleted: Boolean,
    onToggleComplete: () -> Unit,
    theme: TaskDetailsTheme
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .background(
                if (isCompleted) theme.completedGreen.copy(alpha = 0.1f) else theme.surface,
                RoundedCornerShape(12.dp)
            )
            .border(
                1.dp,
                if (isCompleted) theme.completedGreen.copy(alpha = 0.4f) else theme.border,
                RoundedCornerShape(12.dp)
            )
            .clickable { onToggleComplete() }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            if (isCompleted) "✓ TASK COMPLETED - POINTS EARNED" else "Mark as Completed",
            style = MaterialTheme.typography.bodyLarge,
            color = if (isCompleted) theme.completedGreen else Color.White.copy(alpha = 0.6f),
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun TaskTag(
    text: String,
    theme: TaskDetailsTheme
) {
    Box(
        modifier = Modifier
            .background(
                theme.border,
                RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text,
            style = MaterialTheme.typography.labelSmall,
            color = theme.textSecondary,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.5.sp
        )
    }
}

private fun getLinkIcon(type: TaskLinkType) = when (type) {
    TaskLinkType.VIDEO -> Icons.Filled.PlayArrow
    TaskLinkType.ARTICLE -> Icons.Filled.Article
    TaskLinkType.LINK -> Icons.Filled.Link
}

private fun getLinkEmoji(type: TaskLinkType) = when (type) {
    TaskLinkType.VIDEO -> "📹"
    TaskLinkType.ARTICLE -> "📄"
    TaskLinkType.LINK -> "🔗"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLinkDialog(
    isVisible: Boolean,
    title: String,
    url: String,
    description: String,
    selectedType: TaskLinkType,
    onTitleChange: (String) -> Unit,
    onUrlChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onTypeChange: (TaskLinkType) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    theme: TaskDetailsTheme
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onCancel,
            containerColor = theme.surface,
            title = {
                Text(
                    "Add Resource Link",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Column {
                    // Link Type Selection
                    Text(
                        "Link Type",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row {
                        TaskLinkType.values().forEach { type ->
                            val isSelected = selectedType == type
                            FilterChip(
                                selected = isSelected,
                                onClick = { onTypeChange(type) },
                                label = {
                                    Text(
                                        type.name,
                                        fontSize = 12.sp
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = if (isSelected) theme.accent else Color.Transparent,
                                    labelColor = if (isSelected) Color.Black else Color.White.copy(
                                        alpha = 0.8f
                                    ),
                                    selectedContainerColor = theme.accent,
                                    selectedLabelColor = Color.Black
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = theme.border,
                                    selectedBorderColor = theme.accent,
                                    selected = false,
                                    enabled = true
                                ),
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Title Input
                    OutlinedTextField(
                        value = title,
                        onValueChange = onTitleChange,
                        label = {
                            Text(
                                "Title",
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = theme.accent,
                            unfocusedBorderColor = theme.border,
                            focusedLabelColor = theme.accent,
                            unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                            cursorColor = theme.accent
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // URL Input
                    OutlinedTextField(
                        value = url,
                        onValueChange = onUrlChange,
                        label = {
                            Text(
                                "URL",
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = theme.accent,
                            unfocusedBorderColor = theme.border,
                            focusedLabelColor = theme.accent,
                            unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                            cursorColor = theme.accent
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Uri
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Description Input (optional)
                    OutlinedTextField(
                        value = description,
                        onValueChange = onDescriptionChange,
                        label = {
                            Text(
                                "Description (Optional)",
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = theme.accent,
                            unfocusedBorderColor = theme.border,
                            focusedLabelColor = theme.accent,
                            unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                            cursorColor = theme.accent
                        ),
                        minLines = 2,
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = onSave,
                    enabled = title.isNotBlank() && url.isNotBlank(),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = theme.accent,
                        disabledContentColor = Color.White.copy(alpha = 0.4f)
                    )
                ) {
                    Text(
                        "Add Link",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onCancel,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.White.copy(alpha = 0.8f)
                    )
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun TaskDetailsHeaderPreview() {
    TaskDetailsHeader(
        onBackClick = {},
        theme = TaskDetailsTheme()
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun PointsRewardSectionPreview() {
    PointsRewardSection(
        points = 25,
        theme = TaskDetailsTheme()
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun CompletionStatusBannerPreview() {
    CompletionStatusBanner(
        isCompleted = false,
        onToggleComplete = {},
        theme = TaskDetailsTheme()
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun CompletionStatusBannerCompletedPreview() {
    CompletionStatusBanner(
        isCompleted = true,
        onToggleComplete = {},
        theme = TaskDetailsTheme()
    )
}