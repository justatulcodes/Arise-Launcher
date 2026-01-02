package com.expeknow.ariselauncher.ui.screens.targets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expeknow.ariselauncher.data.datasource.Target
import com.expeknow.ariselauncher.data.datasource.TargetsPreferencesDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TargetsViewModel @Inject constructor(
    private val targetsDataSource: TargetsPreferencesDataSource
) : ViewModel() {

    private val _state = MutableStateFlow(TargetsState())
    val state: StateFlow<TargetsState> = _state.asStateFlow()

    init {
        loadTargets()
    }

    private fun loadTargets() {
        viewModelScope.launch {
            targetsDataSource.targetsFlow.collect { targets ->
                _state.value = _state.value.copy(
                    targets = targets.sortedBy { it.endDate }
                )
            }
        }
    }

    fun onEvent(event: TargetsEvent) {
        when (event) {
            is TargetsEvent.AddTarget -> {
                val target = Target(
                    id = UUID.randomUUID().toString(),
                    name = event.name,
                    description = event.description,
                    endDate = event.endDate,
                    progress = 0f,
                    createdAt = System.currentTimeMillis()
                )
                targetsDataSource.addTarget(target)
                _state.value = _state.value.copy(showAddDialog = false)
            }

            is TargetsEvent.UpdateTarget -> {
                targetsDataSource.updateTarget(event.target)
                _state.value = _state.value.copy(
                    showAddDialog = false,
                    editingTarget = null
                )
            }

            is TargetsEvent.DeleteTarget -> {
                targetsDataSource.deleteTarget(event.targetId)
            }

            is TargetsEvent.UpdateProgress -> {
                val target = _state.value.targets.find { it.id == event.targetId }
                if (target != null) {
                    val updatedTarget = target.copy(progress = event.progress.coerceIn(0f, 100f))
                    targetsDataSource.updateTarget(updatedTarget)
                }
            }

            is TargetsEvent.ToggleComplete -> {
                val target = _state.value.targets.find { it.id == event.targetId }
                if (target != null) {
                    val updatedTarget = target.copy(isCompleted = !target.isCompleted)
                    targetsDataSource.updateTarget(updatedTarget)
                }
            }

            TargetsEvent.ShowAddDialog -> {
                _state.value = _state.value.copy(
                    showAddDialog = true,
                    editingTarget = null
                )
            }

            TargetsEvent.HideAddDialog -> {
                _state.value = _state.value.copy(
                    showAddDialog = false,
                    editingTarget = null
                )
            }

            is TargetsEvent.StartEditTarget -> {
                _state.value = _state.value.copy(
                    showAddDialog = true,
                    editingTarget = event.target
                )
            }

            TargetsEvent.CancelEdit -> {
                _state.value = _state.value.copy(
                    showAddDialog = false,
                    editingTarget = null
                )
            }
        }
    }
}

