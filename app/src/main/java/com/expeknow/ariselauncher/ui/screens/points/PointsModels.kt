package com.expeknow.ariselauncher.ui.screens.points

import com.expeknow.ariselauncher.data.model.*
import com.expeknow.ariselauncher.ui.screens.home.StatsUi

/**
 * Heatmap data for a single category.
 * Contains a 2D grid where rows are weeks and columns are days.
 * Each cell contains the count of completed tasks for that day.
 */
data class CategoryHeatmapData(
    val category: TaskCategory,
    val weeklyData: List<List<Int>> = emptyList() // 7 weeks x 7 days (or configurable)
)

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
    val isTunnelVisionMode: Boolean = false,
    val peopleHeatmap: CategoryHeatmapData = CategoryHeatmapData(TaskCategory.PEOPLE),
    val opportunityHeatmap: CategoryHeatmapData = CategoryHeatmapData(TaskCategory.OPPORTUNITY),
    val skillsHeatmap: CategoryHeatmapData = CategoryHeatmapData(TaskCategory.SKILLS),
    val personalHeatmap: CategoryHeatmapData = CategoryHeatmapData(TaskCategory.PERSONAL)
)

sealed class StatsEvent {
    data class SelectTab(val index: Int) : StatsEvent()
    data class SetDebugRank(val rank: Rank?) : StatsEvent()
    data object NavigateToTaskHistory : StatsEvent()
}

enum class TabType(val index: Int, val title: String) {
    OVERVIEW(0, "OVERVIEW"),
    TASKS(1, "TASKS"),
}

typealias PointsState = StatsScreenState
typealias PointsEvent = StatsEvent
