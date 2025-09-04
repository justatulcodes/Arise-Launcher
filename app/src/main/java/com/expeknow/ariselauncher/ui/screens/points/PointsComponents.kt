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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.expeknow.ariselauncher.data.model.*
import com.expeknow.ariselauncher.ui.components.CustomChart
import com.expeknow.ariselauncher.ui.components.CustomProgressBar

@Composable
fun PointsHeader(
    selectedTabIndex: Int,
    onTabSelect: (Int) -> Unit,
    currentRank: Rank
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(currentRank.colors.background)
            .border(1.dp, currentRank.colors.border)
            .padding(24.dp)
    ) {
        Text(
            text = "POINTS & STATS",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.2.sp
            ),
            color = currentRank.colors.accent,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Custom Tab Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    currentRank.colors.background,
                    RoundedCornerShape(8.dp)
                )
                .border(
                    1.dp,
                    currentRank.colors.border,
                    RoundedCornerShape(8.dp)
                )
                .padding(4.dp)
        ) {
            TabType.values().forEach { tab ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (selectedTabIndex == tab.index) Color.White else Color.Transparent,
                            RoundedCornerShape(4.dp)
                        )
                        .clickable { onTabSelect(tab.index) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab.title,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.5.sp
                        ),
                        color = if (selectedTabIndex == tab.index) Color.Black else Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun CurrentStatusCard(
    currentPoints: Int,
    currentRank: Rank,
    nextRank: Rank?,
    progressToNext: Float
) {
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
            // Points display
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = currentRank.icon,
                    contentDescription = null,
                    tint = currentRank.colors.accent,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = currentPoints.toString(),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = currentRank.colors.accent
                )
            }

            Text(
                text = "CURRENT POINTS",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.sp
                ),
                color = Color(0xFF9CA3AF)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Rank Progress
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "CURRENT RANK",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF9CA3AF)
                    )
                    Text(
                        text = currentRank.name,
                        style = MaterialTheme.typography.labelSmall,
                        color = currentRank.colors.accent
                    )
                }

                if (nextRank != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "NEXT: ${nextRank.name}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF6B7280)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    CustomProgressBar(
                        progress = progressToNext / 100f,
                        color = currentRank.colors.accent,
                        backgroundColor = currentRank.colors.background
                    )

                    Text(
                        text = "${progressToNext.toInt()}% TO NEXT RANK",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF6B7280),
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                            .padding(top = 4.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "MAXIMUM RANK ACHIEVED",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = currentRank.colors.accent,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun PointsGrowthChart(
    pointsHistory: List<PointsHistory>,
    currentRank: Rank
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = currentRank.colors.background),
        border = BorderStroke(1.dp, currentRank.colors.border),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "POINTS GROWTH (7 DAYS)",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = currentRank.colors.accent,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            CustomChart(
                data = pointsHistory,
                color = currentRank.colors.accent
            )

            // Chart stats
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                ChartStat(
                    icon = Icons.Filled.TrendingUp,
                    text = "Peak: ${pointsHistory.maxOf { it.points }}",
                    color = Color(0xFF4ADE80),
                    modifier = Modifier.padding(end = 16.dp)
                )

                ChartStat(
                    icon = Icons.Filled.TrendingDown,
                    text = "Low: ${pointsHistory.minOf { it.points }}",
                    color = Color(0xFFF87171)
                )
            }
        }
    }
}

@Composable
fun StatsGrid(
    totalEarned: Int,
    totalBurned: Int,
    currentRank: Rank
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard(
            icon = Icons.Filled.TrendingUp,
            value = totalEarned.toString(),
            label = "TOTAL EARNED",
            color = Color(0xFF4ADE80),
            currentRank = currentRank,
            modifier = Modifier.weight(1f)
        )

        StatCard(
            icon = Icons.Filled.TrendingDown,
            value = totalBurned.toString(),
            label = "TOTAL BURNED",
            color = Color(0xFFF87171),
            currentRank = currentRank,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun PointSystemCard(currentRank: Rank) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = currentRank.colors.background),
        border = BorderStroke(1.dp, currentRank.colors.border),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "POINT SYSTEM",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = currentRank.colors.accent,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            val rules = listOf(
                "Complete Task" to "+10-50 pts",
                "Social Media (per min)" to "-2 pts",
                "Games (per min)" to "-5 pts",
                "Productivity Streak" to "+25 pts"
            )

            rules.forEach { (rule, points) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = rule,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFB3B3B3)
                    )
                    Text(
                        text = points,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (points.startsWith("+")) Color(0xFF4ADE80) else Color(0xFFF87171)
                    )
                }
            }
        }
    }
}

@Composable
fun RecentActivityCard(
    activities: List<PointActivity>,
    currentRank: Rank
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = currentRank.colors.background),
        border = BorderStroke(1.dp, currentRank.colors.border),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "RECENT ACTIVITY",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = currentRank.colors.accent,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            activities.forEachIndexed { index, activity ->
                ActivityItem(
                    activity = activity,
                    showDivider = index < activities.size - 1
                )
            }
        }
    }
}

@Composable
private fun ChartStat(
    icon: ImageVector,
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF9CA3AF)
        )
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    value: String,
    label: String,
    color: Color,
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = color
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF9CA3AF)
            )
        }
    }
}

@Composable
private fun ActivityItem(
    activity: PointActivity,
    showDivider: Boolean
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        if (activity.type == ActivityType.EARN) Color(0x334ADE80) else Color(
                            0x33F87171
                        ),
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = activity.icon,
                    contentDescription = null,
                    tint = if (activity.type == ActivityType.EARN) Color(0xFF4ADE80) else Color(
                        0xFFF87171
                    ),
                    modifier = Modifier.size(14.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activity.activity,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
                Text(
                    text = activity.time,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF6B7280)
                )
            }

            Surface(
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(
                    1.dp,
                    if (activity.type == ActivityType.EARN) Color(0x664ADE80) else Color(0x66F87171)
                ),
                color = Color.Transparent
            ) {
                Text(
                    text = if (activity.points > 0) "+${activity.points}" else activity.points.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (activity.type == ActivityType.EARN) Color(0xFF4ADE80) else Color(
                        0xFFF87171
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = Color(0x1AFFFFFF)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun PointsHeaderPreview() {
    // Create a sample rank for preview
    val sampleRank = Rank(
        name = "Warrior",
        minPoints = 100,
        maxPoints = 299,
        description = "Sample rank for preview",
        icon = androidx.compose.material.icons.Icons.Filled.Star,
        colors = RankColors(
            background = Color(0xFF1F1F1F),
            border = Color.White.copy(alpha = 0.2f),
            accent = Color.White
        )
    )

    PointsHeader(
        selectedTabIndex = 0,
        onTabSelect = {},
        currentRank = sampleRank
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun CurrentStatusCardPreview() {
    // Create sample ranks for preview
    val currentRank = Rank(
        name = "Warrior",
        minPoints = 100,
        maxPoints = 299,
        description = "Building consistent habits",
        icon = androidx.compose.material.icons.Icons.Filled.Star,
        colors = RankColors(
            background = Color(0xFF1F1F1F),
            border = Color.White.copy(alpha = 0.2f),
            accent = Color.White
        )
    )

    val nextRank = Rank(
        name = "Champion",
        minPoints = 300,
        maxPoints = 599,
        description = "Master of discipline",
        icon = androidx.compose.material.icons.Icons.Filled.EmojiEvents,
        colors = RankColors(
            background = Color(0xFF1F1F1F),
            border = Color(0xFFFFD700).copy(alpha = 0.3f),
            accent = Color(0xFFFFD700)
        )
    )

    CurrentStatusCard(
        currentPoints = 245,
        currentRank = currentRank,
        nextRank = nextRank,
        progressToNext = 65f
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun StatsGridPreview() {
    val sampleRank = Rank(
        name = "Warrior",
        minPoints = 100,
        maxPoints = 299,
        description = "Building consistent habits",
        icon = androidx.compose.material.icons.Icons.Filled.Star,
        colors = RankColors(
            background = Color(0xFF1F1F1F),
            border = Color.White.copy(alpha = 0.2f),
            accent = Color.White
        )
    )

    StatsGrid(
        totalEarned = 520,
        totalBurned = 275,
        currentRank = sampleRank
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun PointSystemCardPreview() {
    val sampleRank = Rank(
        name = "Warrior",
        minPoints = 100,
        maxPoints = 299,
        description = "Building consistent habits",
        icon = androidx.compose.material.icons.Icons.Filled.Star,
        colors = RankColors(
            background = Color(0xFF1F1F1F),
            border = Color.White.copy(alpha = 0.2f),
            accent = Color.White
        )
    )

    PointSystemCard(currentRank = sampleRank)
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun RecentActivityCardPreview() {
    val sampleRank = Rank(
        name = "Warrior",
        minPoints = 100,
        maxPoints = 299,
        description = "Building consistent habits",
        icon = androidx.compose.material.icons.Icons.Filled.Star,
        colors = RankColors(
            background = Color(0xFF1F1F1F),
            border = Color.White.copy(alpha = 0.2f),
            accent = Color.White
        )
    )

    val sampleActivities = listOf(
        PointActivity(
            "1",
            ActivityType.EARN,
            25,
            "Completed morning workout",
            "2h ago",
            "CheckCircle"
        ),
        PointActivity(
            "2",
            ActivityType.BURN,
            -10,
            "Instagram usage (5min)",
            "3h ago",
            "Smartphone"
        )
    )

    RecentActivityCard(
        activities = sampleActivities,
        currentRank = sampleRank
    )
}