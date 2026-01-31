package com.expeknow.ariselauncher.ui.screens.points

import com.expeknow.ariselauncher.data.model.*
import com.expeknow.ariselauncher.ui.screens.home.StatsUi

data class StatsScreenState(
    val currentPoints: Int = 0,
    val totalEarned: Int = 0,
    val totalBurned: Int = 0,
    val selectedTabIndex: Int = 0,
    val debugCurrentRank: Rank? = ranks[8],
    val pointsHistory: List<PointsHistory> = emptyList(),
    val taskStats: TaskStats = TaskStats(
        totalTasks = 0,
        completedTasks = 0,
        personalTasks = 0,
        completionRatio = 0f,
        todayCompleted = 0,
        weeklyAverage = 0f
    ),
    val activities: List<PointActivity> = emptyList(),
    val completedTasks: List<Task> = emptyList(),
    val currentRank: Rank = ranks[8],
    val mvpStats: StatsUi = StatsUi(),
    val isTunnelVisionMode: Boolean = false
)

sealed class StatsEvent {
    data class SelectTab(val index: Int) : StatsEvent()
    data class SetDebugRank(val rank: Rank?) : StatsEvent()
    data object NavigateToTaskHistory : StatsEvent()
}

enum class TabType(val index: Int, val title: String) {
    OVERVIEW(0, "OVERVIEW"),
    TASKS(1, "TASKS"),
//    RANKS(2, "RANKS") //removing it from alpha version
}