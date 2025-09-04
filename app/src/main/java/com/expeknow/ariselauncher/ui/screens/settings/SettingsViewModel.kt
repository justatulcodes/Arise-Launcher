package com.expeknow.ariselauncher.ui.screens.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.ToggleCompletedTasks -> {
                _state.value = _state.value.copy(hideCompletedTasks = event.hide)
            }

            is SettingsEvent.ToggleTunnelVision -> {
                _state.value = _state.value.copy(tunnelVisionMode = event.enabled)
            }

            is SettingsEvent.UpdateAppDrawerDelay -> {
                _state.value = _state.value.copy(appDrawerDelay = event.delay)
            }

            is SettingsEvent.UpdateDistractionDelay -> {
                _state.value = _state.value.copy(distractionAppsDelay = event.delay)
            }

            is SettingsEvent.UpdatePointThreshold -> {
                _state.value = _state.value.copy(pointThreshold = event.threshold)
            }

            is SettingsEvent.ToggleWarnings -> {
                _state.value = _state.value.copy(warningsEnabled = event.enabled)
            }

            is SettingsEvent.ToggleAppEssential -> {
                val updatedApps = _state.value.apps.map { app ->
                    if (app.id == event.appId) {
                        app.copy(essential = !app.essential)
                    } else {
                        app
                    }
                }
                _state.value = _state.value.copy(apps = updatedApps)
            }

            is SettingsEvent.ResetAllPoints -> {
                // Handle reset points logic
                // This would typically involve calling a repository method
            }

            is SettingsEvent.FactoryReset -> {
                // Handle factory reset logic
                _state.value = SettingsState()
            }
        }
    }
}