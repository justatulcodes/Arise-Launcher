package com.expeknow.ariselauncher.ui.screens.targets

import com.expeknow.ariselauncher.data.datasource.Target

data class TargetsState(
    val targets: List<Target> = emptyList(),
    val showAddDialog: Boolean = false,
    val editingTarget: Target? = null,
    val isLoading: Boolean = false
)

sealed class TargetsEvent {
    data class AddTarget(
        val name: String,
        val description: String,
        val endDate: Long,
        val showOnHomeScreen: Boolean
    ) : TargetsEvent()

    data class UpdateTarget(val target: Target) : TargetsEvent()
    data class DeleteTarget(val targetId: String) : TargetsEvent()
    data class UpdateProgress(val targetId: String, val progress: Float) : TargetsEvent()
    data object ShowAddDialog : TargetsEvent()
    data object HideAddDialog : TargetsEvent()
    data class StartEditTarget(val target: Target) : TargetsEvent()
    data object CancelEdit : TargetsEvent()
}

