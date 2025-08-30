package com.expeknow.ariselauncher.ui.screens.apps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppDrawerViewModel : ViewModel() {

    private val _state = MutableStateFlow(AppDrawerState())
    val state: StateFlow<AppDrawerState> = _state.asStateFlow()

    init {
        startCountdown()
    }

    private fun startCountdown() {
        viewModelScope.launch {
            while (_state.value.countdown > 0 && !_state.value.isUnlocked) {
                delay(1000)
                _state.value = _state.value.copy(countdown = _state.value.countdown - 1)
            }
            if (_state.value.countdown == 0) {
                _state.value = _state.value.copy(isUnlocked = true)
            }
        }
    }

    fun onEvent(event: AppDrawerEvent) {
        when (event) {
            is AppDrawerEvent.UpdateCountdown -> {
                _state.value = _state.value.copy(countdown = event.countdown)
            }

            is AppDrawerEvent.UnlockDrawer -> {
                _state.value = _state.value.copy(isUnlocked = true)
            }

            is AppDrawerEvent.SelectApp -> {
                if (event.app.pointCost > 0) {
                    _state.value = _state.value.copy(
                        selectedApp = event.app,
                        showWarning = true
                    )
                } else {
                    // Open app immediately for free apps
                    openApp(event.app)
                }
            }

            is AppDrawerEvent.ShowWarning -> {
                _state.value = _state.value.copy(showWarning = true)
            }

            is AppDrawerEvent.HideWarning -> {
                _state.value = _state.value.copy(
                    showWarning = false,
                    selectedApp = null
                )
            }

            is AppDrawerEvent.ConfirmAppOpen -> {
                _state.value.selectedApp?.let { app ->
                    openApp(app)
                    _state.value = _state.value.copy(
                        showWarning = false,
                        selectedApp = null,
                        currentPoints = _state.value.currentPoints - app.pointCost
                    )
                }
            }

            is AppDrawerEvent.CloseDrawer -> {
                // Handle drawer close - this would typically involve navigation
            }
        }
    }

    private fun openApp(app: AppDrawerApp) {
        // Simulate opening app
        println("Opening ${app.name}")
        // In a real app, this would launch the actual app
    }

    fun getCategorizedApps(): Map<AppCategory, List<AppDrawerApp>> {
        return _state.value.apps.groupBy { it.category }
    }
}