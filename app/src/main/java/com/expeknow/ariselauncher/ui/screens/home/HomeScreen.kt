package com.expeknow.ariselauncher.ui.screens.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.expeknow.ariselauncher.R
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

    val pageCount = if (state.mode == HomeMode.FOCUSED) 3 else 2
    val pagerState = rememberPagerState(
        initialPage = 1,
        pageCount = { pageCount }
    )
    val shouldShowTaskCategory = state.mode == HomeMode.FOCUSED && pagerState.currentPage == 1

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
        val allNormalTasksCompleted =
            state.normalTotalTasks > 0 && state.normalCompletedTasks == state.normalTotalTasks

        val combinedTasks = state.normalTasks + state.todayFocusedTasks
        val pointsEarned = combinedTasks.filter { it.isCompleted }.sumOf { it.points }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
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
                            allTasksCompleted = allNormalTasksCompleted,
                            completedTasks = state.normalCompletedTasks,
                            pointsEarned = pointsEarned,
                            normalTasks = state.normalTasks,
                            focusedTasks = state.todayFocusedTasks,
                            focusCategories = state.focusCategories,
                            editingCategoryId = state.editingCategoryId,
                            editingCategoryName = state.editingCategoryName,
                            navController = navController,
                            viewModel = viewModel,
                            context = context,
                            theme = theme,
                            currentPoints = state.currentPoints,
                            pointChange = state.pointChange,
                            pointsTrend = state.pointsTrend,
                            showWeeklySchedule = state.showWeeklySchedule,
                            allFocusedTasks = state.allFocusedTasks
                        )
                    }
                    2 -> {
                        if (state.mode == HomeMode.FOCUSED) {
                            AlternateTasksScreen(
                                allNormalTasks = state.normalTasks,
                                hideCompletedTasks = state.hideCompletedTasks,
                                navController = navController,
                                viewModel = viewModel,
                                context = context,
                                theme = theme,
                                currentPoints = state.currentPoints,
                                pointChange = state.pointChange,
                                pointsTrend = state.pointsTrend,
                                completedTasks = state.normalCompletedTasks,
                                totalTasks = state.normalTotalTasks
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

        if (state.mode == HomeMode.FOCUSED && pagerState.currentPage == 1) {
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
            showCategorySelector = shouldShowTaskCategory,
            initialCategory = if (shouldShowTaskCategory) TaskCategory.PEOPLE else TaskCategory.PERSONAL,
            availableCategories = if (shouldShowTaskCategory)
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
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.wallpaper_4),
            contentDescription = "Wallpaper",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun MainTaskContentScreen(
    mode: HomeMode,
    allTasksCompleted: Boolean,
    completedTasks: Int,
    pointsEarned: Int,
    normalTasks: List<Task>,
    focusedTasks: List<Task>,
    focusCategories: List<FocusCategory>,
    editingCategoryId: TaskCategory?,
    editingCategoryName: String,
    navController: NavController,
    viewModel: HomeViewModel,
    context: android.content.Context,
    theme: HomeTheme,
    currentPoints: Int,
    pointChange: Int,
    pointsTrend: PointsTrend,
    showWeeklySchedule: Boolean,
    allFocusedTasks: List<Task>
) {
    Column(modifier = Modifier.fillMaxSize()) {
        EnhancedPointsHeader(
            currentPoints = currentPoints,
            pointChange = pointChange,
            pointsTrend = pointsTrend,
            completed = completedTasks,
            total =
            if(mode == HomeMode.SIMPLE) normalTasks.size else focusedTasks.size,
            onPointsClick = { /* Navigate to points page */ },
            theme = theme
        )

        EnhancedProgressBar(
            completed = completedTasks,
            total = completedTasks + (normalTasks.size - completedTasks),
            theme = theme
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (mode) {
                HomeMode.SIMPLE -> {
                    val simpleTasks = normalTasks.filter { task ->
                        task.category in listOf(
                            TaskCategory.PERSONAL,
                        )
                    }

                    if (allTasksCompleted) {
                        Box {
                            TasksCompletedCelebration(
                                completedCount = completedTasks,
                                pointsEarned = pointsEarned,
                                theme = theme
                            )
                            AddTaskButton(viewModel)
                        }
                    }
                    else {
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
                    // Check if weekly schedule view is enabled
                    if (showWeeklySchedule) {
                        WeeklyScheduleTaskList(
                            allFocusedTasks = allFocusedTasks,
                            onTaskClick = { taskId ->
                                navController.navigate(Screen.TaskDetails.routeFor(taskId))
                            },
                            onToggleTask = { task ->
                                viewModel.onEvent(HomeEvent.ToggleTask(task))
                            },
                            theme = theme
                        )
                    } else {
                        FocusedTaskList(
                            categories = focusCategories,
                            tasks = focusedTasks,
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
}

@Composable
fun AlternateTasksScreen(
    allNormalTasks: List<Task>,
    hideCompletedTasks: Boolean,
    navController: NavController,
    viewModel: HomeViewModel,
    context: android.content.Context,
    theme: HomeTheme,
    currentPoints: Int,
    pointChange: Int,
    pointsTrend: PointsTrend,
    completedTasks: Int,
    totalTasks: Int
) {
    if(hideCompletedTasks) {
       allNormalTasks.filter { !it.isCompleted }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        EnhancedPointsHeader(
            currentPoints = currentPoints,
            pointChange = pointChange,
            pointsTrend = pointsTrend,
            completed = completedTasks,
            total = totalTasks,
            onPointsClick = { /* Navigate to points page */ },
            theme = theme
        )

        EnhancedProgressBar(
            completed = completedTasks,
            total = totalTasks,
            theme = theme
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(allNormalTasks) { task ->
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
