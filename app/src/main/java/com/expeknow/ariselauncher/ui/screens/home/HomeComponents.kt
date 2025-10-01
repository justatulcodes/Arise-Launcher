package com.expeknow.ariselauncher.ui.screens.home

import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.expeknow.ariselauncher.data.model.*
import com.expeknow.ariselauncher.ui.screens.apps.AppDrawerApp
import com.expeknow.ariselauncher.ui.screens.home.Utils.toImageBitmap
import com.google.accompanist.drawablepainter.rememberDrawablePainter

@Composable
fun EnhancedPointsHeader(
    currentPoints: Int,
    pointChange: Int,
    pointsTrend: PointsTrend,
    completed: Int,
    total: Int,
    onPointsClick: () -> Unit,
    theme: HomeTheme
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Points Section - clickable
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onPointsClick() }
            ) {
                Icon(
                    Icons.Filled.EmojiEvents,
                    contentDescription = "Trophy",
                    tint = theme.accent,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        "$currentPoints",
                        style = MaterialTheme.typography.displaySmall,
                        color = theme.accent,
                        fontWeight = FontWeight.Black,
                        fontSize = 48.sp,
                    )
                    Text(
                        "CURRENT POINTS",
                        style = MaterialTheme.typography.labelSmall,
                        color = theme.textSecondary,
                        fontSize = 10.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val trendColor = if (pointsTrend == PointsTrend.INCREASING)
                        Color(0xFF4ADE80) else Color(0xFFEF4444)
                    val trendIcon = if (pointsTrend == PointsTrend.INCREASING)
                        Icons.Filled.TrendingUp else Icons.Filled.TrendingDown

                    Icon(
                        trendIcon,
                        contentDescription = null,
                        tint = trendColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        if (pointsTrend == PointsTrend.INCREASING) "+$pointChange" else "-$pointChange",
                        color = trendColor,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "$completed/$total",
                    color = theme.accent,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    "COMPLETED",
                    style = MaterialTheme.typography.labelSmall,
                    color = theme.textSecondary,
                    fontSize = 10.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))


    }
}

@Composable
fun EssentialAppsDrawer(
    onClose: () -> Unit,
    onOpenFullApps: () -> Unit,
    theme: HomeTheme
) {
    val essentialApps = listOf(
        Triple("Phone", Icons.Filled.Phone, "Make and receive calls"),
        Triple("Messages", Icons.Filled.Message, "Text messaging"),
        Triple("Mail", Icons.Filled.Email, "Email communication"),
        Triple("Calendar", Icons.Filled.CalendarToday, "Schedule and events"),
        Triple("Contacts", Icons.Filled.Contacts, "Contact management"),
        Triple("WhatsApp", Icons.Filled.Chat, "Instant messaging"),
        Triple("Teams", Icons.Filled.VideoCall, "Work collaboration")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f))
            .clickable { onClose() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .align(Alignment.BottomCenter)
                .background(
                    Color.Black,
                    RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                )
                .border(
                    2.dp,
                    theme.border,
                    RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                )
                .clickable(enabled = false) { }
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = theme.border,
                            shape = RoundedCornerShape(bottomStart = 0.dp, bottomEnd = 0.dp)
                        )
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "ESSENTIAL APPS",
                            style = MaterialTheme.typography.titleMedium,
                            color = theme.accent,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            "Quick access to daily drivers",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }

                // Apps Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(essentialApps) { (name, icon, description) ->
                        EssentialAppCard(
                            name = name,
                            icon = icon,
                            description = description,
                            theme = theme,
                            onClick = { onClose() }
                        )
                    }
                }

                // Full Apps Access
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(
                            theme.bg,
                            RoundedCornerShape(8.dp)
                        )
                        .border(
                            2.dp,
                            theme.border,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            onClose()
                            onOpenFullApps()
                        }
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    theme.bg,
                                    RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.GridView,
                                contentDescription = null,
                                tint = theme.accent,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "ALL APPS",
                                style = MaterialTheme.typography.titleSmall,
                                color = theme.accent,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                "Access full app drawer",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }

                        Icon(
                            Icons.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.4f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Quick Stats
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .border(
                            width = 1.dp,
                            color = theme.border,
                            shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
                        )
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "${essentialApps.size} essential apps available",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                        Text(
                            "No delays • Instant access",
                            style = MaterialTheme.typography.labelSmall,
                            color = theme.accent,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EssentialAppCard(
    name: String,
    icon: ImageVector,
    description: String,
    theme: HomeTheme,
    onClick: () -> Unit
) {
    val categoryColor = when (name) {
        "Phone", "Messages", "Contacts" -> Color(0xFF4ADE80)
        "Mail", "Calendar", "Teams" -> Color(0xFFFFD700)
        else -> Color(0xFF60A5FA)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .background(
                categoryColor.copy(alpha = 0.1f),
                RoundedCornerShape(12.dp)
            )
            .border(
                1.dp,
                categoryColor.copy(alpha = 0.3f),
                RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    theme.bg,
                    RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = name,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            name,
            style = MaterialTheme.typography.labelMedium,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )

        Text(
            description,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.4f),
            maxLines = 2
        )
    }
}

@Composable
fun ModeToggle(
    mode: HomeMode,
    onModeToggle: () -> Unit,
    theme: HomeTheme
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onModeToggle,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (mode == HomeMode.SIMPLE)
                    Color.White.copy(alpha = 0.1f) else theme.accent.copy(alpha = 0.1f),
                contentColor = if (mode == HomeMode.SIMPLE)
                    Color.White else theme.accent
            ),
            modifier = Modifier
                .wrapContentWidth()
        ) {
            Icon(
                if (mode == HomeMode.SIMPLE) Icons.Filled.ToggleOff else Icons.Filled.ToggleOn,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                if (mode == HomeMode.SIMPLE) "SIMPLE MODE" else "FOCUSED MODE",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun EnhancedProgressBar(
    completed: Int,
    total: Int,
    theme: HomeTheme
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    Color.White.copy(alpha = 0.2f),
                    RoundedCornerShape(4.dp)
                )
        ) {
            val progress = if (total > 0) completed.toFloat() / total else 0f
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .background(
                        theme.accent,
                        RoundedCornerShape(4.dp)
                    )
                    .animateContentSize()
            )
        }
    }
}

@Composable
fun SimpleTaskList(
    tasks: List<Task>,
    onTaskClick: (String) -> Unit,
    onToggleTask: (String) -> Unit,
    onTaskLinkClick : (TaskLink) -> Unit,
    theme: HomeTheme
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(tasks) { task ->
            SimpleTaskItem(
                task = task,
                onTaskClick = onTaskClick,
                onToggleTask = onToggleTask,
                theme = theme,
                onTaskLinkClick = onTaskLinkClick
            )
        }
    }
}

@Composable
fun SimpleTaskItem(
    task: Task,
    onTaskClick: (String) -> Unit,
    onToggleTask: (String) -> Unit,
    onTaskLinkClick : (TaskLink) -> Unit,
    theme: HomeTheme
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTaskClick(task.id) },
        verticalAlignment = Alignment.Top
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = null,
            colors = CheckboxDefaults.colors(
                checkedColor = theme.accent,
                uncheckedColor = Color.White.copy(alpha = 0.4f),
                checkmarkColor = Color.Black
            ),
            modifier = Modifier
                .size(20.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            onToggleTask(task.id)
                        }
                    )
                }
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.title,
                color = if (task.isCompleted) Color.White.copy(alpha = 0.5f) else Color.White,
                style = MaterialTheme.typography.bodyMedium,
                textDecoration = if (task.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "+${task.points} pts",
                    color = theme.accent,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium
                )
            }

            if (task.relatedLinks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    task.relatedLinks.take(2).forEach { link ->
                        TaskLinkChip(link = link, theme = theme,
                            onTaskLinkChipClick = {
                                onTaskLinkClick(it)
                            })
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    if (task.relatedLinks.size > 2) {
                        Text(
                            "+${task.relatedLinks.size - 2} more",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }

        Icon(
            Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.4f),
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun FocusedTaskList(
    categories: List<FocusCategory>,
    tasks: List<Task>,
    onTaskClick: (String) -> Unit,
    onToggleTask: (Task) -> Unit,
    onEditCategory: (TaskCategory) -> Unit,
    editingCategoryId: TaskCategory?,
    editingCategoryName: String,
    onSaveEdit: () -> Unit,
    onCancelEdit: () -> Unit,
    onEditingNameChange: (String) -> Unit,
    theme: HomeTheme
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        items(categories) { category ->
            val categoryTasks = tasks.filter { it.category == category.id }
            FocusedCategorySection(
                category = category,
                tasks = categoryTasks,
                onTaskClick = onTaskClick,
                onToggleTask = onToggleTask,
                onEditCategory = onEditCategory,
                isEditing = editingCategoryId == category.id,
                editingName = editingCategoryName,
                onSaveEdit = onSaveEdit,
                onCancelEdit = onCancelEdit,
                onEditingNameChange = onEditingNameChange,
                theme = theme
            )
        }
    }
}

@Composable
private fun FocusedCategorySection(
    category: FocusCategory,
    tasks: List<Task>,
    onTaskClick: (String) -> Unit,
    onToggleTask: (Task) -> Unit,
    onEditCategory: (TaskCategory) -> Unit,
    isEditing: Boolean,
    editingName: String,
    onSaveEdit: () -> Unit,
    onCancelEdit: () -> Unit,
    onEditingNameChange: (String) -> Unit,
    theme: HomeTheme
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                category.bgColor,
                RoundedCornerShape(12.dp)
            )
            .border(
                2.dp,
                category.borderColor,
                RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                category.icon,
                contentDescription = null,
                tint = category.color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))

            if (isEditing) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = editingName,
                        onValueChange = onEditingNameChange,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White.copy(alpha = 0.2f),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            cursorColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onSaveEdit) {
                        Icon(
                            Icons.Filled.Check,
                            null,
                            tint = Color(0xFF4ADE80),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(onClick = onCancelEdit) {
                        Icon(
                            Icons.Filled.Close,
                            null,
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            } else {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            category.name,
                            color = category.color,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = { onEditCategory(category.id) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Filled.Edit,
                                contentDescription = "Edit",
                                tint = Color.White.copy(alpha = 0.4f),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                    Text(
                        "${tasks.count { it.isCompleted }}/${tasks.size} completed",
                        color = Color.White.copy(alpha = 0.5f),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (tasks.isEmpty()) {
            Text(
                "No tasks yet. Click the + button to add one.",
                color = Color.White.copy(alpha = 0.4f),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            tasks.forEach { task ->
                FocusedTaskItem(
                    task = task,
                    categoryColor = category.color,
                    onTaskClick = onTaskClick,
                    onToggleTask = onToggleTask
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun FocusedTaskItem(
    task: Task,
    categoryColor: Color,
    onTaskClick: (String) -> Unit,
    onToggleTask: (Task) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTaskClick(task.id) }
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = { onToggleTask(task) },
            colors = CheckboxDefaults.colors(
                checkedColor = categoryColor,
                uncheckedColor = Color.White.copy(alpha = 0.4f),
                checkmarkColor = Color.Black
            ),
            modifier = Modifier.size(16.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                task.title,
                color = if (task.isCompleted) Color.White.copy(alpha = 0.5f) else Color.White,
                style = MaterialTheme.typography.bodySmall,
                textDecoration = if (task.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null,
                modifier = Modifier.weight(1f)
            )
            Text(
                "+${task.points} pts",
                color = categoryColor,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = categoryColor.copy(alpha = 0.6f),
            modifier = Modifier.size(14.dp)
        )
    }
}

@Composable
private fun TaskLinkChip(
    link: TaskLink,
    theme: HomeTheme,
    onTaskLinkChipClick : (TaskLink) -> Unit = {}
) {
    val linkIcon = when (link.type) {
        TaskLinkType.VIDEO -> "📹"
        TaskLinkType.ARTICLE -> "📄"
        TaskLinkType.LINK -> "🔗"
    }

    Box(
        modifier = Modifier
            .clickable { onTaskLinkChipClick(link) }
            .background(
                theme.bg,
                RoundedCornerShape(8.dp)
            )
            .border(
                1.dp,
                theme.border,
                RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(linkIcon, fontSize = 10.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                link.title.take(20),
                color = Color.White.copy(alpha = 0.6f),
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun TasksCompletedCelebration(
    completedCount: Int,
    pointsEarned: Int,
    theme: HomeTheme
) {
    val infiniteTransition = rememberInfiniteTransition(label = "celebration")

    // Main pulsing animation for the central icon
    val pulseAnimation by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Floating icons data with different animations
    val floatingIcons = listOf(
        Triple(Icons.Filled.Star, "0ms", Offset(50f, 80f)),
        Triple(Icons.Filled.WorkspacePremium, "500ms", Offset(300f, 120f)),
        Triple(Icons.Filled.Bolt, "1000ms", Offset(80f, 200f)),
        Triple(Icons.Filled.EmojiEvents, "1500ms", Offset(280f, 60f)),
        Triple(Icons.Filled.TrackChanges, "2000ms", Offset(40f, 160f)),
        Triple(Icons.Filled.CheckCircle, "2500ms", Offset(320f, 180f))
    )

    val scrollState = rememberScrollState()

    Column(Modifier.scrollable(scrollState, Orientation.Vertical)) {
        Box(
            modifier = Modifier
                .background(
                    brush = androidx.compose.ui.graphics.Brush.radialGradient(
                        colors = listOf(
                            theme.accent.copy(alpha = 0.1f),
                            Color.Transparent
                        ),
                        radius = 320f
                    )
                ),
            contentAlignment = Alignment.Center
        )
        {
            // Background subtle pattern
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.02f),
                                Color.Transparent
                            ),
                            radius = 200f
                        )
                    )
            )

            // Floating celebration icons
            floatingIcons.forEachIndexed { index, (icon, delay, position) ->
                val bounceAnimation by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 20f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = 2000 + (index * 200),
                            delayMillis = delay.removeSuffix("ms").toInt(),
                            easing = EaseInOut
                        ),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "bounce$index"
                )

                Box(
                    modifier = Modifier
                        .offset(
                            x = position.x.dp,
                            y = (position.y - bounceAnimation).dp
                        )
                        .size(24.dp)
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = theme.accent.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Central celebration content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                // Main celebration icon with pulsing ring
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    // Pulsing ring effect
                    Box(
                        modifier = Modifier
                            .size((120 * pulseAnimation).dp)
                            .background(
                                Color.Transparent,
                                CircleShape
                            )
                            .border(
                                2.dp,
                                theme.border.copy(alpha = 0.3f),
                                CircleShape
                            )
                    )

                    // Main icon container
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .background(
                                theme.bg.copy(alpha = 0.8f),
                                CircleShape
                            )
                            .border(
                                2.dp,
                                theme.border,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = theme.accent,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Celebration text
                Text(
                    "ALL TASKS COMPLETED",
                    style = MaterialTheme.typography.headlineMedium,
                    color = theme.accent,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    "Outstanding discipline and focus!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Stats badges
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                theme.border.copy(alpha = 0.3f),
                                RoundedCornerShape(12.dp)
                            )
                            .border(
                                1.dp,
                                theme.border,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            "$completedCount Tasks Done",
                            color = theme.accent,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(
                                Color(0xFF4ADE80).copy(alpha = 0.2f),
                                RoundedCornerShape(12.dp)
                            )
                            .border(
                                1.dp,
                                Color(0xFF4ADE80).copy(alpha = 0.4f),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            "+$pointsEarned Points",
                            color = Color(0xFF4ADE80),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Motivational quote with border accent
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color.Black.copy(alpha = 0.3f),
                            RoundedCornerShape(12.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = theme.border,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(start = 4.dp)
                ) {
                    Row {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(60.dp)
                                .background(
                                    theme.accent,
                                    RoundedCornerShape(2.dp)
                                )
                        )

                        Text(
                            "\"Excellence is not a skill, it's an attitude. Today you've proven yours.\"",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f),
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Next steps suggestion
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Your consistency builds your legacy",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.4f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }

}

@Composable
fun FloatingAddButton(
    onClick: () -> Unit,
    theme: HomeTheme
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = theme.accent,
        contentColor = Color.Black,
        modifier = Modifier.size(56.dp)
    ) {
        Icon(
            Icons.Filled.Add,
            contentDescription = "Add Task",
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun MotivationalQuote(theme: HomeTheme) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "\"Discipline is the bridge between goals and accomplishment.\"",
            style = MaterialTheme.typography.labelSmall,
            color = theme.accent.copy(alpha = 0.9f),
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "— Jim Rohn",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun EssentialAppsBar(
    onAppClick: (AppDrawerApp) -> Unit,
    onOpenFullApps: () -> Unit,
    theme: HomeTheme,
    appsList: List<AppDrawerApp>
) {
    Column {
        Box(contentAlignment = Alignment.Center) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        detectVerticalDragGestures() { change, dragAmount ->
                            if (dragAmount < -50f) {
                                onOpenFullApps()
                            }
                        }
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                for (app in appsList) {
                    AppIcon(
                        icon = app.icon,
                        label = app.name,
                        onClick = {
                            if(app.name == "Apps") {
                                onOpenFullApps()
                            } else {
                                onAppClick(app)
                            }
                        },
                        theme = theme
                    )
                }
            }

            val infiniteTransition = rememberInfiniteTransition(label = "swipe_animation")

            val offsetY by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = -8f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 1000,
                        easing = FastOutLinearInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "offset_animation"
            )

            Column(
                modifier = Modifier.offset(y = offsetY.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Swipe up for all apps",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    modifier = Modifier
                        .padding(top = 8.dp)
                )
            }

        }

    }
}

@Composable
private fun AppIcon(
    icon: Drawable? = null,
    label: String,
    onClick: () -> Unit,
    theme: HomeTheme,
    isCenter: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(if (isCenter) 56.dp else 48.dp)
                .background(
                    Color.White.copy(alpha = 0.1f),
                    RoundedCornerShape(if (isCenter) 14.dp else 12.dp)
                )
                .border(
                    if (isCenter) 2.dp else 1.dp,
                    theme.border,
                    RoundedCornerShape(if (isCenter) 14.dp else 12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if(icon == null) {
                Icon(
                    imageVector = Icons.Default.GridOn,
                    contentDescription = label,
                    modifier = Modifier.size(if (isCenter) 22.dp else 32.dp)
                )
            } else {
                Image(
                    bitmap = icon.toImageBitmap(),
                    contentDescription = label,
                    modifier = Modifier.size(if (isCenter) 22.dp else 32.dp)
                )
            }
            if (isCenter) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(Color(0xFF4ADE80), CircleShape)
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-4).dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isCenter) theme.accent else Color.White.copy(alpha = 0.4f),
            fontSize = 10.sp,
            fontWeight = if (isCenter) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun EnhancedPointsHeaderPreview() {
    EnhancedPointsHeader(
        currentPoints = 245,
        pointChange = 15,
        pointsTrend = PointsTrend.INCREASING,
        completed = 5,
        total = 8,
        onPointsClick = {},
        theme = HomeTheme()
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun ModeTogglePreview() {
    ModeToggle(
        mode = HomeMode.SIMPLE,
        onModeToggle = {},
        theme = HomeTheme()
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun EnhancedProgressBarPreview() {
    EnhancedProgressBar(
        completed = 5,
        total = 8,
        theme = HomeTheme()
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun MotivationalQuotePreview() {
    MotivationalQuote(theme = HomeTheme())
}

//@Preview(showBackground = true, backgroundColor = 0xFF000000)
//@Composable
//private fun EssentialAppsBarPreview() {
//    EssentialAppsBar(
//        onAppClick = {},
//        onOpenFullApps = {},
//        theme = HomeTheme(),
//    )
//}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun TasksCompletedCelebrationPreview() {
    TasksCompletedCelebration(
        completedCount = 12,
        pointsEarned = 285,
        theme = HomeTheme()
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun FloatingAddButtonPreview() {
    FloatingAddButton(
        onClick = {},
        theme = HomeTheme()
    )
}