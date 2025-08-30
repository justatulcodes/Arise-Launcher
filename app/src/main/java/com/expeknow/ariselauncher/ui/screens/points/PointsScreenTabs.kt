package com.expeknow.ariselauncher.ui.screens.points

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.expeknow.ariselauncher.data.model.*

@Composable
fun TasksContent(
    currentRank: Rank,
    taskStats: TaskStats,
    onNavigateToTaskHistory: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        // Task Statistics Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = currentRank.colors.background),
            border = BorderStroke(2.dp, currentRank.colors.border),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckBox,
                        contentDescription = null,
                        tint = currentRank.colors.accent,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${taskStats.completionRatio.toInt()}%",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = currentRank.colors.accent
                    )
                }

                Text(
                    text = "TASK COMPLETION RATIO",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.sp
                    ),
                    color = Color(0xFF9CA3AF)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .background(
                            Color.White.copy(alpha = 0.2f),
                            RoundedCornerShape(6.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(taskStats.completionRatio / 100f)
                            .background(
                                currentRank.colors.accent,
                                RoundedCornerShape(6.dp)
                            )
                    )
                }
            }
        }

        // Task History Link
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = currentRank.colors.background),
            border = BorderStroke(1.dp, currentRank.colors.border),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                currentRank.colors.background,
                                RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckBox,
                            contentDescription = null,
                            tint = currentRank.colors.accent,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "TASK HISTORY",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = currentRank.colors.accent
                        )
                        Text(
                            text = "View all completed tasks",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                }

                Button(
                    onClick = onNavigateToTaskHistory,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = currentRank.colors.accent
                    )
                ) {
                    Text("View All")
                }
            }
        }

        // Task Stats Grid
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TaskStatCard(
                    modifier = Modifier.weight(1f),
                    title = "${taskStats.completedTasks}/${taskStats.totalTasks}",
                    subtitle = "TASKS COMPLETED",
                    currentRank = currentRank
                )

                TaskStatCard(
                    modifier = Modifier.weight(1f),
                    title = taskStats.todayCompleted.toString(),
                    subtitle = "TODAY'S TASKS",
                    currentRank = currentRank
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TaskStatCard(
                    modifier = Modifier.weight(1f),
                    title = taskStats.urgentTasks.toString(),
                    subtitle = "URGENT TASKS",
                    currentRank = currentRank
                )

                TaskStatCard(
                    modifier = Modifier.weight(1f),
                    title = taskStats.weeklyAverage.toString(),
                    subtitle = "WEEKLY AVG",
                    currentRank = currentRank
                )
            }
        }

        // Task Breakdown
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = currentRank.colors.background),
            border = BorderStroke(1.dp, currentRank.colors.border),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "TASK BREAKDOWN",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = currentRank.colors.accent,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                TaskBreakdownItem(
                    label = "Work Tasks",
                    count = taskStats.workTasks,
                    total = taskStats.totalTasks,
                    color = Color(0xFF60A5FA)
                )

                Spacer(modifier = Modifier.height(12.dp))

                TaskBreakdownItem(
                    label = "Personal Tasks",
                    count = taskStats.personalTasks,
                    total = taskStats.totalTasks,
                    color = Color(0xFF4ADE80)
                )

                Spacer(modifier = Modifier.height(12.dp))

                TaskBreakdownItem(
                    label = "Urgent Tasks",
                    count = taskStats.urgentTasks,
                    total = taskStats.totalTasks,
                    color = Color(0xFFF87171)
                )
            }
        }

        // Productivity Insights
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = currentRank.colors.background),
            border = BorderStroke(1.dp, currentRank.colors.border),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "PRODUCTIVITY INSIGHTS",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = currentRank.colors.accent,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                ProductivityInsightItem(
                    icon = Icons.Filled.Schedule,
                    title = "Peak Performance",
                    description = "You complete most tasks between 9-11 AM",
                    iconColor = Color(0xFFFACC15)
                )

                Spacer(modifier = Modifier.height(12.dp))

                ProductivityInsightItem(
                    icon = Icons.Filled.CalendarToday,
                    title = "Consistency Streak",
                    description = "12 days of completing daily goals",
                    iconColor = Color(0xFF4ADE80)
                )

                Spacer(modifier = Modifier.height(12.dp))

                ProductivityInsightItem(
                    icon = Icons.Filled.CenterFocusStrong,
                    title = "Focus Score",
                    description = "87% - Excellent task completion rate",
                    iconColor = currentRank.colors.accent
                )
            }
        }
    }
}

@Composable
fun RanksContent(
    currentRank: Rank,
    onRankClick: (Rank) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Header
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "RANK PROGRESSION",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = currentRank.colors.accent,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Click any rank to preview its theme",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF9CA3AF),
                textAlign = TextAlign.Center
            )

            Surface(
                modifier = Modifier.padding(top = 8.dp),
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, currentRank.colors.border),
                color = Color.Transparent
            ) {
                Text(
                    text = "DEBUG MODE: ALL RANKS UNLOCKED",
                    style = MaterialTheme.typography.labelSmall,
                    color = currentRank.colors.accent,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // Ranks List
        ranks.forEach { rank ->
            val isCurrentRank = rank.name == currentRank.name

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onRankClick(rank) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isCurrentRank) rank.colors.background else Color.Transparent
                ),
                border = BorderStroke(
                    width = if (isCurrentRank) 2.dp else 1.dp,
                    color = if (isCurrentRank) rank.colors.border else Color(0x33FFFFFF)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                rank.colors.background,
                                RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = rank.icon,
                            contentDescription = null,
                            tint = rank.colors.accent,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = rank.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = rank.colors.accent
                            )

                            if (isCurrentRank) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    border = BorderStroke(1.dp, rank.colors.border),
                                    color = Color.Transparent
                                ) {
                                    Text(
                                        text = "ACTIVE",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = rank.colors.accent,
                                        modifier = Modifier.padding(
                                            horizontal = 6.dp,
                                            vertical = 2.dp
                                        )
                                    )
                                }
                            }
                        }

                        Text(
                            text = rank.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFB3B3B3),
                            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                        )

                        Text(
                            text = "${rank.minPoints} - ${if (rank.maxPoints == 999999) "∞" else rank.maxPoints} points",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskStatCard(
    title: String,
    subtitle: String,
    currentRank: Rank,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = currentRank.colors.background),
        border = BorderStroke(1.dp, currentRank.colors.border),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = currentRank.colors.accent,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF9CA3AF),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun TaskBreakdownItem(
    label: String,
    count: Int,
    total: Int,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFFB3B3B3)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(8.dp)
                    .background(
                        Color.White.copy(alpha = 0.2f),
                        RoundedCornerShape(4.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth((count.toFloat() / total))
                        .background(
                            color,
                            RoundedCornerShape(4.dp)
                        )
                )
            }

            Text(
                text = count.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = color
            )
        }
    }
}

@Composable
private fun ProductivityInsightItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    iconColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(16.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
            Text(
                text = description,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF9CA3AF)
            )
        }
    }
}