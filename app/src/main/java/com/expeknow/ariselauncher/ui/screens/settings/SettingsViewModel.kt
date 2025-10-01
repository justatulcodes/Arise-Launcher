package com.expeknow.ariselauncher.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.expeknow.ariselauncher.data.repository.interfaces.AppRepository
import com.expeknow.ariselauncher.data.repository.interfaces.PointsLogRepository
import com.expeknow.ariselauncher.data.repository.interfaces.SettingsRepository
import com.expeknow.ariselauncher.data.repository.interfaces.TaskLinkRepository
import com.expeknow.ariselauncher.data.repository.interfaces.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val taskRepositoryImpl: TaskRepository,
    private val pointsLogRepositoryImpl: PointsLogRepository,
    private val taskLinkRepositoryImpl: TaskLinkRepository,
    private val appRepositoryImpl: AppRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        loadSettings()
        checkLauncherStatus()
    }

    private fun loadSettings() {
        _state.value = _state.value.copy(
            hideCompletedTasks = settingsRepository.getHideCompletedTasks(),
            tunnelVisionMode = settingsRepository.getTunnelVisionMode(),
            appDrawerDelay = settingsRepository.getAppDrawerDelay(),
            distractionAppsDelay = settingsRepository.getDistractionAppsDelay(),
            pointThreshold = settingsRepository.getPointThreshold(),
            warningsEnabled = settingsRepository.getWarningsEnabled()
        )
    }

    fun checkLauncherStatus() {
        viewModelScope.launch {
            val isDefault = appRepositoryImpl.isDefaultLauncher()
            _state.value = _state.value.copy(isDefaultLauncher = isDefault)
        }
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.ToggleCompletedTasks -> {
                _state.value = _state.value.copy(hideCompletedTasks = event.hide)
                settingsRepository.setHideCompletedTasks(event.hide)
            }

            is SettingsEvent.ToggleTunnelVision -> {
                _state.value = _state.value.copy(tunnelVisionMode = event.enabled)
                settingsRepository.setTunnelVisionMode(event.enabled)
            }

            is SettingsEvent.UpdateAppDrawerDelay -> {
                _state.value = _state.value.copy(appDrawerDelay = event.delay)
                settingsRepository.setAppDrawerDelay(event.delay)
            }

            is SettingsEvent.UpdateDistractionDelay -> {
                _state.value = _state.value.copy(distractionAppsDelay = event.delay)
                settingsRepository.setDistractionAppsDelay(event.delay)
            }

            is SettingsEvent.UpdatePointThreshold -> {
                _state.value = _state.value.copy(pointThreshold = event.threshold)
                settingsRepository.setPointThreshold(event.threshold)
            }

            is SettingsEvent.ToggleWarnings -> {
                _state.value = _state.value.copy(warningsEnabled = event.enabled)
                settingsRepository.setWarningsEnabled(event.enabled)
            }

            is SettingsEvent.SetDefaultLauncher -> {
                appRepositoryImpl.openDefaultLauncherSettings()
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

            is SettingsEvent.ShowResetPointsDialog -> {
                _state.value = _state.value.copy(showResetPointsDialog = true)
            }

            is SettingsEvent.HideResetPointsDialog -> {
                _state.value = _state.value.copy(showResetPointsDialog = false)
            }

            is SettingsEvent.ShowFactoryResetDialog -> {
                _state.value = _state.value.copy(showFactoryResetDialog = true)
            }

            is SettingsEvent.HideFactoryResetDialog -> {
                _state.value = _state.value.copy(showFactoryResetDialog = false)
            }

            is SettingsEvent.ResetAllPoints -> {
                viewModelScope.launch {
                    pointsLogRepositoryImpl.resetAllPointsLog()
                }
                _state.value = _state.value.copy(showResetPointsDialog = false)
            }

            is SettingsEvent.FactoryReset -> {
                viewModelScope.launch {
                    taskRepositoryImpl.deleteAllTasks()
                    pointsLogRepositoryImpl.resetAllPointsLog()
                    taskLinkRepositoryImpl.deleteAllTaskLinks()
                    settingsRepository.resetAllSettings()
                    loadSettings() // Reload default settings
                }
                _state.value = _state.value.copy(
                    showFactoryResetDialog = false,
                    apps = _state.value.apps // Keep app list as is
                )
            }
        }
    }
}