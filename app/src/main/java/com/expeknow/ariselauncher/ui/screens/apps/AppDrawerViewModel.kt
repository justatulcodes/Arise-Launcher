package com.expeknow.ariselauncher.ui.screens.apps

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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
        observePoints()
        startCountdown()
    }

    private fun loadApps() {
        viewModelScope.launch {
            val apps = appRepositoryImpl.getInstalledApps()
            _state.value = _state.value.copy(apps = apps)
        }
    }

    private fun observePoints() {
        viewModelScope.launch {
            pointsLogRepositoryImpl.getAvailablePoints().collect { points ->
                _state.value = _state.value.copy(currentPoints = points)
                if(points > 0) {
                    _state.value = _state.value.copy(isUnlocked = true)
                }else {
                    _state.value = _state.value.copy(isUnlocked = false)
                }
            }
        }
    }
    fun startCountdown() {
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
                if (event.app.pointCost > 0 && _state.value.currentPoints < event.app.pointCost) {
                    appRepositoryImpl.launchApp(event.app.packageName)
                    return
                }

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
//                _state.value = _state.value.copy(
//                    isUnlocked = false,
//                    countdown = 10
//                )
            }

            is AppDrawerEvent.SearchApps -> {
                _state.value = _state.value.copy(searchQuery = event.query)
            }
        }
    }

    fun getCategorizedApps(): Map<AppCategory, List<AppDrawerApp>> {
        val query = _state.value.searchQuery
        val filteredApps = if (query.isBlank()) {
            _state.value.apps
        } else {
            _state.value.apps.filter { app ->
                app.name.contains(query, ignoreCase = true) ||
                        app.category.displayName.contains(query, ignoreCase = true)
            }
        }
        return filteredApps.groupBy { it.category }
    }

    fun getSearchResults(): List<AppDrawerApp> {
        val query = _state.value.searchQuery
        return if (query.isBlank()) {
            emptyList()
        } else {
            _state.value.apps.filter { app ->
                app.name.contains(query, ignoreCase = true) ||
                        app.category.displayName.contains(query, ignoreCase = true)
            }
        }
    }
}