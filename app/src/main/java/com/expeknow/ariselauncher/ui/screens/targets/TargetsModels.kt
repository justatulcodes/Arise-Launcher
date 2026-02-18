package com.expeknow.ariselauncher.ui.screens.targets

import com.expeknow.ariselauncher.data.model.Targets

data class TargetsState(
    val targets: List<Targets> = emptyList(),
    val showAddDialog: Boolean = false,
    val editingTarget: Targets? = null,
    val isLoading: Boolean = false
)

sealed class TargetsEvent {
    data class AddTarget(
        val name: String,
        val description: String,
        val endDate: Long,
        val showOnHomeScreen: Boolean
    ) : TargetsEvent()

    data class UpdateTarget(val target: Targets) : TargetsEvent()
    data class DeleteTarget(val targetId: String) : TargetsEvent()
    data class UpdateProgress(val targetId: String, val progress: Float) : TargetsEvent()
    data object ShowAddDialog : TargetsEvent()
    data object HideAddDialog : TargetsEvent()
    data class StartEditTarget(val target: Targets) : TargetsEvent()
    data object CancelEdit : TargetsEvent()
}

