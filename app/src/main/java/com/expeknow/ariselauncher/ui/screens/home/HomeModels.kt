package com.expeknow.ariselauncher.ui.screens.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.expeknow.ariselauncher.data.model.DaysOfWeek
import com.expeknow.ariselauncher.data.model.Task
import com.expeknow.ariselauncher.data.model.TaskCategory
import com.expeknow.ariselauncher.data.datasource.Target
import com.expeknow.ariselauncher.ui.screens.apps.AppDrawerApp

data class StatsUi(
    val focusOverallCompleted: Int = 0,
    val focusOverallTotal: Int = 0,
    val focusOverallPercent: Double = 0.0,
    val categories: List<CategoryStat> = emptyList(),
    val focusEarnedPoints: Int = 0,
    val focusPotentialPoints: Int = 0,
    val personalCompleted: Int = 0,
    val personalTotal: Int = 0,
    val personalPercent: Double = 0.0
)

data class CategoryStat(
    val category: TaskCategory,
    val completed: Int,
    val total: Int,
    val percent: Double,
    val earnedPoints: Int,
    val potentialPoints: Int
)

data class FocusCategory(
    val id: TaskCategory,
    val name: String,
    val icon: ImageVector,
    val color: Color,
    val bgColor: Color,
    val borderColor: Color
)

enum class HomeMode {
    SIMPLE, FOCUSED
}

enum class PointsTrend {
    INCREASING, DECREASING, STABLE
}

data class HomeState(
    val countdown: Int = 10,
    val isUnlocked: Boolean = false,
    val selectedApp: AppDrawerApp? = null,
    val showWarning: Boolean = false,
    val currentPoints: Int = 0,
    val pointChange: Int = 0,
    val pointsTrend: PointsTrend = PointsTrend.STABLE,
    val normalCompletedTasks: Int = 0,
    val normalTotalTasks: Int = 0,
    val focusedCompletedTasks: Int = 0,
    val focusedTotalTasks: Int = 0,
    val earnedPoints: Int = 0,
    val apps: List<AppDrawerApp> =emptyList(),
    val normalTasks: List<Task> = emptyList(),
    val allFocusedTasks: List<Task> = emptyList(),
    val todayFocusedTasks : List<Task> = emptyList(),
    val showAddTaskDialog: Boolean = false,
    val tunnelVisionMode: Boolean = true,
    val mode: HomeMode = HomeMode.SIMPLE,
    val hideCompletedTasks: Boolean = true,
    val showWeeklySchedule: Boolean = false,
    val showCategorizedApps : Boolean = false,
    val focusCategories: List<FocusCategory> = getDefaultFocusCategories(),
    val editingCategoryId: TaskCategory? = null,
    val editingCategoryName: String = "",
    val expandedLinkId: String? = null,
    val currentPage: Int = 1, // 0 = blank, 1 = main tasks, 2 = alternate (focused mode only)
    val statsUi: StatsUi = StatsUi(), // MVP stats
    val targets: List<Target> = emptyList() // User's targets
)

sealed class HomeEvent {
    data object LoadApps : HomeEvent()
    data object LoadTasks : HomeEvent()
    data class CompleteTask(val taskId: String) : HomeEvent()
    data class ToggleTask(val task: Task) : HomeEvent()
    data class AddTask(
        val title: String,
        val description: String,
        val points: Int,
        val category: TaskCategory = TaskCategory.PERSONAL,
        val isRepeated: Boolean = false,
        val repeatDays: List<DaysOfWeek> = emptyList()
    ) : HomeEvent()
    data object ShowAddTaskDialog : HomeEvent()
    data object HideAddTaskDialog : HomeEvent()
    data class NavigateToTaskDetails(val taskId: String) : HomeEvent()
    data class LaunchApp(val app: AppDrawerApp) : HomeEvent()
    data class StartEditingCategory(val categoryId: TaskCategory) : HomeEvent()
    data class SaveEditingCategory(val name: String) : HomeEvent()
    data object CancelEditingCategory : HomeEvent()
    data class UpdateEditingCategoryName(val name: String) : HomeEvent()
    data class ExpandLink(val linkId: String?) : HomeEvent()
    data class UpdateCurrentPage(val page: Int) : HomeEvent()
}

data class HomeTheme(
    val background: Color = Color.Black,
    val surface: Color = Color(0xFF1F1F1F),
    val accent: Color = Color.White,
    val textPrimary: Color = Color.White,
    val textSecondary: Color = Color(
        0xFF9CA3AF
    ),
    val border: Color = Color(0xFF374151),
    val bg: Color = Color(0xFF1F1F1F)
)

private fun getDefaultFocusCategories(): List<FocusCategory> = listOf(
    FocusCategory(
        id = TaskCategory.PEOPLE,
        name = "Improving People Interactions",
        icon = Icons.Filled.Groups,
        color = Color(0xFF60A5FA),
        bgColor = Color(0xFF60A5FA).copy(alpha = 0.1f),
        borderColor = Color(0xFF60A5FA).copy(alpha = 0.3f)
    ),
    FocusCategory(
        id = TaskCategory.OPPORTUNITY,
        name = "Identifying and Creating Opportunities",
        icon = Icons.Filled.Lightbulb,
        color = Color(0xFFFB923C),
        bgColor = Color(0xFFFB923C).copy(alpha = 0.1f),
        borderColor = Color(0xFFFB923C).copy(alpha = 0.3f)
    ),
    FocusCategory(
        id = TaskCategory.SKILLS,
        name = "Becoming Highly Skilled",
        icon = Icons.Filled.AttachMoney,
        color = Color(0xFF4ADE80),
        bgColor = Color(0xFF4ADE80).copy(alpha = 0.1f),
        borderColor = Color(0xFF4ADE80).copy(alpha = 0.3f)
    )
)