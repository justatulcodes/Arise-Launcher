package com.expeknow.ariselauncher.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import com.expeknow.ariselauncher.ui.theme.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import com.expeknow.ariselauncher.data.model.Task
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.tooling.preview.Preview
import com.expeknow.ariselauncher.data.model.TaskLinkType

@Composable
fun TaskItem(
    task: Task,
    onTaskCompleted: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceCard),
        verticalAlignment = Alignment.Top
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = { if (it) onTaskCompleted(task.id) },
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(top = 8.dp, end = 16.dp)
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleSmall,
                color = TaskTitle
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "+${task.points} pts",
                style = MaterialTheme.typography.bodySmall,
                color = AccentGreen
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                task.relatedLinks.filter { it.type == TaskLinkType.ARTICLE }.let { link ->
                    if (link.isNotEmpty()) {
                        BadgeLink(link.first().title, icon = Icons.Filled.Article)
                    }
                }

                task.relatedLinks.filter { it.type == TaskLinkType.VIDEO }.let { link ->
                    if (link.isNotEmpty()) {
                        BadgeLink(link.first().title, icon = Icons.Filled.PlayArrow)
                    }
                }
                task.relatedLinks.filter { it.type == TaskLinkType.LINK }.let { link ->
                    if (link.isNotEmpty()) {
                        BadgeLink(link.first().title, icon = Icons.Filled.Link)
                    }
                }
            }
        }
    }
}

@Composable
fun BadgeLink(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(7.dp))
            .background(DividerGray)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AccentGreen,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = BannerTextGray,
            maxLines = 1
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TaskItemPreview() {
    TaskItem(
        task = Task(
            id = "example-task-id",
            title = "Task 1",
            description = "Description 1",
            isCompleted = false,
            points = 10
        ),
        onTaskCompleted = {}
    )
}

@Preview(showBackground = true)
@Composable
fun BadgeLinkPreview() {
    BadgeLink(
        text = "Badge Link",
        icon = Icons.Default.Link
    )
}


