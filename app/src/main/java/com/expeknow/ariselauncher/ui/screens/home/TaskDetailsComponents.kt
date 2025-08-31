package com.expeknow.ariselauncher.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
    theme: TaskDetailsTheme
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

                Text(
                    task.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (task.isCompleted) Color.White.copy(alpha = 0.5f) else Color.White,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                    modifier = Modifier.weight(1f)
                )
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

            // Description
            Text(
                task.description,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFFD1D5DB),
                lineHeight = 24.sp
            )
        }
    }
}

@Composable
fun ResourcesLinksSection(
    links: List<TaskLink>,
    expandedLinkId: String?,
    onExpandLink: (String?) -> Unit,
    onOpenLink: (String) -> Unit,
    theme: TaskDetailsTheme
) {
    if (links.isEmpty()) return

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

        Spacer(modifier = Modifier.height(12.dp))

        links.forEach { link ->
            ResourceLinkItem(
                link = link,
                isExpanded = expandedLinkId == link.id,
                onExpandClick = {
                    onExpandLink(if (expandedLinkId == link.id) null else link.id)
                },
                onOpenClick = { onOpenLink(link.url) },
                theme = theme
            )
            if (link != links.last()) {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun ResourceLinkItem(
    link: TaskLink,
    isExpanded: Boolean,
    onExpandClick: () -> Unit,
    onOpenClick: () -> Unit,
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
            if (isCompleted) "✓ TASK COMPLETED - POINTS EARNED" else "COMPLETE THIS TASK TO EARN POINTS",
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