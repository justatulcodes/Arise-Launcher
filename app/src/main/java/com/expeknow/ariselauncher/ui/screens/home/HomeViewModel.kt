package com.expeknow.ariselauncher.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.expeknow.ariselauncher.data.repository.AppRepositoryImpl
import com.expeknow.ariselauncher.data.repository.TaskRepositoryImpl
import com.expeknow.ariselauncher.data.model.*
import com.expeknow.ariselauncher.data.repository.interfaces.AppRepository
import com.expeknow.ariselauncher.data.repository.interfaces.PointsLogRepository
import com.expeknow.ariselauncher.data.repository.interfaces.TaskRepository
import com.expeknow.ariselauncher.ui.screens.apps.AppCategory
import com.expeknow.ariselauncher.ui.screens.apps.AppDrawerApp
import com.expeknow.ariselauncher.ui.screens.home.Utils.getTodayEndTime
import com.expeknow.ariselauncher.ui.screens.home.Utils.getTodayStartTime
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val appRepositoryImpl: AppRepository,
    private val taskRepositoryImpl: TaskRepository,
    private val pointsLogRepositoryImpl: PointsLogRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadInitialData()
        observePoints()

    }

    private fun loadInitialData() {
        viewModelScope.launch {
            loadApps()
            observeTasks()
        }
    }
    fun getCategorizedApps(): Map<AppCategory, List<AppDrawerApp>> {
        return _state.value.apps.groupBy { it.category }
    }

    private fun observePoints() {
        viewModelScope.launch {
            pointsLogRepositoryImpl.getAvailablePoints().collect { points ->
                updateState { it.copy(currentPoints = points) }
            }
        }
    }

    private fun updateState(update: (HomeState) -> HomeState) {
        _state.value = update(_state.value)
    }


    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.LoadApps -> {
                viewModelScope.launch {
                    loadApps()
                }
            }

            is HomeEvent.LoadTasks -> {
                viewModelScope.launch {
                    observeTasks()
                }
            }

            is HomeEvent.CompleteTask -> {
                viewModelScope.launch {
                    taskRepositoryImpl.completeTask(event.taskId)
                }
            }

            is HomeEvent.ToggleTask -> {
                viewModelScope.launch {
                    val task = taskRepositoryImpl.getTaskById(event.task.id)
                    task?.let {
                        if (it.isCompleted) {
                            taskRepositoryImpl.uncompleteTask(event.task.id)
                        } else {
                            taskRepositoryImpl.completeTask(event.task.id)
                            pointsLogRepositoryImpl.insertPointsLogWithTask(event.task)
                        }
                    }
                }
            }

            is HomeEvent.AddTask -> {
                viewModelScope.launch {
                    taskRepositoryImpl.addTask(
                        title = event.title,
                        description = event.description,
                        points = event.points,
                        category = event.category
                    )
                    _state.value = _state.value.copy(showAddTaskDialog = false)
                }
            }

            is HomeEvent.ShowAddTaskDialog -> {
                _state.value = _state.value.copy(showAddTaskDialog = true)
            }

            is HomeEvent.HideAddTaskDialog -> {
                _state.value = _state.value.copy(showAddTaskDialog = false)
            }

            is HomeEvent.ShowEssentialAppsSheet -> {
                _state.value = _state.value.copy(showEssentialAppsSheet = true)
            }

            is HomeEvent.HideEssentialAppsSheet -> {
                _state.value = _state.value.copy(showEssentialAppsSheet = false)
            }

            is HomeEvent.ToggleMode -> {
                val newMode =
                    if (_state.value.mode == HomeMode.SIMPLE) HomeMode.FOCUSED else HomeMode.SIMPLE
                _state.value = _state.value.copy(mode = newMode)
            }

            is HomeEvent.ToggleHideCompletedTasks -> {
                _state.value = _state.value.copy(
                    hideCompletedTasks = !_state.value.hideCompletedTasks
                )
            }

            is HomeEvent.StartEditingCategory -> {
                val category = _state.value.focusCategories.find { it.id == event.categoryId }
                _state.value = _state.value.copy(
                    editingCategoryId = event.categoryId,
                    editingCategoryName = category?.name ?: ""
                )
            }

            is HomeEvent.SaveEditingCategory -> {
                val updatedCategories = _state.value.focusCategories.map { category ->
                    if (category.id == _state.value.editingCategoryId) {
                        category.copy(name = event.name)
                    } else category
                }
                _state.value = _state.value.copy(
                    focusCategories = updatedCategories,
                    editingCategoryId = null,
                    editingCategoryName = ""
                )
            }

            is HomeEvent.CancelEditingCategory -> {
                _state.value = _state.value.copy(
                    editingCategoryId = null,
                    editingCategoryName = ""
                )
            }

            is HomeEvent.UpdateEditingCategoryName -> {
                _state.value = _state.value.copy(
                    editingCategoryName = event.name
                )
            }

            is HomeEvent.ExpandLink -> {
                _state.value = _state.value.copy(
                    expandedLinkId = if (_state.value.expandedLinkId == event.linkId) null else event.linkId
                )
            }

            is HomeEvent.NavigateToTaskDetails -> {
                // Handle navigation - this would typically involve a navigation callback
            }

            is HomeEvent.LaunchApp -> {
                if (event.app.name == "Apps") {
                    _state.value = _state.value.copy(showEssentialAppsSheet = true)
                }else {
                    appRepositoryImpl.launchApp(event.app.packageName)
                }
            }
        }
    }

    private fun loadApps() {
        viewModelScope.launch {
            val apps = appRepositoryImpl.getInstalledApps().filter {
                it.name in listOf("Phone", "Messages") || it.name.contains("App")
            }.take(2)

            _state.value = _state.value.copy(apps = apps)
        }

    }
    private fun observeTasks() {
        viewModelScope.launch {
            taskRepositoryImpl.getAllTasks().collect { tasks ->
                val filteredTasks = filterTasksForStats(tasks)
                _state.value = _state.value.copy(tasks = tasks)
                updateTaskStats(filteredTasks)
            }
        }
    }

    private fun filterTasksForStats(tasks: List<Task>): List<Task> {
        val todayStart = getTodayStartTime()
        val todayEnd = getTodayEndTime()

        return tasks.filter { task ->
            val taskTime = task.createdAt
            val isToday = taskTime in todayStart..todayEnd

            // Include if: task is from today OR task is uncompleted (regardless of date)
            isToday || !task.isCompleted
        }
    }


    private fun updateTaskStats(tasks: List<Task>) {
        val completedCount = tasks.count { it.isCompleted }
        val totalCount = tasks.size
        val totalPoints = tasks.sumOf { it.points }

        _state.value = _state.value.copy(
            completedTasks = completedCount,
            totalTasks = totalCount,
            earnedPoints = totalPoints

        )
    }
}