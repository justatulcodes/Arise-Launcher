package com.expeknow.ariselauncher.ui.screens.points

import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Smartphone
import com.expeknow.ariselauncher.data.model.*

data class PointsState(
    val currentPoints: Int = 0,
    val totalEarned: Int = 0,
    val totalBurned: Int = 0,
    val selectedTabIndex: Int = 0,
    val debugCurrentRank: Rank? = null,
    val pointsHistory: List<PointsHistory> = emptyList(),
    val taskStats: TaskStats = TaskStats(
        totalTasks = 0,
        completedTasks = 0,
        urgentTasks = 0,
        workTasks = 0,
        personalTasks = 0,
        completionRatio = 0f,
        todayCompleted = 0,
        weeklyAverage = 0f
    ),
    val activities: List<PointActivity> = emptyList()
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