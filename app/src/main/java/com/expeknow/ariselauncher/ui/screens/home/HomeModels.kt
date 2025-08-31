package com.expeknow.ariselauncher.ui.screens.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.expeknow.ariselauncher.data.model.AppInfo
import com.expeknow.ariselauncher.data.model.Task
import com.expeknow.ariselauncher.data.model.TaskCategory

data class FocusCategory(
    val id: TaskCategory,
    val name: String,
    val icon: ImageVector,
    val color: androidx.compose.ui.graphics.Color,
    val bgColor: androidx.compose.ui.graphics.Color,
    val borderColor: androidx.compose.ui.graphics.Color
)

enum class HomeMode {
    SIMPLE, FOCUSED
}

enum class PointsTrend {
    INCREASING, DECREASING, STABLE
}

data class HomeState(
    val currentPoints: Int = 0,
    val pointChange: Int = 0,
    val pointsTrend: PointsTrend = PointsTrend.STABLE,
    val completedTasks: Int = 0,
    val totalTasks: Int = 0,
    val apps: List<AppInfo> = emptyList(),
    val tasks: List<Task> = emptyList(),
    val showAddTaskDialog: Boolean = false,
    val showEssentialAppsSheet: Boolean = false,
    val tunnelVisionMode: Boolean = true,
    val mode: HomeMode = HomeMode.SIMPLE,
    val hideCompletedTasks: Boolean = true,
    val focusCategories: List<FocusCategory> = getDefaultFocusCategories(),
    val editingCategoryId: TaskCategory? = null,
    val editingCategoryName: String = "",
    val expandedLinkId: String? = null
)

sealed class HomeEvent {
    data object LoadApps : HomeEvent()
    data object LoadTasks : HomeEvent()
    data class CompleteTask(val taskId: String) : HomeEvent()
    data class ToggleTask(val taskId: String) : HomeEvent()
    data class AddTask(
        val title: String,
        val description: String,
        val points: Int,
        val category: TaskCategory = TaskCategory.PERSONAL
    ) : HomeEvent()
    data object ShowAddTaskDialog : HomeEvent()
    data object HideAddTaskDialog : HomeEvent()
    data object ShowEssentialAppsSheet : HomeEvent()
    data object HideEssentialAppsSheet : HomeEvent()
    data class NavigateToTaskDetails(val taskId: String) : HomeEvent()
    data class LaunchApp(val appName: String) : HomeEvent()
    data object ToggleMode : HomeEvent()
    data object ToggleHideCompletedTasks : HomeEvent()
    data class StartEditingCategory(val categoryId: TaskCategory) : HomeEvent()
    data class SaveEditingCategory(val name: String) : HomeEvent()
    data object CancelEditingCategory : HomeEvent()
    data class UpdateEditingCategoryName(val name: String) : HomeEvent()
    data class ExpandLink(val linkId: String?) : HomeEvent()
}

data class HomeTheme(
    val background: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Black,
    val surface: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color(0xFF1F1F1F),
    val accent: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.White,
    val textPrimary: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.White,
    val textSecondary: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color(
        0xFF9CA3AF
    ),
    val border: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color(0xFF374151),
    val bg: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color(0xFF1F1F1F)
)

private fun getDefaultFocusCategories(): List<FocusCategory> = listOf(
    FocusCategory(
        id = TaskCategory.INTELLIGENCE,
        name = "Becoming Incredibly Intelligent",
        icon = Icons.Filled.Psychology,
        color = androidx.compose.ui.graphics.Color(0xFF60A5FA),
        bgColor = androidx.compose.ui.graphics.Color(0xFF60A5FA).copy(alpha = 0.1f),
        borderColor = androidx.compose.ui.graphics.Color(0xFF60A5FA).copy(alpha = 0.3f)
    ),
    FocusCategory(
        id = TaskCategory.PHYSICAL,
        name = "Becoming Incredibly Muscular",
        icon = Icons.Filled.FitnessCenter,
        color = androidx.compose.ui.graphics.Color(0xFFFB923C),
        bgColor = androidx.compose.ui.graphics.Color(0xFFFB923C).copy(alpha = 0.1f),
        borderColor = androidx.compose.ui.graphics.Color(0xFFFB923C).copy(alpha = 0.3f)
    ),
    FocusCategory(
        id = TaskCategory.WEALTH,
        name = "Becoming Incredibly Rich",
        icon = Icons.Filled.AttachMoney,
        color = androidx.compose.ui.graphics.Color(0xFF4ADE80),
        bgColor = androidx.compose.ui.graphics.Color(0xFF4ADE80).copy(alpha = 0.1f),
        borderColor = androidx.compose.ui.graphics.Color(0xFF4ADE80).copy(alpha = 0.3f)
    )
)