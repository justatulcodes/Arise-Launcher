package com.expeknow.ariselauncher.ui.screens.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.ui.tooling.preview.Preview
import com.expeknow.ariselauncher.data.model.Task
import com.expeknow.ariselauncher.data.model.TaskLink
import com.expeknow.ariselauncher.data.model.TaskLinkType
import com.expeknow.ariselauncher.ui.screens.home.Utils.openLink
import com.expeknow.ariselauncher.ui.screens.settings.ConfirmationDialog
import com.expeknow.ariselauncher.ui.screens.settings.SettingsTheme

@Composable
fun TaskDetailsScreen(
    navController: NavController,
    id: String,
    viewModel: TaskDetailsViewModel,
    state: TaskDetailsState
) {

    val theme = TaskDetailsTheme()
    val context = LocalContext.current

    // Load task when screen is first displayed
    LaunchedEffect(id) {
        viewModel.onEvent(TaskDetailsEvent.LoadTask(id))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (state.isLoading) {
            // Loading state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = theme.accent)
            }
        } else {
            state.task?.let { task ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header
                    TaskDetailsHeader(
                        onBackClick = {
                            viewModel.onEvent(TaskDetailsEvent.NavigateBack)
                            navController.popBackStack()
                        },
                        theme = theme,
                        onDeleteTask = {
                            viewModel.onEvent(TaskDetailsEvent.ShowDeleteConfirmation)
                        }
                    )


                    // Points Reward Section
                    PointsRewardSection(
                        points = task.points,
                        theme = theme
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Task Details Card
                    EnhancedTaskDetailsCard(
                        task = task,
                        onToggleTask = { taskId ->
                            viewModel.onEvent(TaskDetailsEvent.ToggleTask(taskId))
                        },
                        theme = theme,
                        isEditingTitle = state.isEditingTitle,
                        isEditingDescription = state.isEditingDescription,
                        editingTitleText = state.editingTitleText,
                        editingDescriptionText = state.editingDescriptionText,
                        onStartEditingTitle = {
                            viewModel.onEvent(TaskDetailsEvent.StartEditingTitle)
                        },
                        onStartEditingDescription = {
                            viewModel.onEvent(TaskDetailsEvent.StartEditingDescription)
                        },
                        onUpdateTitleText = { text ->
                            viewModel.onEvent(TaskDetailsEvent.UpdateTitleText(text))
                        },
                        onUpdateDescriptionText = { text ->
                            viewModel.onEvent(TaskDetailsEvent.UpdateDescriptionText(text))
                        },
                        onSaveTitle = {
                            viewModel.onEvent(TaskDetailsEvent.SaveTitle)
                        },
                        onSaveDescription = {
                            viewModel.onEvent(TaskDetailsEvent.SaveDescription)
                        },
                        onCancelEditing = {
                            viewModel.onEvent(TaskDetailsEvent.CancelEditing)
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Recurring Task Section
                    RecurringTaskSection(
                        task = task,
                        isEditingRecurrence = state.isEditingRecurrence,
                        editingIsRepeated = state.editingIsRepeated,
                        editingRepeatDays = state.editingRepeatDays,
                        onStartEditing = {
                            viewModel.onEvent(TaskDetailsEvent.StartEditingRecurrence)
                        },
                        onToggleIsRepeated = { isRepeated ->
                            viewModel.onEvent(TaskDetailsEvent.UpdateIsRepeated(isRepeated))
                        },
                        onToggleDay = { day ->
                            viewModel.onEvent(TaskDetailsEvent.ToggleRepeatDay(day))
                        },
                        onSave = {
                            viewModel.onEvent(TaskDetailsEvent.SaveRecurrence)
                        },
                        onCancel = {
                            viewModel.onEvent(TaskDetailsEvent.CancelEditing)
                        },
                        theme = theme
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Resources & Links Section
                    ResourcesLinksSection(
                        links = task.relatedLinks,
                        expandedLinkId = state.expandedLinkId,
                        onExpandLink = { linkId ->
                            viewModel.onEvent(TaskDetailsEvent.ExpandLink(linkId))
                        },
                        onOpenLink = { link ->
                            openLink(context, link.url, link.type)
                        },
                        theme = theme,
                        onAddLink = {
                            viewModel.onEvent(TaskDetailsEvent.StartAddingLink)
                        },
                        onRemoveLink = { linkId ->
                            viewModel.onEvent(TaskDetailsEvent.RemoveLink(linkId))
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Completion Status Banner
                    CompletionStatusBanner(
                        isCompleted = task.isCompleted,
                        onToggleComplete = {
                            viewModel.onEvent(TaskDetailsEvent.ToggleTask(task.id))
                        },
                        theme = theme
                    )

                    // Bottom padding
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
    
    // Add Link Dialog
    AddLinkDialog(
        isVisible = state.isAddingLink,
        title = state.newLinkTitle,
        url = state.newLinkUrl,
        description = state.newLinkDescription,
        selectedType = state.newLinkType,
        onTitleChange = { title ->
            viewModel.onEvent(TaskDetailsEvent.UpdateNewLinkTitle(title))
        },
        onUrlChange = { url ->
            viewModel.onEvent(TaskDetailsEvent.UpdateNewLinkUrl(url))
        },
        onDescriptionChange = { description ->
            viewModel.onEvent(TaskDetailsEvent.UpdateNewLinkDescription(description))
        },
        onTypeChange = { type ->
            viewModel.onEvent(TaskDetailsEvent.UpdateNewLinkType(type))
        },
        onSave = {
            viewModel.onEvent(TaskDetailsEvent.SaveNewLink)
        },
        onCancel = {
            viewModel.onEvent(TaskDetailsEvent.CancelEditing)
        },
        theme = theme
    )

    if (state.showDeleteConfirmation) {
        state.task?.let { task ->
            ConfirmationDialog(
                title = "DELETE TASK",
                message = "Are you sure you want to delete this task? This action cannot be undone.",
                confirmText = "DELETE",
                cancelText = "CANCEL",
                onConfirm = {
                    viewModel.onEvent(TaskDetailsEvent.DeleteTask(task.id))
                    navController.popBackStack()
                },
                onDismiss = {
                    viewModel.onEvent(TaskDetailsEvent.DismissDeleteConfirmation)
                },
                theme = SettingsTheme(),
                isDestructive = true
            )
        }
    }
}
