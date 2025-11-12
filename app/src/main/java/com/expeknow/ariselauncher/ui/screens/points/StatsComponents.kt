package com.expeknow.ariselauncher.ui.screens.points

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.expeknow.ariselauncher.data.model.*
import com.expeknow.ariselauncher.ui.screens.home.CategoryStat
import com.expeknow.ariselauncher.ui.screens.home.StatsUi

@Composable
fun MvpStatsContent(
    statsUi: StatsUi,
    currentRank: Rank
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        PointsProgressCard(
            earnedPoints = statsUi.focusEarnedPoints,
            potentialPoints = statsUi.focusPotentialPoints,
            currentRank = currentRank
        )

        OverallFocusCard(
            statsUi = statsUi,
            currentRank = currentRank
        )

        CategoryBreakdownCard(
            categories = statsUi.categories,
            currentRank = currentRank
        )

        PersonalTasksCard(
            completed = statsUi.personalCompleted,
            total = statsUi.personalTotal,
            percent = statsUi.personalPercent,
            currentRank = currentRank
        )
    }
}

@Composable
private fun OverallFocusCard(
    statsUi: StatsUi,
    currentRank: Rank
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = currentRank.colors.background),
        border = BorderStroke(2.dp, currentRank.colors.border),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "TODAY'S FOCUS COMPLETION",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp
                ),
                color = Color(0xFF9CA3AF),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Large circular progress
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(200.dp)
            ) {
                CircularProgressIndicator(
                    progress = { statsUi.focusOverallPercent.toFloat() },
                    modifier = Modifier.fillMaxSize(),
                    color = currentRank.colors.accent,
                    strokeWidth = 16.dp,
                    trackColor = Color.White.copy(alpha = 0.1f),
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${(statsUi.focusOverallPercent * 100).toInt()}%",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = currentRank.colors.accent
                    )
                    Text(
                        text = "${statsUi.focusOverallCompleted}/${statsUi.focusOverallTotal}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF9CA3AF)
                    )
                    Text(
                        text = "TASKS",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF6B7280)
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryBreakdownCard(
    categories: List<CategoryStat>,
    currentRank: Rank
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = currentRank.colors.background),
        border = BorderStroke(1.dp, currentRank.colors.border),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "CATEGORY BREAKDOWN",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp
                ),
                color = currentRank.colors.accent,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            categories.forEach { categoryStat ->
                CategoryStatItem(
                    categoryStat = categoryStat,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun CategoryStatItem(
    categoryStat: CategoryStat,
    modifier: Modifier = Modifier
) {
    val categoryColor = when (categoryStat.category) {
        TaskCategory.PEOPLE -> Color(0xFF60A5FA)
        TaskCategory.OPPORTUNITY -> Color(0xFFFB923C)
        TaskCategory.SKILLS -> Color(0xFF4ADE80)
        else -> Color.White
    }

    val categoryIcon = when (categoryStat.category) {
        TaskCategory.PEOPLE -> Icons.Filled.Groups
        TaskCategory.OPPORTUNITY -> Icons.Filled.Lightbulb
        TaskCategory.SKILLS -> Icons.Filled.Star
        else -> Icons.Filled.CheckCircle
    }

    val categoryName = when (categoryStat.category) {
        TaskCategory.PEOPLE -> "People"
        TaskCategory.OPPORTUNITY -> "Opportunity"
        TaskCategory.SKILLS -> "Skills"
        else -> "Personal"
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = categoryIcon,
                    contentDescription = null,
                    tint = categoryColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = categoryName.uppercase(),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = Color.White
                    )
                    Text(
                        text = "${categoryStat.completed}/${categoryStat.total} tasks",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF9CA3AF)
                    )
                }
            }

            // Circular mini progress
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(60.dp)
            ) {
                CircularProgressIndicator(
                    progress = { categoryStat.percent.toFloat() },
                    modifier = Modifier.fillMaxSize(),
                    color = categoryColor,
                    strokeWidth = 6.dp,
                    trackColor = categoryColor.copy(alpha = 0.2f),
                )
                Text(
                    text = "${(categoryStat.percent * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    ),
                    color = categoryColor
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Linear progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(categoryColor.copy(alpha = 0.2f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(categoryStat.percent.toFloat())
                    .clip(RoundedCornerShape(4.dp))
                    .background(categoryColor)
            )
        }

        // Points info
        if (categoryStat.potentialPoints > 0) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Points: ${categoryStat.earnedPoints}/${categoryStat.potentialPoints}",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF6B7280)
            )
        }
    }
}

@Composable
private fun PointsProgressCard(
    earnedPoints: Int,
    potentialPoints: Int,
    currentRank: Rank
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = currentRank.colors.background),
        border = BorderStroke(1.dp, currentRank.colors.border),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "FOCUS POINTS TODAY",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.5.sp
                        ),
                        color = currentRank.colors.accent
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = earnedPoints.toString(),
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color(0xFF4ADE80)
                        )
                        Text(
                            text = " / $potentialPoints",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Filled.EmojiEvents,
                    contentDescription = null,
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress bar
            val progress = if (potentialPoints > 0) earnedPoints.toFloat() / potentialPoints else 0f
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color.White.copy(alpha = 0.2f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color(0xFF4ADE80))
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${(progress * 100).toInt()}% of potential points earned",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF9CA3AF),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun PersonalTasksCard(
    completed: Int,
    total: Int,
    percent: Double,
    currentRank: Rank
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = currentRank.colors.background),
        border = BorderStroke(1.dp, currentRank.colors.border),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PERSONAL TASKS",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.5.sp
                        ),
                        color = currentRank.colors.accent
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$completed / $total completed",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF9CA3AF)
                    )
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(64.dp)
                ) {
                    CircularProgressIndicator(
                        progress = { percent.toFloat() },
                        modifier = Modifier.fillMaxSize(),
                        color = Color(0xFF8B5CF6),
                        strokeWidth = 7.dp,
                        trackColor = Color(0xFF8B5CF6).copy(alpha = 0.2f),
                    )
                    Text(
                        text = "${(percent * 100).toInt()}%",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFF8B5CF6)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { percent.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color(0xFF8B5CF6),
                trackColor = Color(0xFF8B5CF6).copy(alpha = 0.2f),
            )
        }
    }
}

@Composable
fun FocusTasksStatsTab(
    statsUi: StatsUi,
    currentRank: Rank
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        PointsProgressCard(
            earnedPoints = statsUi.focusEarnedPoints,
            potentialPoints = statsUi.focusPotentialPoints,
            currentRank = currentRank
        )

        OverallFocusCard(
            statsUi = statsUi,
            currentRank = currentRank
        )

        CategoryBreakdownCard(
            categories = statsUi.categories,
            currentRank = currentRank
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun PersonalTasksStatsTab(
    statsUi: StatsUi,
    currentRank: Rank
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        PersonalTasksCard(
            completed = statsUi.personalCompleted,
            total = statsUi.personalTotal,
            percent = statsUi.personalPercent,
            currentRank = currentRank
        )

        // Additional personal task widgets
        PersonalTasksDetailCard(
            statsUi = statsUi,
            currentRank = currentRank
        )

        // Personal Task Progress Card
        PersonalTaskProgressCard(
            statsUi = statsUi,
            currentRank = currentRank
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun PersonalTaskProgressCard(
    statsUi: StatsUi,
    currentRank: Rank
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = currentRank.colors.background),
        border = BorderStroke(1.dp, currentRank.colors.border),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DAILY PROGRESS",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp
                    ),
                    color = currentRank.colors.accent
                )

                Icon(
                    imageVector = Icons.Filled.TrendingUp,
                    contentDescription = null,
                    tint = Color(0xFF8B5CF6),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress visualization
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                repeat(7) { index ->
                    val height = when {
                        index < statsUi.personalCompleted -> 100
                        index == statsUi.personalCompleted -> 60
                        else -> 30
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 2.dp)
                            .height((height / 2).dp)
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(
                                when {
                                    index < statsUi.personalCompleted -> Color(0xFF8B5CF6)
                                    index == statsUi.personalCompleted -> Color(0xFF8B5CF6).copy(alpha = 0.5f)
                                    else -> Color(0xFF8B5CF6).copy(alpha = 0.2f)
                                }
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Keep up the momentum! Complete all tasks to reach 100%",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF9CA3AF),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun PersonalTasksDetailCard(
    statsUi: StatsUi,
    currentRank: Rank
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = currentRank.colors.background),
        border = BorderStroke(1.dp, currentRank.colors.border),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "PERSONAL TASKS OVERVIEW",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp
                ),
                color = currentRank.colors.accent,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Completion rate
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Completion Rate",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF9CA3AF)
                    )
                    Text(
                        text = "${(statsUi.personalPercent * 100).toInt()}%",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFF8B5CF6)
                    )
                }

                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF8B5CF6),
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Task count breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PersonalStatItem(
                    label = "Completed",
                    value = statsUi.personalCompleted.toString(),
                    color = Color(0xFF4ADE80)
                )

                PersonalStatItem(
                    label = "Remaining",
                    value = (statsUi.personalTotal - statsUi.personalCompleted).toString(),
                    color = Color(0xFFFB923C)
                )

                PersonalStatItem(
                    label = "Total",
                    value = statsUi.personalTotal.toString(),
                    color = Color(0xFF60A5FA)
                )
            }
        }
    }
}

@Composable
private fun PersonalStatItem(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF9CA3AF)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun MvpStatsContentPreview() {
    val sampleStats = StatsUi(
        focusOverallCompleted = 5,
        focusOverallTotal = 9,
        focusOverallPercent = 0.56,
        categories = listOf(
            CategoryStat(
                category = TaskCategory.PEOPLE,
                completed = 2,
                total = 3,
                percent = 0.67,
                earnedPoints = 30,
                potentialPoints = 45
            ),
            CategoryStat(
                category = TaskCategory.OPPORTUNITY,
                completed = 1,
                total = 3,
                percent = 0.33,
                earnedPoints = 15,
                potentialPoints = 45
            ),
            CategoryStat(
                category = TaskCategory.SKILLS,
                completed = 2,
                total = 3,
                percent = 0.67,
                earnedPoints = 30,
                potentialPoints = 45
            )
        ),
        focusEarnedPoints = 75,
        focusPotentialPoints = 135,
        personalCompleted = 3,
        personalTotal = 5,
        personalPercent = 0.6
    )

    val sampleRank = Rank(
        name = "Warrior",
        minPoints = 100,
        maxPoints = 299,
        description = "Building consistent habits",
        icon = Icons.Filled.Star,
        colors = RankColors(
            background = Color(0xFF1F1F1F),
            border = Color.White.copy(alpha = 0.2f),
            accent = Color.White
        )
    )

    MvpStatsContent(
        statsUi = sampleStats,
        currentRank = sampleRank
    )
}
