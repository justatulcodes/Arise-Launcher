package com.expeknow.ariselauncher.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.expeknow.ariselauncher.data.model.TaskCategory
import com.expeknow.ariselauncher.data.repository.AppRepository
import com.expeknow.ariselauncher.data.repository.TaskRepository
import com.expeknow.ariselauncher.ui.components.TaskDialog
import com.expeknow.ariselauncher.ui.navigation.Screen
import com.expeknow.ariselauncher.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val appRepository = remember { AppRepository(context) }
    val taskRepository = remember { TaskRepository() }
    val viewModel: HomeViewModel = viewModel { HomeViewModel(appRepository, taskRepository) }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val theme = HomeTheme()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
    ) {
        // Calculated values for the entire screen
        val allTasksCompleted = state.totalTasks > 0 && state.completedTasks == state.totalTasks
        val filteredTasks = if (state.hideCompletedTasks) {
            state.tasks.filter { !it.isCompleted }
        } else {
            state.tasks
        }
        val pointsEarned = state.tasks.filter { it.isCompleted }.sumOf { it.points }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header with Points and Progress
            EnhancedPointsHeader(
                currentPoints = state.currentPoints,
                pointChange = state.pointChange,
                pointsTrend = state.pointsTrend,
                completed = state.completedTasks,
                total = state.totalTasks,
                onPointsClick = { /* Navigate to points page */ },
                theme = theme
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Mode Toggle
            ModeToggle(
                mode = state.mode,
                onModeToggle = { viewModel.onEvent(HomeEvent.ToggleMode) },
                theme = theme
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar
            EnhancedProgressBar(
                completed = state.completedTasks,
                total = state.totalTasks,
                theme = theme
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Task Content - Scrollable
            Box(modifier = Modifier.weight(1f)) {
                if (allTasksCompleted) {
                    TasksCompletedCelebration(
                        completedCount = state.completedTasks,
                        pointsEarned = pointsEarned,
                        theme = theme
                    )
                } else {
                    when (state.mode) {
                        HomeMode.SIMPLE -> {
                            val simpleTasks = filteredTasks.filter { task ->
                                task.category in listOf(
                                    TaskCategory.URGENT,
                                    TaskCategory.IMPORTANT,
                                    TaskCategory.PERSONAL,
                                    TaskCategory.WORK
                                )
                            }

                            Column {
                                SimpleTaskList(
                                    tasks = simpleTasks,
                                    onTaskClick = { taskId ->
                                        navController.navigate(Screen.TaskDetails.routeFor(taskId))
                                    },
                                    onToggleTask = { taskId ->
                                        viewModel.onEvent(HomeEvent.ToggleTask(taskId))
                                    },
                                    theme = theme
                                )

                                Spacer(modifier = Modifier.weight(1f))

                                // Add Task Button for Simple Mode
                                OutlinedButton(
                                    onClick = { viewModel.onEvent(HomeEvent.ShowAddTaskDialog) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp)
                                        .padding(bottom = 16.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color.White.copy(alpha = 0.6f)
                                    ),
                                    border = ButtonDefaults.outlinedButtonBorder.copy(
                                        brush = androidx.compose.ui.graphics.SolidColor(
                                            Color.White.copy(alpha = 0.2f)
                                        )
                                    )
                                ) {
                                    Icon(
                                        androidx.compose.material.icons.Icons.Filled.Add,
                                        contentDescription = "Add Task",
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("ADD TASK")
                                }
                            }
                        }

                        HomeMode.FOCUSED -> {
                            val focusedTasks = filteredTasks.filter { task ->
                                task.category in listOf(
                                    TaskCategory.INTELLIGENCE,
                                    TaskCategory.PHYSICAL,
                                    TaskCategory.WEALTH
                                )
                            }

                            FocusedTaskList(
                                categories = state.focusCategories,
                                tasks = focusedTasks,
                                onTaskClick = { taskId ->
                                    navController.navigate(Screen.TaskDetails.routeFor(taskId))
                                },
                                onToggleTask = { taskId ->
                                    viewModel.onEvent(HomeEvent.ToggleTask(taskId))
                                },
                                onEditCategory = { categoryId ->
                                    viewModel.onEvent(HomeEvent.StartEditingCategory(categoryId))
                                },
                                editingCategoryId = state.editingCategoryId,
                                editingCategoryName = state.editingCategoryName,
                                onSaveEdit = {
                                    viewModel.onEvent(HomeEvent.SaveEditingCategory(state.editingCategoryName))
                                },
                                onCancelEdit = {
                                    viewModel.onEvent(HomeEvent.CancelEditingCategory)
                                },
                                onEditingNameChange = { name ->
                                    viewModel.onEvent(HomeEvent.UpdateEditingCategoryName(name))
                                },
                                theme = theme
                            )
                        }
                    }
                }
            }

            // Motivational Quote
            Box(
                modifier = Modifier.padding(16.dp)
            ) {
                MotivationalQuote(theme = theme)
            }

            // Essential Apps Bar
            EssentialAppsBar(
                onAppClick = { appName ->
                    viewModel.onEvent(HomeEvent.LaunchApp(appName))
                },
                onOpenFullApps = {
                    viewModel.onEvent(HomeEvent.ShowEssentialAppsSheet)
                },
                theme = theme
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mode Status Banner
            Box(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (state.mode == HomeMode.SIMPLE) "TUNNEL VISION MODE ACTIVE" else "FOCUSED DEVELOPMENT MODE ACTIVE",
                        style = MaterialTheme.typography.labelSmall,
                        color = theme.accent.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        if (state.mode == HomeMode.SIMPLE) "Stay focused. Complete your goals." else "Transform yourself through disciplined action.",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Floating Action Button for Focused Mode
        if (state.mode == HomeMode.FOCUSED && !allTasksCompleted) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 140.dp)
            ) {
                FloatingAddButton(
                    onClick = { viewModel.onEvent(HomeEvent.ShowAddTaskDialog) },
                    theme = theme
                )
            }
        }
    }

    // Essential Apps Drawer
    if (state.showEssentialAppsSheet) {
        EssentialAppsDrawer(
            onClose = {
                viewModel.onEvent(HomeEvent.HideEssentialAppsSheet)
            },
            onOpenFullApps = {
                // Navigate to full app drawer
                viewModel.onEvent(HomeEvent.HideEssentialAppsSheet)
            },
            theme = theme
        )
    }

    // Add Task Dialog
    if (state.showAddTaskDialog) {
        TaskDialog(
            onDismiss = {
                viewModel.onEvent(HomeEvent.HideAddTaskDialog)
            },
            onTaskAdded = { title: String, desc: String, pts: Int ->
                val category =
                    if (state.mode == HomeMode.FOCUSED) TaskCategory.INTELLIGENCE else TaskCategory.PERSONAL
                viewModel.onEvent(HomeEvent.AddTask(title, desc, pts, category))
            }
        )
    }
}