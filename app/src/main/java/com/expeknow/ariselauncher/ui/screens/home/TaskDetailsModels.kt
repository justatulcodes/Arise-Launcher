package com.expeknow.ariselauncher.ui.screens.home

import androidx.compose.ui.graphics.Color
import com.expeknow.ariselauncher.data.model.Task
import com.expeknow.ariselauncher.data.model.TaskLinkType

data class TaskDetailsState(
    val task: Task? = null,
    val isLoading: Boolean = true,
    val expandedLinkId: String? = null,
    val showCompletionBanner: Boolean = false,
    val isEditingTitle: Boolean = false,
    val isEditingDescription: Boolean = false,
    val isAddingLink: Boolean = false,
    val editingTitleText: String = "",
    val editingDescriptionText: String = "",
    val newLinkTitle: String = "",
    val newLinkUrl: String = "",
    val newLinkType: TaskLinkType = TaskLinkType.LINK,
    val newLinkDescription: String = ""
)

sealed class TaskDetailsEvent {
    data class LoadTask(val taskId: String) : TaskDetailsEvent()
    data class CompleteTask(val taskId: String) : TaskDetailsEvent()
    data class ToggleTask(val taskId: String) : TaskDetailsEvent()
    data class ExpandLink(val linkId: String?) : TaskDetailsEvent()
    data class OpenLink(val url: String) : TaskDetailsEvent()
    data object NavigateBack : TaskDetailsEvent()

    // Editing events
    data object StartEditingTitle : TaskDetailsEvent()
    data object StartEditingDescription : TaskDetailsEvent()
    data object StartAddingLink : TaskDetailsEvent()
    data object CancelEditing : TaskDetailsEvent()
    data class UpdateTitleText(val text: String) : TaskDetailsEvent()
    data class UpdateDescriptionText(val text: String) : TaskDetailsEvent()
    data object SaveTitle : TaskDetailsEvent()
    data object SaveDescription : TaskDetailsEvent()

    // Link management events
    data class UpdateNewLinkTitle(val title: String) : TaskDetailsEvent()
    data class UpdateNewLinkUrl(val url: String) : TaskDetailsEvent()
    data class UpdateNewLinkDescription(val description: String) : TaskDetailsEvent()
    data class UpdateNewLinkType(val type: TaskLinkType) : TaskDetailsEvent()
    data object SaveNewLink : TaskDetailsEvent()
    data class RemoveLink(val linkId: String) : TaskDetailsEvent()
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