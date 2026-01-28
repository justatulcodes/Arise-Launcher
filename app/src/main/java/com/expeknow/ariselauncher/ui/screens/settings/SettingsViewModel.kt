package com.expeknow.ariselauncher.ui.screens.settings

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.expeknow.ariselauncher.data.datasource.AppInfoDataSource
import com.expeknow.ariselauncher.data.repository.interfaces.AppRepository
import com.expeknow.ariselauncher.data.repository.interfaces.PointsLogRepository
import com.expeknow.ariselauncher.data.repository.interfaces.SettingsRepository
import com.expeknow.ariselauncher.data.repository.interfaces.TaskLinkRepository
import com.expeknow.ariselauncher.data.repository.interfaces.TaskRepository
import com.expeknow.ariselauncher.service.AppUsageTimerService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val taskRepositoryImpl: TaskRepository,
    private val pointsLogRepositoryImpl: PointsLogRepository,
    private val taskLinkRepositoryImpl: TaskLinkRepository,
    private val appRepositoryImpl: AppRepository,
    private val settingsRepository: SettingsRepository,
    private val appInfoDataSource: AppInfoDataSource
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
            appLaunchPopupEnabled = settingsRepository.getAppLaunchPopupEnabled(),
            pointThreshold = settingsRepository.getPointThreshold(),
            warningsEnabled = settingsRepository.getWarningsEnabled(),
            showHomeScreen = settingsRepository.getShouldShowHomeScreen(),
            keyboardTriggerEnabled = settingsRepository.getShouldTriggerKeyboardInAppDrawer(),
            showWeeklyScheduleEnabled = settingsRepository.getShowEntireWeekSchedule(),
            showCategorizedAppsEnabled = settingsRepository.getShouldShowCategorizedApps(),
            appTimerEnabled = settingsRepository.getAppTimerEnabled()
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

            is SettingsEvent.ToggleAppLaunchPopup -> {
                _state.value = _state.value.copy(appLaunchPopupEnabled = event.enabled)
                settingsRepository.setAppLaunchPopupEnabled(event.enabled)
            }

            is SettingsEvent.UpdatePointThreshold -> {
                _state.value = _state.value.copy(pointThreshold = event.threshold)
                settingsRepository.setPointThreshold(event.threshold)
            }

            is SettingsEvent.ToggleWarnings -> {
                _state.value = _state.value.copy(warningsEnabled = event.enabled)
                settingsRepository.setWarningsEnabled(event.enabled)
            }

            is SettingsEvent.ToggleShowCategorizedApps -> {
                _state.value = _state.value.copy(showCategorizedAppsEnabled = event.enabled)
                settingsRepository.setShouldShowCategorizedApps(event.enabled)
            }
            is SettingsEvent.ToggleKeyboardTrigger -> {
                _state.value = _state.value.copy(keyboardTriggerEnabled = event.enabled)
                settingsRepository.setShouldTriggerKeyboardInAppDrawer(event.enabled)
            }

            is SettingsEvent.ToggleWeeklySchedule -> {
                _state.value = _state.value.copy(showWeeklyScheduleEnabled = event.enabled)
                settingsRepository.setShowEntireWeekSchedule(event.enabled)
            }

            is SettingsEvent.ToggleAppTimer -> {
                _state.value = _state.value.copy(appTimerEnabled = event.enabled)
                settingsRepository.setAppTimerEnabled(event.enabled)

                // Stop the service if the timer is being disabled
                if (!event.enabled) {
                    try {
                        val intent = Intent(context, AppUsageTimerService::class.java).apply {
                            action = AppUsageTimerService.ACTION_STOP_TRACKING
                        }
                        context.stopService(intent)
                    } catch (e: Exception) {
                        // Service might not be running, which is fine
                    }
                }
            }

            is SettingsEvent.SetDefaultLauncher -> {
                appRepositoryImpl.openDefaultLauncherSettings()
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

            is SettingsEvent.ToggleShowHomeScreen -> {
                _state.value = _state.value.copy(showHomeScreen = event.enabled)
                settingsRepository.setShouldShowHomeScreen(event.enabled)
            }

            is SettingsEvent.FactoryReset -> {
                viewModelScope.launch {
                    taskRepositoryImpl.deleteAllTasks()
                    pointsLogRepositoryImpl.resetAllPointsLog()
                    taskLinkRepositoryImpl.deleteAllTaskLinks()
                    settingsRepository.resetAllSettings()
                    loadSettings()
                }
                _state.value = _state.value.copy(
                    showFactoryResetDialog = false,
                )
            }

            SettingsEvent.HideAppRefreshDialog -> {
                _state.value = _state.value.copy(showAppRefreshDialog = false)
            }
            SettingsEvent.RefreshApps -> {
                CoroutineScope(Dispatchers.IO).launch {
                    appInfoDataSource.deleteAllAppInfo()
                    settingsRepository.setShouldRefreshAppDrawer(true)
                }
                _state.value = _state.value.copy(showAppRefreshDialog = false)
            }
            SettingsEvent.ShowAppRefreshDialog -> {
                _state.value = _state.value.copy(showAppRefreshDialog = true)
            }

        }
    }
}