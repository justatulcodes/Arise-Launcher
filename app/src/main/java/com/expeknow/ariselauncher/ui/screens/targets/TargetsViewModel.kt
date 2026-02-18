package com.expeknow.ariselauncher.ui.screens.targets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expeknow.ariselauncher.data.datasource.interfaces.TargetsDataSource
import com.expeknow.ariselauncher.data.model.Targets
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TargetsViewModel @Inject constructor(
    private val targetsDataSource: TargetsDataSource
) : ViewModel() {

    private val _state = MutableStateFlow(TargetsState())
    val state: StateFlow<TargetsState> = _state.asStateFlow()

    init {
        loadTargets()
    }

    private fun loadTargets() {
        viewModelScope.launch {
            targetsDataSource.getAllTargets().collect { targets ->
                _state.value = _state.value.copy(
                    targets = targets.sortedBy { it.endDate }
                )
            }
        }
    }

    fun onEvent(event: TargetsEvent) {
        when (event) {
            is TargetsEvent.AddTarget -> {
                viewModelScope.launch {
                    val target = Targets(
                        id = UUID.randomUUID().toString(),
                        name = event.name,
                        description = event.description,
                        endDate = event.endDate,
                        progress = 0f,
                        createdAt = System.currentTimeMillis(),
                        showOnHomeScreen = event.showOnHomeScreen
                    )
                    targetsDataSource.addTarget(target)
                    _state.value = _state.value.copy(showAddDialog = false)
                }
            }

            is TargetsEvent.UpdateTarget -> {
                viewModelScope.launch {
                    targetsDataSource.updateTarget(event.target)
                    _state.value = _state.value.copy(
                        showAddDialog = false,
                        editingTarget = null
                    )
                }

            }

            is TargetsEvent.DeleteTarget -> {
                viewModelScope.launch {
                    targetsDataSource.deleteTarget(event.targetId)
                }

            }

            is TargetsEvent.UpdateProgress -> {
                val target = _state.value.targets.find { it.id == event.targetId }
                if (target != null) {
                    val updatedTarget = target.copy(progress = event.progress.coerceIn(0f, 100f))
                    viewModelScope.launch {
                        targetsDataSource.updateTarget(updatedTarget)
                    }
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

