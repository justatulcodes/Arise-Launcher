package com.expeknow.ariselauncher.ui.screens.home

import androidx.compose.ui.graphics.Color
import com.expeknow.ariselauncher.data.model.Task

data class TaskDetailsState(
    val task: Task? = null,
    val isLoading: Boolean = true,
    val expandedLinkId: String? = null,
    val showCompletionBanner: Boolean = false
)

sealed class TaskDetailsEvent {
    data class LoadTask(val taskId: String) : TaskDetailsEvent()
    data class CompleteTask(val taskId: String) : TaskDetailsEvent()
    data class ToggleTask(val taskId: String) : TaskDetailsEvent()
    data class ExpandLink(val linkId: String?) : TaskDetailsEvent()
    data class OpenLink(val url: String) : TaskDetailsEvent()
    data object NavigateBack : TaskDetailsEvent()
}

data class TaskDetailsTheme(
    val background: Color = Color.Black,
    val surface: Color = Color(0xFF1F1F1F),
    val accent: Color = Color.White,
    val textPrimary: Color = Color.White,
    val textSecondary: Color = Color(0xFF9CA3AF),
    val border: Color = Color(0xFF374151),
    val completedGreen: Color = Color(0xFF4ADE80)
)