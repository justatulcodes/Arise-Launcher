package com.expeknow.ariselauncher.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.expeknow.ariselauncher.data.repository.AppRepository
import com.expeknow.ariselauncher.data.repository.TaskRepository
import com.expeknow.ariselauncher.data.model.*
import kotlinx.coroutines.CoroutineScope

class HomeViewModel(
    private val appRepository: AppRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            loadApps()
            observeTasks()
            getCurrentPoints()
//            loadMockTasks() // Add some mock tasks for demonstration
        }
    }

    private suspend fun getCurrentPoints() {
        taskRepository.getTotalPointsEarned().collect { points ->
            if(points != null) {
                _state.value = _state.value.copy(currentPoints = points)
            }
        }
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
                    taskRepository.completeTask(event.taskId)
                }
            }

            is HomeEvent.ToggleTask -> {
                viewModelScope.launch {
                    val task = taskRepository.getTaskById(event.taskId)
                    task?.let {
                        if (it.isCompleted) {
                            taskRepository.uncompleteTask(event.taskId)
                        } else {
                            taskRepository.completeTask(event.taskId)
                        }
                    }
                }
            }

            is HomeEvent.AddTask -> {
                viewModelScope.launch {
                    taskRepository.addTask(
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
                // Handle app launch - this would typically involve launching an intent
                if (event.appName == "Apps") {
                    _state.value = _state.value.copy(showEssentialAppsSheet = true)
                }
            }
        }
    }

    private suspend fun loadApps() {
        val apps = appRepository.getInstalledApps().filter {
            it.name in listOf("Phone", "Messages") || it.name.contains("App")
        }.take(3)
        _state.value = _state.value.copy(apps = apps)
    }

    private fun observeTasks() {
        viewModelScope.launch {
            taskRepository.getActiveTasks().collect { tasks ->
                _state.value = _state.value.copy(tasks = tasks)
                updateTaskStats(tasks)
            }
        }
    }

    private fun loadMockTasks() {
        // Add some mock tasks for demonstration
        viewModelScope.launch {
            val mockTasks = listOf(
                Task(
                    title = "Complete morning workout",
                    description = "A comprehensive 45-minute workout routine focusing on strength training and cardio to boost energy and productivity for the day.",
                    points = 25,
                    category = TaskCategory.PHYSICAL,
                    priority = 1,
                    relatedLinks = listOf(
                        TaskLink(
                            url = "https://youtube.com/watch?v=workout",
                            title = "Morning Strength Routine",
                            type = TaskLinkType.VIDEO,
                            thumbnail = "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=200&h=120&fit=crop",
                            description = "A 20-minute strength training routine perfect for mornings"
                        )
                    )
                ),
                Task(
                    title = "Review project proposal",
                    description = "Thoroughly review the Q4 project proposal, analyze budget requirements, timeline feasibility, and resource allocation.",
                    points = 40,
                    category = TaskCategory.WORK,
                    priority = 2,
                    relatedLinks = listOf(
                        TaskLink(
                            url = "https://docs.google.com/proposal",
                            title = "Q4 Project Proposal Draft",
                            type = TaskLinkType.ARTICLE,
                            description = "Complete project proposal with timeline and budget"
                        )
                    )
                ),
                Task(
                    title = "Read 30 pages of cognitive science book",
                    description = "Study advanced cognitive psychology concepts to enhance thinking patterns and mental models.",
                    points = 35,
                    category = TaskCategory.INTELLIGENCE,
                    priority = 1
                ),
                Task(
                    title = "Research investment opportunities",
                    description = "Analyze potential investment opportunities in emerging markets and cryptocurrency.",
                    points = 40,
                    category = TaskCategory.WEALTH,
                    priority = 1
                ),
                Task(
                    title = "Complete daily meditation",
                    description = "20-minute mindfulness meditation session to improve focus and reduce stress.",
                    points = 15,
                    category = TaskCategory.PERSONAL,
                    priority = 1,
                    isCompleted = true
                )
            )

            // Insert mock tasks into the database
            mockTasks.forEach { task ->
                taskRepository.insertTask(task)
            }
        }
    }

    private fun updateTaskStats(tasks: List<Task>) {
        val completedCount = tasks.count { it.isCompleted }
        val totalCount = tasks.size
        val pointsEarned = tasks.filter { it.isCompleted }.sumOf { it.points }

        _state.value = _state.value.copy(
            completedTasks = completedCount,
            totalTasks = totalCount,
            currentPoints = pointsEarned
        )
    }
}