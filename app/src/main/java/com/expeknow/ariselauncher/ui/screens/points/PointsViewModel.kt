package com.expeknow.ariselauncher.ui.screens.points

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PointsViewModel : ViewModel() {

    private val _state = MutableStateFlow(PointsState())
    val state: StateFlow<PointsState> = _state.asStateFlow()

    fun onEvent(event: PointsEvent) {
        when (event) {
            is PointsEvent.SelectTab -> {
                _state.value = _state.value.copy(selectedTabIndex = event.index)
            }

            is PointsEvent.SetDebugRank -> {
                _state.value = _state.value.copy(debugCurrentRank = event.rank)
            }

            is PointsEvent.NavigateToTaskHistory -> {
                // Handle navigation - this would typically involve a navigation callback
            }
        }
    }
}