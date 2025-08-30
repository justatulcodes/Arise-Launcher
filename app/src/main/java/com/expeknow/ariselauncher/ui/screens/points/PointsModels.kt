package com.expeknow.ariselauncher.ui.screens.points

import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Smartphone
import com.expeknow.ariselauncher.data.model.*

data class PointsState(
    val currentPoints: Int = 245,
    val totalEarned: Int = 520,
    val totalBurned: Int = 275,
    val selectedTabIndex: Int = 0,
    val debugCurrentRank: Rank? = null,
    val pointsHistory: List<PointsHistory> = getDefaultPointsHistory(),
    val taskStats: TaskStats = getDefaultTaskStats(),
    val activities: List<PointActivity> = getDefaultActivities()
)

private fun getDefaultPointsHistory(): List<PointsHistory> = listOf(
    PointsHistory("Mon", 180),
    PointsHistory("Tue", 195),
    PointsHistory("Wed", 220),
    PointsHistory("Thu", 210),
    PointsHistory("Fri", 235),
    PointsHistory("Sat", 250),
    PointsHistory("Sun", 245)
)

private fun getDefaultTaskStats(): TaskStats = TaskStats(
    totalTasks = 12,
    completedTasks = 8,
    urgentTasks = 3,
    workTasks = 5,
    personalTasks = 4,
    completionRatio = 66.7f,
    todayCompleted = 5,
    weeklyAverage = 7.2f
)

private fun getDefaultActivities(): List<PointActivity> = listOf(
    PointActivity(
        "1",
        ActivityType.EARN,
        25,
        "Completed morning workout",
        "2h ago",
        androidx.compose.material.icons.Icons.Filled.FitnessCenter
    ),
    PointActivity(
        "2",
        ActivityType.BURN,
        -10,
        "Instagram usage (5min)",
        "3h ago",
        androidx.compose.material.icons.Icons.Filled.Smartphone
    ),
    PointActivity(
        "3",
        ActivityType.EARN,
        30,
        "Finished reading session",
        "4h ago",
        androidx.compose.material.icons.Icons.Filled.Book
    ),
    PointActivity(
        "4",
        ActivityType.BURN,
        -15,
        "YouTube usage (8min)",
        "5h ago",
        androidx.compose.material.icons.Icons.Filled.Smartphone
    ),
    PointActivity(
        "5",
        ActivityType.EARN,
        20,
        "Team meeting completed",
        "6h ago",
        androidx.compose.material.icons.Icons.Filled.Group
    )
)

sealed class PointsEvent {
    data class SelectTab(val index: Int) : PointsEvent()
    data class SetDebugRank(val rank: Rank?) : PointsEvent()
    data object NavigateToTaskHistory : PointsEvent()
}

enum class TabType(val index: Int, val title: String) {
    OVERVIEW(0, "OVERVIEW"),
    TASKS(1, "TASKS"),
    RANKS(2, "RANKS")
}