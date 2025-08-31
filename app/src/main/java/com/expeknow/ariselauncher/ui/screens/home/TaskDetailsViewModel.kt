package com.expeknow.ariselauncher.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.expeknow.ariselauncher.data.model.*
import com.expeknow.ariselauncher.data.repository.TaskRepository

class TaskDetailsViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TaskDetailsState())
    val state: StateFlow<TaskDetailsState> = _state.asStateFlow()

    fun onEvent(event: TaskDetailsEvent) {
        when (event) {
            is TaskDetailsEvent.LoadTask -> {
                loadTask(event.taskId)
            }

            is TaskDetailsEvent.CompleteTask -> {
                viewModelScope.launch {
                    taskRepository.completeTask(event.taskId)
                    // Reload task to get updated state
                    loadTask(event.taskId)
                }
            }

            is TaskDetailsEvent.ToggleTask -> {
                _state.value.task?.let { currentTask ->
                    val updatedTask = currentTask.copy(isCompleted = !currentTask.isCompleted)
                    _state.value = _state.value.copy(
                        task = updatedTask,
                        showCompletionBanner = updatedTask.isCompleted
                    )

                    viewModelScope.launch {
                        if (updatedTask.isCompleted) {
                            taskRepository.completeTask(event.taskId)
                        }
                    }
                }
            }

            is TaskDetailsEvent.ExpandLink -> {
                _state.value = _state.value.copy(
                    expandedLinkId = if (_state.value.expandedLinkId == event.linkId) null else event.linkId
                )
            }

            is TaskDetailsEvent.OpenLink -> {
                // Handle opening link - this would typically involve launching a browser intent
                // For now, just log it
                println("Opening link: ${event.url}")
            }

            is TaskDetailsEvent.NavigateBack -> {
                // Handle navigation - this would typically involve a navigation callback
            }
        }
    }

    private fun loadTask(taskId: String) {
        _state.value = _state.value.copy(isLoading = true)

        // Mock task data with enhanced fields
        val mockTask = when (taskId) {
            "1" -> Task(
                id = taskId,
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
                        description = "A 20-minute strength training routine perfect for mornings. This video covers proper form for basic exercises including push-ups, squats, and planks."
                    ),
                    TaskLink(
                        url = "https://example.com/workout-plan",
                        title = "Complete Workout Plan PDF",
                        type = TaskLinkType.ARTICLE,
                        description = "Downloadable workout plan with step-by-step instructions and progress tracking sheets."
                    )
                )
            )

            "2" -> Task(
                id = taskId,
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
                        description = "Complete project proposal with timeline, budget breakdown, and team assignments for Q4 deliverables."
                    ),
                    TaskLink(
                        url = "https://example.com/budget-template",
                        title = "Budget Analysis Template",
                        type = TaskLinkType.LINK,
                        description = "Excel template for analyzing project costs and resource allocation."
                    )
                )
            )

            "3" -> Task(
                id = taskId,
                title = "Read 30 pages of cognitive science book",
                description = "Study advanced cognitive psychology concepts to enhance thinking patterns and mental models.",
                points = 35,
                category = TaskCategory.INTELLIGENCE,
                priority = 1,
                relatedLinks = listOf(
                    TaskLink(
                        url = "https://example.com/cognitive-science-chapter",
                        title = "Chapter 5: Mental Models",
                        type = TaskLinkType.ARTICLE,
                        description = "Essential reading on how mental models shape our understanding and decision-making processes."
                    )
                )
            )

            "4" -> Task(
                id = taskId,
                title = "Research investment opportunities",
                description = "Analyze potential investment opportunities in emerging markets and cryptocurrency.",
                points = 40,
                category = TaskCategory.WEALTH,
                priority = 1,
                relatedLinks = listOf(
                    TaskLink(
                        url = "https://youtube.com/investment-strategy",
                        title = "Investment Strategy for Beginners",
                        type = TaskLinkType.VIDEO,
                        description = "Comprehensive guide to building a diversified investment portfolio with focus on long-term growth."
                    ),
                    TaskLink(
                        url = "https://example.com/market-analysis",
                        title = "Current Market Analysis Report",
                        type = TaskLinkType.ARTICLE,
                        description = "Latest market trends and analysis for emerging markets and cryptocurrency sectors."
                    )
                )
            )

            else -> Task(
                id = taskId,
                title = "Sample Task",
                description = "This is a sample task for demonstration purposes.",
                points = 20,
                category = TaskCategory.PERSONAL,
                priority = 1
            )
        }

        _state.value = _state.value.copy(
            task = mockTask,
            isLoading = false,
            showCompletionBanner = mockTask.isCompleted
        )
    }
}