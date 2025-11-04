package com.expeknow.ariselauncher.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.expeknow.ariselauncher.data.model.*
import com.expeknow.ariselauncher.data.repository.interfaces.AppRepository
import com.expeknow.ariselauncher.data.repository.interfaces.PointsLogRepository
import com.expeknow.ariselauncher.data.repository.interfaces.SettingsRepository
import com.expeknow.ariselauncher.data.repository.interfaces.TaskRepository
import com.expeknow.ariselauncher.ui.screens.home.Utils.getDayOfWeekFromTimeStampInMillis
import com.expeknow.ariselauncher.ui.screens.home.Utils.getTodayEndTime
import com.expeknow.ariselauncher.ui.screens.home.Utils.getTodayStartTime
import com.expeknow.ariselauncher.ui.screens.home.Utils.getTodaysDayOfWeek
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import java.util.Calendar

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val appRepositoryImpl: AppRepository,
    private val taskRepositoryImpl: TaskRepository,
    private val pointsLogRepositoryImpl: PointsLogRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()
    private var lastCheckedDate: String = getCurrentDate()


    init {
        updateModeFromSettings()
        loadInitialData()
        observePoints()
    }

    private fun updateModeFromSettings() {
        val tunnelVisionEnabled = settingsRepository.getTunnelVisionMode()
        val shouldHideCompletedTasks = settingsRepository.getHideCompletedTasks()
        val showWeeklySchedule = settingsRepository.getShowEntireWeekSchedule()
        val shouldShowCategorizedApps = settingsRepository.getShouldShowCategorizedApps()
        _state.value = _state.value.copy(
            mode = if (tunnelVisionEnabled) HomeMode.FOCUSED else HomeMode.SIMPLE,
            hideCompletedTasks = shouldHideCompletedTasks,
            showWeeklySchedule = showWeeklySchedule,
            showCategorizedApps = shouldShowCategorizedApps
        )

    }

    private fun loadInitialData() {
        viewModelScope.launch {
            loadApps()
            refreshTasks()
            loadPoints()
        }
    }
    private fun observePoints() {
        viewModelScope.launch {
            pointsLogRepositoryImpl.getAvailablePoints().collect { points ->
                updateState { it.copy(currentPoints = points) }
            }
        }
    }

    private fun loadPoints() {
        viewModelScope.launch {
            if(settingsRepository.getIsFreshDatabaseInstance()) {
                pointsLogRepositoryImpl.earnPoints(
                    1000,
                    "initial_points",
                    "initial_points",
                )
                settingsRepository.setIsFreshDatabaseInstance(false)
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
                    refreshTasks()
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
                        category = event.category,
                        isRepeated = event.isRepeated,
                        repeatDays = event.repeatDays
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
                appRepositoryImpl.launchApp(event.app.packageName)
            }

            is HomeEvent.UpdateCurrentPage -> {
                _state.value = _state.value.copy(currentPage = event.page)
            }
        }
    }

    fun refreshTasksToMatchCurrentDay() {
        val currentDate = getCurrentDate()
        if (currentDate != lastCheckedDate) {
            lastCheckedDate = currentDate
            refreshTasks()
        }
    }

    private fun loadApps() {
        viewModelScope.launch {
            val apps = appRepositoryImpl.getCallingAndMessagingApps().take(2)
            _state.value = _state.value.copy(apps = apps)
        }

    }

    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        return "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH)}-${calendar.get(Calendar.DAY_OF_MONTH)}"
    }
    private fun refreshTasks() {
        viewModelScope.launch {
            taskRepositoryImpl.getAllTasks().collect { tasks ->
                markCompletedRecurringTasksAsIncomplete(tasks)

                val filteredTasks = getNormalModeTasksCreatedTodayOrTasksNotYetCompleted(tasks)
                _state.value = _state.value.copy(normalTasks = filteredTasks)
                updateTaskStats(filteredTasks)
            }
        }
        viewModelScope.launch {
            taskRepositoryImpl.getAllTasks().collect { tasks ->
                markCompletedRecurringTasksAsIncomplete(tasks)

                val tasksForToday = getTasksRecurringToday(tasks)
                updateAllFocusedTasks(tasks)
                _state.value = _state.value.copy(todayFocusedTasks = tasksForToday)
            }
        }
    }

    private fun updateAllFocusedTasks(tasks: List<Task>) {
        val focusedTasks = tasks.filter { it.category in listOf(TaskCategory.PEOPLE, TaskCategory.OPPORTUNITY, TaskCategory.SKILLS) }
        _state.value = _state.value.copy(allFocusedTasks = focusedTasks)
    }

    private suspend fun markCompletedRecurringTasksAsIncomplete(tasks: List<Task>) {
        val currentDayOfWeek = getTodaysDayOfWeek()

        val tasksToReset = tasks.filter { task ->
            task.isRepeated &&
            task.isCompleted &&
            task.completedAt != null &&
            getDayOfWeekFromTimeStampInMillis(task.completedAt) != currentDayOfWeek
        }

        tasksToReset.forEach { task ->
            taskRepositoryImpl.updateTask(
                task.copy(isCompleted = false, completedAt = null)
            )
        }
    }

    private fun getTasksRecurringToday(tasks: List<Task>): List<Task> {
        val currentDayOfWeek = getTodaysDayOfWeek()
        val tasksRecurringToday = tasks.filter { task ->
            task.category in listOf(TaskCategory.PEOPLE, TaskCategory.OPPORTUNITY, TaskCategory.SKILLS)
                    && (task.repeatDays.contains(currentDayOfWeek)
                    || task.repeatDays.isEmpty())
        }
        return tasksRecurringToday
    }

    private fun getNormalModeTasksCreatedTodayOrTasksNotYetCompleted(tasks: List<Task>): List<Task> {
        val todayStart = getTodayStartTime()
        val todayEnd = getTodayEndTime()

        val filteredTasks = tasks.filter { task ->
            val taskTime = task.createdAt
            val isToday = taskTime in todayStart..todayEnd
            (isToday || !task.isCompleted) && task.category == TaskCategory.PERSONAL
        }
        return filteredTasks
    }


    private fun updateTaskStats(tasks: List<Task>) {
        val taskViewMode = _state.value.mode
        when(taskViewMode) {
            HomeMode.SIMPLE -> {
                val personalTasks = tasks.filter { it.category == TaskCategory.PERSONAL }
                val completedCount = personalTasks.count { it.isCompleted }
                val totalCount = personalTasks.filter {
                    !it.isRepeated || it.repeatDays.contains(getTodaysDayOfWeek())
                }.size
                val totalPoints = personalTasks.sumOf { it.points }
                _state.value = _state.value.copy(
                    normalCompletedTasks = completedCount,
                    normalTotalTasks = totalCount,
                    earnedPoints = totalPoints
                )

            }

            HomeMode.FOCUSED -> {
                val focusedTasks = tasks.filter { it.category in
                        listOf(TaskCategory.PEOPLE, TaskCategory.OPPORTUNITY, TaskCategory.SKILLS)}
                val completedCountFocusedTask = focusedTasks.count { it.isCompleted }
                val totalCountFocusedTask = focusedTasks.filter {
                    !it.isRepeated || it.repeatDays.contains(getTodaysDayOfWeek()) || it.repeatDays.isEmpty()
                }.size
                val totalPoints = focusedTasks.sumOf { it.points }
                _state.value = _state.value.copy(
                    focusedCompletedTasks = completedCountFocusedTask,
                    focusedTotalTasks = totalCountFocusedTask,
                    earnedPoints = totalPoints
                )

                val personalTasks = tasks.filter { it.category == TaskCategory.PERSONAL }
                val completedCountPersonalTasks = personalTasks.count { it.isCompleted }
                val totalCountPersonalTasks = personalTasks.filter {
                    !it.isRepeated || it.repeatDays.contains(getTodaysDayOfWeek())
                }.size
                _state.value = _state.value.copy(
                    normalCompletedTasks = completedCountPersonalTasks,
                    normalTotalTasks = totalCountPersonalTasks,
                )

            }
        }
    }
}