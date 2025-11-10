package com.expeknow.ariselauncher.ui.screens.apps

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.expeknow.ariselauncher.data.repository.interfaces.AppRepository
import com.expeknow.ariselauncher.data.repository.interfaces.PointsLogRepository
import com.expeknow.ariselauncher.data.repository.interfaces.SettingsRepository
import com.expeknow.ariselauncher.data.repository.interfaces.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class AppDrawerViewModel @Inject constructor(
    private val taskRepositoryImpl: TaskRepository,
    private val pointsLogRepositoryImpl: PointsLogRepository,
    private val appRepositoryImpl: AppRepository,
    private val settingsRepository: SettingsRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(AppDrawerState())
    val state: StateFlow<AppDrawerState> = _state.asStateFlow()

    private var countdownJob: Job? = null
    private var timerJob: Job? = null

    init {
        resetAppDrawerTimeoutTime()
        loadApps()
        observePoints()
    }

    private fun resetAppDrawerTimeoutTime() {
        viewModelScope.launch {
            _state.value = _state.value.copy(countdown = settingsRepository.getAppDrawerDelay().toInt())
        }
    }

    fun getAppDrawerDelay(): Float {
        return settingsRepository.getAppDrawerDelay()
    }

    fun getShouldTriggerKeyboard(): Boolean {
        return settingsRepository.getShouldTriggerKeyboardInAppDrawer()
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
                    //TODO should we have app drawer delay timer (at all or) controlled by user?
                    if(settingsRepository.getAppDrawerDelay() > 0){
                        _state.value = _state.value.copy(isUnlocked = false)
                    }
                }
            }
        }
    }

    fun startCountdown() {
        countdownJob?.cancel()

        countdownJob = viewModelScope.launch {
            while (_state.value.countdown > 0 && !_state.value.isUnlocked) {
                delay(1000)
                _state.value = _state.value.copy(countdown = _state.value.countdown - 1)
            }
            if (_state.value.countdown == 0) {
                _state.value = _state.value.copy(isUnlocked = true)
            }
        }
    }

    private fun stopCountdown() {
        countdownJob?.cancel()
        countdownJob = null
    }

    fun startTimer() {
        timerJob?.cancel()

        timerJob = viewModelScope.launch {
            while (_state.value.timerCountdown > 0) {
                delay(1000)
                _state.value = _state.value.copy(timerCountdown = _state.value.timerCountdown - 1)
            }
            if (_state.value.timerCountdown == 0) {
                _state.value.timerApp?.let { app ->
                    appRepositoryImpl.launchApp(app.packageName)
                    _state.value = _state.value.copy(
                        showTimerDialog = false,
                        timerCountdown = 0,
                        timerApp = null
                    )
                }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
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
                    _state.value = _state.value.copy(
                        showTimerDialog = true,
                        timerCountdown = event.app.pointCost,
                        timerApp = event.app
                    )
                    startTimer()
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
                    _state.value = _state.value.copy(
                        showWarning = false,
                        selectedApp = null,
                        currentPoints = _state.value.currentPoints - app.pointCost
                    )
                }
            }

            is AppDrawerEvent.CloseDrawer -> {
                stopCountdown()
                resetAppDrawerTimeoutTime()
            }

            is AppDrawerEvent.SearchApps -> {

            }

            AppDrawerEvent.OpenDrawer -> {
                startCountdown()
            }

            is AppDrawerEvent.DismissTimerDialog -> {
                timerJob?.cancel()
                timerJob = null
                _state.value = _state.value.copy(
                    showTimerDialog = false,
                    timerCountdown = 0,
                    timerApp = null
                )
            }
        }
    }

    fun getCategorizedApps(): Map<AppCategory, List<AppDrawerApp>> {
        return _state.value.apps
            .groupBy { it.category }
            .toSortedMap(compareBy { it.priority })
    }

    fun getAlphabeticallyArrangedApps() : List<AppDrawerApp> {
        return _state.value.apps
            .sortedBy { it.name }
    }

    fun getSearchResults(searchQuery: String): List<AppDrawerApp> {
        val query = searchQuery
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