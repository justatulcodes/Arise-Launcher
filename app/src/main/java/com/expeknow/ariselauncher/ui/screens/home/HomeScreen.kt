package com.expeknow.ariselauncher.ui.screens.home

import android.content.ContentValues.TAG
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.expeknow.ariselauncher.data.model.DaysOfWeek
import com.expeknow.ariselauncher.data.model.TaskCategory
import com.expeknow.ariselauncher.service.AppUsageTimerService
import com.expeknow.ariselauncher.ui.components.TaskDialog
import com.expeknow.ariselauncher.ui.navigation.Screen
import com.expeknow.ariselauncher.ui.screens.apps.AppDrawerEvent
import com.expeknow.ariselauncher.ui.screens.apps.AppDrawerScreen
import com.expeknow.ariselauncher.ui.screens.apps.AppDrawerViewModel
import com.expeknow.ariselauncher.ui.screens.home.Utils.openLink
import android.content.Intent
import android.util.Log
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel,
    appDrawerViewModel: AppDrawerViewModel,
    state: HomeState
) {

    val theme = HomeTheme()
    val keyboardController = LocalSoftwareKeyboardController.current
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )
    var showAppDrawerBottomSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current

    BackHandler {
        // do nothing to disable back navigation
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Calculated values for the entire screen
        val allTasksCompleted = state.totalTasks > 0 && state.completedTasks == state.totalTasks
        val filteredTasks = if (state.hideCompletedTasks) {
            state.allTasks.filter { !it.isCompleted }
        } else {
            state.allTasks
        }
        val pointsEarned = state.allTasks.filter { it.isCompleted }.sumOf { it.points }

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

            // Progress Bar
            EnhancedProgressBar(
                completed = state.completedTasks,
                total = state.totalTasks,
                theme = theme
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Task Content - Scrollable
            Box(modifier = Modifier.weight(1f)) {
                if (allTasksCompleted) {
                    TasksCompletedCelebration(
                        completedCount = state.completedTasks,
                        pointsEarned = pointsEarned,
                        theme = theme
                    )
                    AddTaskButton(viewModel)
                } else {
                    when (state.mode) {
                        HomeMode.SIMPLE -> {
                            val simpleTasks = filteredTasks.filter { task ->
                                task.category in listOf(
                                    TaskCategory.PERSONAL,
                                )
                            }

                            Column {

                                Box {
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        items(simpleTasks) { task ->
                                            SimpleTaskItem(
                                                task = task,
                                                onTaskClick = {
                                                    navController.navigate(Screen.TaskDetails.routeFor(task.id))
                                                },
                                                onToggleTask = {
                                                    viewModel.onEvent(HomeEvent.ToggleTask(task))
                                                },
                                                theme = theme,
                                                onTaskLinkClick = { taskLink ->
                                                    openLink(context = context,
                                                        url = taskLink.url,
                                                        linkType = taskLink.type)
                                                }
                                            )
                                        }

                                        item {
                                            Spacer(modifier = Modifier.height(56.dp))
                                        }
                                    }
                                    AddTaskButton(viewModel)

                                }



                            }
                        }

                        HomeMode.FOCUSED -> {
                            FocusedTaskList(
                                categories = state.focusCategories,
                                tasks = state.recurringTasks,
                                onTaskClick = { taskId ->
                                    navController.navigate(Screen.TaskDetails.routeFor(taskId))
                                },
                                onToggleTask = { task ->
                                    viewModel.onEvent(HomeEvent.ToggleTask(task))
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

            EssentialAppsBar(
                appsList = state.apps,
                onAppClick = { appName ->
                    viewModel.onEvent(HomeEvent.LaunchApp(appName))
                },
                onOpenFullApps = {
                    appDrawerViewModel.onEvent(AppDrawerEvent.OpenDrawer)
                    showAppDrawerBottomSheet = true
                },
                theme = theme
            )

        }

        // Floating Action Button for Focused Mode
        if (state.mode == HomeMode.FOCUSED && !allTasksCompleted) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 100.dp)
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
                viewModel.onEvent(HomeEvent.HideEssentialAppsSheet)
                showAppDrawerBottomSheet = true
            },
            theme = theme
        )
    }

    // App Drawer Bottom Sheet
    if (showAppDrawerBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                keyboardController?.hide()
                showAppDrawerBottomSheet = false
                appDrawerViewModel.onEvent(AppDrawerEvent.CloseDrawer)
                               },
            sheetState = bottomSheetState,
            containerColor = Color.Black,
            contentColor = Color.White,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .width(32.dp)
                        .height(4.dp)
                        .background(
                            Color.White.copy(alpha = 0.3f),
                            androidx.compose.foundation.shape.RoundedCornerShape(2.dp)
                        )
                )
            },
            modifier = Modifier.statusBarsPadding(),
        ) {
            AppDrawerScreen(
                onClose = { showAppDrawerBottomSheet = false },
                viewModel = appDrawerViewModel
            )
        }
    }

    // Add Task Dialog
    if (state.showAddTaskDialog) {
        TaskDialog(
            onDismiss = {
                viewModel.onEvent(HomeEvent.HideAddTaskDialog)
            },
            onTaskAdded = { title: String, desc: String, pts: Int, category: TaskCategory, isRepeatable : Boolean, daysOfWeek : List<DaysOfWeek> ->
                viewModel.onEvent(HomeEvent.AddTask(title, desc, pts, category, isRepeatable, daysOfWeek))
            },
            showCategorySelector = state.mode == HomeMode.FOCUSED,
            initialCategory = if (state.mode == HomeMode.FOCUSED) TaskCategory.PEOPLE else TaskCategory.PERSONAL,
            availableCategories = if (state.mode == HomeMode.FOCUSED)
                listOf(TaskCategory.PEOPLE, TaskCategory.OPPORTUNITY, TaskCategory.SKILLS)
                else listOf(TaskCategory.PERSONAL)
        )
    }
}

@Composable
private fun BoxScope.AddTaskButton(viewModel: HomeViewModel) {
    OutlinedButton(
        onClick = { viewModel.onEvent(HomeEvent.ShowAddTaskDialog) },
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .padding(horizontal = 24.dp)
            .padding(bottom = 16.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color.White.copy(alpha = 0.8f),
            containerColor = Color.Black
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = SolidColor(
                Color.White.copy(alpha = 0.2f)
            )
        )
    ) {
        Icon(
            Icons.Filled.Add,
            contentDescription = "Add Task",
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("ADD TASK")
    }
}
