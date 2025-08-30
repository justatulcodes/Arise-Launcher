package com.expeknow.ariselauncher.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.expeknow.ariselauncher.data.model.Task
import com.expeknow.ariselauncher.data.repository.TaskRepository
import com.expeknow.ariselauncher.ui.theme.*

@Composable
fun TaskDetailsScreen(
    navController: NavController,
    id: String
) {
    val taskRepository = remember { TaskRepository() }
    val viewModel: TaskDetailsViewModel = viewModel { TaskDetailsViewModel(taskRepository) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val theme = TaskDetailsTheme()

    // Load task when screen is first displayed
    LaunchedEffect(id) {
        viewModel.onEvent(TaskDetailsEvent.LoadTask(id))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
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
                        theme = theme
                    )

                    Spacer(modifier = Modifier.height(16.dp))

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
                        theme = theme
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Resources & Links Section
                    ResourcesLinksSection(
                        links = task.links,
                        expandedLinkId = state.expandedLinkId,
                        onExpandLink = { linkId ->
                            viewModel.onEvent(TaskDetailsEvent.ExpandLink(linkId))
                        },
                        onOpenLink = { url ->
                            viewModel.onEvent(TaskDetailsEvent.OpenLink(url))
                        },
                        theme = theme
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
}
