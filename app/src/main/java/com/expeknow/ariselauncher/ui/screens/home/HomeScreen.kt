package com.expeknow.ariselauncher.ui.screens.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.ExtraBold
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.expeknow.ariselauncher.R
import com.expeknow.ariselauncher.data.model.DaysOfWeek
import com.expeknow.ariselauncher.data.model.Task
import com.expeknow.ariselauncher.data.model.TaskCategory
import com.expeknow.ariselauncher.ui.components.QuoteDialog
import com.expeknow.ariselauncher.ui.components.TaskDialog
import com.expeknow.ariselauncher.ui.navigation.Screen
import com.expeknow.ariselauncher.ui.screens.apps.AppDrawerApp
import com.expeknow.ariselauncher.ui.screens.apps.AppDrawerEvent
import com.expeknow.ariselauncher.ui.screens.apps.AppDrawerScreen
import com.expeknow.ariselauncher.ui.screens.apps.AppDrawerViewModel
import com.expeknow.ariselauncher.ui.screens.home.Utils.openLink
import kotlinx.coroutines.launch

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
    var showAppDrawer by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var appDrawerOffsetY by remember { mutableFloatStateOf(Float.MAX_VALUE) }
    var isDraggingAppDrawer by remember { mutableStateOf(false) }
    var screenHeight by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(screenHeight) {
        if (screenHeight > 0f && appDrawerOffsetY == 0f) {
            appDrawerOffsetY = screenHeight * 0.3f
        }
    }

    // Animate the offset when not dragging
    val animatedOffsetY by animateFloatAsState(
        targetValue = appDrawerOffsetY,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "appDrawerOffset"
    )

    // Calculate progress: 0f = closed (at 70% from bottom), 1f = fully open (at top)
    val drawerProgress = if (screenHeight > 0f) {
        val closedOffset = screenHeight * 0.3f
        val openOffset = 0f
        1f - ((animatedOffsetY - openOffset) / (closedOffset - openOffset)).coerceIn(0f, 1f)
    } else 0f

    // Alpha based on progress
    val drawerAlpha = drawerProgress

    val pageCount = when {
        state.mode == HomeMode.FOCUSED && state.showHomeScreen -> 3  // blank, main, alternate
        state.mode == HomeMode.FOCUSED && !state.showHomeScreen -> 2 // main, alternate
        state.mode == HomeMode.SIMPLE && state.showHomeScreen -> 2   // blank, main
        else -> 1 // only main
    }

    val pagerState = rememberPagerState(
        initialPage = if (state.showHomeScreen) 1 else 0,
        pageCount = { pageCount }
    )
    val shouldShowTaskCategory = state.mode == HomeMode.FOCUSED && pagerState.currentPage == 1

    LaunchedEffect(Unit) {
        viewModel.refreshTasksToMatchCurrentDay()
        viewModel.refreshState()
    }

    LaunchedEffect(pagerState.currentPage) {
        viewModel.onEvent(HomeEvent.UpdateCurrentPage(pagerState.currentPage))
    }

    LaunchedEffect(showAppDrawer, screenHeight) {
        if (screenHeight > 0f) {
            if (!isDraggingAppDrawer) {
                appDrawerOffsetY = if (showAppDrawer) {
                    0f // Fully open
                } else {
                    screenHeight * 0.3f // Closed at 60% from bottom
                }
            }
        }
    }

    BackHandler {
        when {
            showAppDrawer -> {
                showAppDrawer = false
                appDrawerViewModel.onEvent(AppDrawerEvent.CloseDrawer)
            }
            pagerState.currentPage != 0 -> {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(0)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .onSizeChanged { size ->
                if (screenHeight == 0f) {
                    screenHeight = size.height.toFloat()
                }
            }
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
            )
            { page ->

                val actualPage = if (state.showHomeScreen) page else page + 1

                when (actualPage) {
                    0 -> {
                        BlankScreen(
                            theme = theme,
                            appsList = state.apps,
                            onAppClick = { appName ->
                                viewModel.onEvent(HomeEvent.LaunchApp(appName))
                            },
                            onOpenFullApps = {
                                appDrawerViewModel.onEvent(AppDrawerEvent.OpenDrawer)
                                showAppDrawer = true
                            },
                            quote = state.homeScreenQuote,
                            onLongPress = {
                                viewModel.onEvent(HomeEvent.ShowQuoteDialog)
                            }
                        )
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
                            allFocusedTasks = state.allFocusedTasks,
                            targets = state.targets
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

        }

        if (state.mode == HomeMode.FOCUSED && pagerState.currentPage == 1) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 24.dp)
            ) {
                FloatingAddButton(
                    onClick = { viewModel.onEvent(HomeEvent.ShowAddTaskDialog) },
                    theme = theme
                )
            }
        }

        Box(
            modifier = Modifier
                .graphicsLayer {
                    translationY = animatedOffsetY
                    alpha = drawerAlpha
                }
                .fillMaxSize()
                .zIndex(if (drawerProgress > 0.01f) 10f else -1f)
                .background(Color.Black)
                .pointerInput(screenHeight, showAppDrawer) {
                    detectVerticalDragGestures(
                        onDragStart = {
                            isDraggingAppDrawer = true
                        },
                        onVerticalDrag = { change, dragAmount ->
                            change.consume()
                            val newOffset = (appDrawerOffsetY + dragAmount).coerceIn(
                                0f,
                                screenHeight * 0.4f
                            )
                            appDrawerOffsetY = newOffset
                        },
                        onDragEnd = {
                            isDraggingAppDrawer = false
                            // Snap to open or closed based on position
                            val threshold = screenHeight * 0.2f // 20% threshold
                            if (appDrawerOffsetY < threshold) {
                                showAppDrawer = true
                                appDrawerOffsetY = 0f
                            } else {
                                showAppDrawer = false
                                appDrawerViewModel.onEvent(AppDrawerEvent.CloseDrawer)
                                keyboardController?.hide()
                                appDrawerOffsetY = screenHeight * 0.4f
                            }
                        }
                    )

                }
        ) {

            AppDrawerScreen(
                onClose = {
                    keyboardController?.hide()
                },
                viewModel = appDrawerViewModel,
                shouldShowCategorizedApps = state.showCategorizedApps,
                isVisible = showAppDrawer,
                isFullyExpanded = drawerProgress > 0.95f, // Only consider fully expanded when progress > 95%
                onDragDelta = { delta ->
                    val newOffset = (appDrawerOffsetY + delta).coerceIn(0f, screenHeight * 0.4f)
                    appDrawerOffsetY = newOffset
                },
                onDragEnd = {
                    val threshold = screenHeight * 0.2f
                    if (appDrawerOffsetY > threshold) {
                        showAppDrawer = false
                        appDrawerViewModel.onEvent(AppDrawerEvent.CloseDrawer)
                        keyboardController?.hide()
                        appDrawerOffsetY = screenHeight * 0.4f
                    } else {
                        appDrawerOffsetY = 0f
                    }
                }
            )

        }
    }

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

    if (state.showQuoteDialog) {
        QuoteDialog(
            currentQuote = state.homeScreenQuote,
            onDismiss = {
                viewModel.onEvent(HomeEvent.HideQuoteDialog)
            },
            onSave = { quote ->
                viewModel.onEvent(HomeEvent.SaveQuote(quote))
            },
            onClear = {
                viewModel.onEvent(HomeEvent.ClearQuote)
            }
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
fun BlankScreen(
    theme: HomeTheme,
    appsList: List<AppDrawerApp>,
    onAppClick: (AppDrawerApp) -> Unit,
    onOpenFullApps: () -> Unit,
    quote: String? = null,
    onLongPress: () -> Unit = {}
) {
    val currentTime by remember {
        mutableStateOf(java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()))
    }
    val currentDay by remember {
        mutableStateOf(java.text.SimpleDateFormat("EEEE", java.util.Locale.getDefault()))
    }
    val currentDate by remember {
        mutableStateOf(java.text.SimpleDateFormat("MMMM dd", java.util.Locale.getDefault()))
    }

    var time by remember { mutableStateOf(currentTime.format(java.util.Date())) }
    var day by remember { mutableStateOf(currentDay.format(java.util.Date())) }
    var date by remember { mutableStateOf(currentDate.format(java.util.Date())) }

    var totalDrag by remember { mutableStateOf(0f) }
    var hasTriggered by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        while (true) {
            time = currentTime.format(java.util.Date())
            day = currentDay.format(java.util.Date())
            date = currentDate.format(java.util.Date())
            kotlinx.coroutines.delay(1000)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        onLongPress()
                    }
                )
            }
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragStart = {
                        totalDrag = 0f
                        hasTriggered = false
                    },
                    onVerticalDrag = { change, dragAmount ->
                        change.consume()

                        totalDrag += dragAmount
                        if (totalDrag < -50f && !hasTriggered) {
                            onOpenFullApps()
                            hasTriggered = true
                        }
                        else if (totalDrag > 50f && !hasTriggered) {
                            hasTriggered = true
                            try {
                                val service = context.getSystemService("statusbar")
                                val statusBarClass = Class.forName("android.app.StatusBarManager")
                                val expandNotifications = statusBarClass.getMethod("expandNotificationsPanel")
                                expandNotifications.invoke(service)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    },
                    onDragEnd = {
                        totalDrag = 0f
                        hasTriggered = false
                    }
                )
            }
    ) {
        if (quote != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = quote,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(32.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            Image(
                painter = painterResource(id = R.drawable.wallpaper_4),
                contentDescription = "Wallpaper",
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Column(
            modifier = Modifier.fillMaxSize()

        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(top = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = time,
                    style = MaterialTheme.typography.displayLarge,
                    color = Color.White,
                    fontSize = 64.sp,
                    fontWeight = ExtraBold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = day,
                        style = MaterialTheme.typography.titleLarge,
                        color = getDayColor(day),
                        fontSize = 16.sp,
                        fontWeight = SemiBold
                    )
                    Text(
                        text = ", $date",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 16.sp
                    )
                }
            }

            EssentialAppsBar(
                appsList = appsList,
                onAppClick = onAppClick,
                onOpenFullApps = onOpenFullApps,
                theme = theme
            )
        }
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
    allFocusedTasks: List<Task>,
    targets: List<com.expeknow.ariselauncher.data.datasource.Target>
) {

    val currentDay by remember {
        mutableStateOf(java.text.SimpleDateFormat("EEEE", java.util.Locale.getDefault()))
    }
    var day by remember { mutableStateOf(currentDay.format(java.util.Date())) }

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

        Spacer(modifier = Modifier.height(8.dp))

        // Display targets if any exist
        if (targets.isNotEmpty()) {
            CompactTargetsList(
                targets = targets,
                onTargetClick = { _ ->
                    navController.navigate(Screen.Targets.route)
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (mode == HomeMode.FOCUSED && !showWeeklySchedule) {
            CompactDayOfWeekIndicator(
                currentDay = day,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

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
