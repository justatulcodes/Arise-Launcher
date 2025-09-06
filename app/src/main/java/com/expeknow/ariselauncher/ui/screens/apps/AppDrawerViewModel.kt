package com.expeknow.ariselauncher.ui.screens.apps

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import com.expeknow.ariselauncher.data.repository.PointsLogRepositoryImpl
import com.expeknow.ariselauncher.data.repository.TaskRepositoryImpl
import com.expeknow.ariselauncher.data.repository.interfaces.AppRepository
import com.expeknow.ariselauncher.data.repository.interfaces.PointsLogRepository
import com.expeknow.ariselauncher.data.repository.interfaces.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppDrawerViewModel @Inject constructor(
    private val taskRepositoryImpl: TaskRepository,
    private val pointsLogRepositoryImpl: PointsLogRepository,
    private val appRepositoryImpl: AppRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AppDrawerState())
    val state: StateFlow<AppDrawerState> = _state.asStateFlow()

    init {
        loadApps()
        startCountdown()
        observePoints()
    }

    private fun loadApps() {
        val apps = appRepositoryImpl.getInstalledApps()

        val appDrawerApps = apps.map { app ->
            AppDrawerApp(
                name = app.name,
                packageName = app.packageName,
                icon = app.icon,
                id = app.packageName,
                category = AppCategory.Utility,
            )
        }
        _state.value = _state.value.copy(apps = appDrawerApps)

    }

    private fun observePoints() {
        viewModelScope.launch {
            pointsLogRepositoryImpl.getAvailablePoints().collect { points ->
                _state.value = _state.value.copy(currentPoints = points)
            }
        }
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
                // Check if user has enough points
                if (event.app.pointCost > 0 && _state.value.currentPoints < event.app.pointCost) {
                    return
                }

                // Deduct points if the app has a cost
                if (event.app.pointCost > 0) {
                    viewModelScope.launch {
                        pointsLogRepositoryImpl.spendPoints(
                            event.app.pointCost,
                            "NIL",
                            "Launched ${event.app.name}",

                        )
                    }
                }
                appRepositoryImpl.launchApp(event.app.packageName)
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
//                    openApp(app, event.context)
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

    fun getCategorizedApps(): Map<AppCategory, List<AppDrawerApp>> {
        return _state.value.apps.groupBy { it.category }
    }
}