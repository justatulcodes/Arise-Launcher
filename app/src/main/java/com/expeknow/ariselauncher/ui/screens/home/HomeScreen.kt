package com.expeknow.ariselauncher.ui.screens.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.expeknow.ariselauncher.data.model.DaysOfWeek
import com.expeknow.ariselauncher.data.model.Task
import com.expeknow.ariselauncher.data.model.TaskCategory
import com.expeknow.ariselauncher.ui.components.TaskDialog
import com.expeknow.ariselauncher.ui.navigation.Screen
import com.expeknow.ariselauncher.ui.screens.apps.AppDrawerEvent
import com.expeknow.ariselauncher.ui.screens.apps.AppDrawerScreen
import com.expeknow.ariselauncher.ui.screens.apps.AppDrawerViewModel
import com.expeknow.ariselauncher.ui.screens.home.Utils.openLink

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

    // Determine page count based on mode
    val pageCount = if (state.mode == HomeMode.FOCUSED) 3 else 2
    val pagerState = rememberPagerState(
        initialPage = 1,
        pageCount = { pageCount }
    )
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        viewModel.onEvent(HomeEvent.UpdateCurrentPage(pagerState.currentPage))
    }

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

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                when (page) {
                    0 -> {
                        BlankScreen(theme = theme)
                    }
                    1 -> {
                        MainTaskContentScreen(
                            mode = state.mode,
                            allTasksCompleted = allTasksCompleted,
                            completedTasks = state.completedTasks,
                            pointsEarned = pointsEarned,
                            filteredTasks = filteredTasks,
                            recurringTasks = state.recurringTasks,
                            focusCategories = state.focusCategories,
                            editingCategoryId = state.editingCategoryId,
                            editingCategoryName = state.editingCategoryName,
                            navController = navController,
                            viewModel = viewModel,
                            context = context,
                            theme = theme
                        )
                    }
                    2 -> {
                        if (state.mode == HomeMode.FOCUSED) {
                            AlternateTasksScreen(
                                allTasks = state.allTasks,
                                hideCompletedTasks = state.hideCompletedTasks,
                                navController = navController,
                                viewModel = viewModel,
                                context = context,
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

        if (state.mode == HomeMode.FOCUSED && !allTasksCompleted && pagerState.currentPage == 1) {
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

@Composable
fun BlankScreen(theme: HomeTheme) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Swipe to navigate",
            color = Color.White.copy(alpha = 0.5f),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun MainTaskContentScreen(
    mode: HomeMode,
    allTasksCompleted: Boolean,
    completedTasks: Int,
    pointsEarned: Int,
    filteredTasks: List<Task>,
    recurringTasks: List<Task>,
    focusCategories: List<FocusCategory>,
    editingCategoryId: TaskCategory?,
    editingCategoryName: String,
    navController: NavController,
    viewModel: HomeViewModel,
    context: android.content.Context,
    theme: HomeTheme
) {
    // Task content implementation based on mode (SIMPLE or FOCUSED)
    if (allTasksCompleted) {
        Box {
            TasksCompletedCelebration(
                completedCount = completedTasks,
                pointsEarned = pointsEarned,
                theme = theme
            )
            AddTaskButton(viewModel)
        }
    } else {
        when (mode) {
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
                    categories = focusCategories,
                    tasks = recurringTasks,
                    onTaskClick = { taskId ->
                        navController.navigate(Screen.TaskDetails.routeFor(taskId))
                    },
                    onToggleTask = { task ->
                        viewModel.onEvent(HomeEvent.ToggleTask(task))
                    },
                    onEditCategory = { categoryId ->
                        viewModel.onEvent(HomeEvent.StartEditingCategory(categoryId))
                    },
                    editingCategoryId = editingCategoryId,
                    editingCategoryName = editingCategoryName,
                    onSaveEdit = {
                        viewModel.onEvent(HomeEvent.SaveEditingCategory(editingCategoryName))
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

@Composable
fun AlternateTasksScreen(
    allTasks: List<Task>,
    hideCompletedTasks: Boolean,
    navController: NavController,
    viewModel: HomeViewModel,
    context: android.content.Context,
    theme: HomeTheme
) {
    val simpleTasks = allTasks.filter { task ->
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
