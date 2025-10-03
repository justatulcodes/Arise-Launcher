package com.expeknow.ariselauncher.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.expeknow.ariselauncher.data.model.*
import com.expeknow.ariselauncher.data.repository.TaskRepositoryImpl
import com.expeknow.ariselauncher.data.repository.interfaces.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class TaskDetailsViewModel @Inject constructor(
    private val taskRepositoryImpl: TaskRepository
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
                    taskRepositoryImpl.completeTask(event.taskId)
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
                            taskRepositoryImpl.completeTask(event.taskId)
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

            // Editing events
            is TaskDetailsEvent.StartEditingTitle -> {
                _state.value.task?.let { task ->
                    _state.value = _state.value.copy(
                        isEditingTitle = true,
                        editingTitleText = task.title
                    )
                }
            }

            is TaskDetailsEvent.StartEditingDescription -> {
                _state.value.task?.let { task ->
                    _state.value = _state.value.copy(
                        isEditingDescription = true,
                        editingDescriptionText = task.description
                    )
                }
            }

            is TaskDetailsEvent.StartAddingLink -> {
                _state.value = _state.value.copy(
                    isAddingLink = true,
                    newLinkTitle = "",
                    newLinkUrl = "",
                    newLinkDescription = "",
                    newLinkType = TaskLinkType.LINK
                )
            }

            is TaskDetailsEvent.CancelEditing -> {
                _state.value = _state.value.copy(
                    isEditingTitle = false,
                    isEditingDescription = false,
                    isAddingLink = false,
                    editingTitleText = "",
                    editingDescriptionText = "",
                    newLinkTitle = "",
                    newLinkUrl = "",
                    newLinkDescription = ""
                )
            }

            is TaskDetailsEvent.UpdateTitleText -> {
                _state.value = _state.value.copy(editingTitleText = event.text)
            }

            is TaskDetailsEvent.UpdateDescriptionText -> {
                _state.value = _state.value.copy(editingDescriptionText = event.text)
            }

            is TaskDetailsEvent.SaveTitle -> {
                _state.value.task?.let { task ->
                    val updatedTask = task.copy(title = _state.value.editingTitleText)
                    _state.value = _state.value.copy(
                        task = updatedTask,
                        isEditingTitle = false,
                        editingTitleText = ""
                    )

                    viewModelScope.launch {
                        taskRepositoryImpl.updateTask(updatedTask)
                    }
                }
            }

            is TaskDetailsEvent.SaveDescription -> {
                _state.value.task?.let { task ->
                    val updatedTask = task.copy(description = _state.value.editingDescriptionText)
                    _state.value = _state.value.copy(
                        task = updatedTask,
                        isEditingDescription = false,
                        editingDescriptionText = ""
                    )

                    viewModelScope.launch {
                        taskRepositoryImpl.updateTask(updatedTask)
                    }
                }
            }

            // Link management events
            is TaskDetailsEvent.UpdateNewLinkTitle -> {
                _state.value = _state.value.copy(newLinkTitle = event.title)
            }

            is TaskDetailsEvent.UpdateNewLinkUrl -> {
                _state.value = _state.value.copy(newLinkUrl = event.url)
            }

            is TaskDetailsEvent.UpdateNewLinkDescription -> {
                _state.value = _state.value.copy(newLinkDescription = event.description)
            }

            is TaskDetailsEvent.UpdateNewLinkType -> {
                _state.value = _state.value.copy(newLinkType = event.type)
            }

            is TaskDetailsEvent.SaveNewLink -> {
                _state.value.task?.let { task ->
                    if (_state.value.newLinkTitle.isNotBlank() && _state.value.newLinkUrl.isNotBlank()) {
                        val newLink = TaskLink(
                            title = _state.value.newLinkTitle,
                            url = _state.value.newLinkUrl,
                            type = _state.value.newLinkType,
                            description = _state.value.newLinkDescription.takeIf { it.isNotBlank() }
                        )

                        val updatedTask = task.copy(
                            relatedLinks = task.relatedLinks + newLink
                        )

                        _state.value = _state.value.copy(
                            task = updatedTask,
                            isAddingLink = false,
                            newLinkTitle = "",
                            newLinkUrl = "",
                            newLinkDescription = ""
                        )

                        viewModelScope.launch {
                            taskRepositoryImpl.updateTask(updatedTask)
                        }
                    }
                }
            }

            is TaskDetailsEvent.RemoveLink -> {
                _state.value.task?.let { task ->
                    val updatedTask = task.copy(
                        relatedLinks = task.relatedLinks.filter { it.id != event.linkId }
                    )
                    _state.value = _state.value.copy(task = updatedTask)

                    viewModelScope.launch {
                        taskRepositoryImpl.updateTask(updatedTask)
                    }
                }
            }
        }
    }

    private fun loadTask(taskId: String) {
        _state.value = _state.value.copy(isLoading = true)

        viewModelScope.launch {
            val task = taskRepositoryImpl.getTaskById(taskId)
            if (task != null) {
                _state.value = _state.value.copy(
                    task = task,
                    isLoading = false,
                    showCompletionBanner = task.isCompleted
                )
            } else {
                // Handle task not found - for simplicity, just set loading to false
                _state.value = _state.value.copy(isLoading = false)
            }
        }


    }
}