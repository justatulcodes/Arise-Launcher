package com.expeknow.ariselauncher.ui.screens.apps

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Velocity

@Composable
fun AppDrawerScreen(
    onClose: () -> Unit = {},
    viewModel: AppDrawerViewModel = viewModel(),
    shouldShowCategorizedApps: Boolean,
    onDragDelta: (Float) -> Unit = {},
    isVisible: Boolean = true,
    isFullyExpanded: Boolean = false,
    onDragEnd: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val topUsedApps by viewModel.topUsedApps.collectAsStateWithLifecycle()
    val theme = AppDrawerTheme()
    val listState = rememberLazyListState()
    var searchQuery by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val shouldTriggerKeyboard = viewModel.getShouldTriggerKeyboard()
    val focusManager = LocalFocusManager.current

    var shouldShowKeyboard by remember { mutableStateOf(false) }
    var isBottomSheetMoving by remember { mutableStateOf(false) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val isAtTop = listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
                val isDraggingDown = available.y > 0

                if (isAtTop && isDraggingDown) {
                    onDragDelta(available.y)
                    return Offset.Zero
                }
                return Offset.Zero
            }

            override suspend fun onPostFling(consumed : Velocity, available: Velocity): Velocity {
                onDragEnd()
                return super.onPreFling(available)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadTopUsedApps()
        viewModel.checkAndRefreshAppList()
    }

    LaunchedEffect(isVisible) {
        if (!isVisible) {
            searchQuery = ""
            listState.scrollToItem(0)
            focusManager.clearFocus(force = true)
            keyboardController?.hide()
            shouldShowKeyboard = false
        }
    }

    // Update keyboard trigger to only show when fully expanded
    LaunchedEffect(isFullyExpanded, state.isUnlocked, shouldTriggerKeyboard) {
        if (isFullyExpanded && state.isUnlocked && shouldTriggerKeyboard && !shouldShowKeyboard) {
            delay(200)
            focusRequester.requestFocus()
            keyboardController?.show()
            shouldShowKeyboard = true
        } else if (!isFullyExpanded && shouldShowKeyboard) {
            keyboardController?.hide()
            focusManager.clearFocus(force = true)
            shouldShowKeyboard = false
        }
    }

    LaunchedEffect(listState, shouldTriggerKeyboard) {
        snapshotFlow { Triple(listState.isScrollInProgress, listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) }
            .collect { (inProgress, index, offset) ->
                if (inProgress) {
                    // User scrolled down - hide keyboard and clear focus
                    if (shouldShowKeyboard && (index > 0 || offset > 50)) {
                        keyboardController?.hide()
                        focusManager.clearFocus(force = true)
                        shouldShowKeyboard = false
                    }
                }
            }
    }

    LaunchedEffect(isBottomSheetMoving) {
        if (isBottomSheetMoving && shouldShowKeyboard) {
            keyboardController?.hide()
            focusManager.clearFocus(force = true)
            shouldShowKeyboard = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AppDrawerSearchBar(
            searchQuery = searchQuery,
            onSearchQueryChange = { query ->
                searchQuery = query
            },
            theme = theme,
            focusRequester = focusRequester
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .nestedScroll(nestedScrollConnection)
                    .padding(top = 0.dp, start = 16.dp, bottom = 0.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                state = listState
            ) {
                item {
                    Spacer(Modifier.height(2.dp))
                }

                if (searchQuery.isNotEmpty()) {
                    val searchResults = viewModel.getSearchResults(searchQuery)
                    item {
                        SearchResultsSection(
                            searchQuery = searchQuery,
                            searchResults = searchResults,
                            onAppClick = { app: AppDrawerApp ->
                                viewModel.onEvent(AppDrawerEvent.SelectApp(app))
                                searchQuery = ""
                            },
                            theme = theme,
                            onUpdateAppStartTimer = { app, launchTimerValue ->
                                viewModel.onEvent(
                                    AppDrawerEvent.UpdateAppStartTimer(
                                        packageName = app.packageName,
                                        launchTimerValue = launchTimerValue
                                    )
                                )
                            }
                        )
                    }
                } else {
                    // Show top used apps row only when not searching and not in categorized view
                    if (!shouldShowCategorizedApps && topUsedApps.isNotEmpty()) {
                        item {
                            TopUsedAppsRow(
                                apps = topUsedApps,
                                onAppClick = { app: AppDrawerApp ->
                                    viewModel.onEvent(AppDrawerEvent.SelectApp(app))
                                    searchQuery = ""
                                    onClose()
                                },
                                theme = theme,
                                onUpdateAppStartTimer = { app, launchTimerValue ->
                                    viewModel.onEvent(
                                        AppDrawerEvent.UpdateAppStartTimer(
                                            packageName = app.packageName,
                                            launchTimerValue = launchTimerValue
                                        )
                                    )
                                }
                            )
                        }
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(56.dp)
                                        .height(2.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color.White)
                                )
                            }
                        }
                    }

                    if(shouldShowCategorizedApps){
                        val categorizedApps = viewModel.getCategorizedApps()
                        categorizedApps.forEach { (category, apps) ->
                            item {
                                AppCategorySection(
                                    category = category,
                                    apps = apps,
                                    onAppClick = { app: AppDrawerApp ->
                                        viewModel.onEvent(AppDrawerEvent.SelectApp(app))
                                        searchQuery = ""
                                    },
                                    theme = theme,
                                    onUpdateAppStartTimer = { app, launchTimerValue ->
                                        viewModel.onEvent(
                                            AppDrawerEvent.UpdateAppStartTimer(
                                                packageName = app.packageName,
                                                launchTimerValue = launchTimerValue
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }
                    else{
                        val allApps = viewModel.getAlphabeticallyArrangedApps()
                        item {
                            AppGrid(
                                apps = allApps,
                                onAppClick = { app: AppDrawerApp ->
                                    viewModel.onEvent(AppDrawerEvent.SelectApp(app))
                                    searchQuery = ""
                                    onClose()
                                },
                                theme = theme,
                                onUpdateAppStartTimer = { app, launchTimerValue ->
                                    viewModel.onEvent(
                                        AppDrawerEvent.UpdateAppStartTimer(
                                            packageName = app.packageName,
                                            launchTimerValue = launchTimerValue
                                        )
                                    )
                                }
                            )
                        }
                    }

                }
                item {
                    Spacer(Modifier.height(2.dp))
                }
            }
        }

        if (state.showTimerDialog) {
            AppTimerDialog(
                app = state.timerApp,
                timerCountdown = state.timerCountdown,
                onDismiss = {
                    viewModel.onEvent(AppDrawerEvent.DismissTimerDialog)
                },
                theme = theme
            )
        }
    }

}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFF000000)
private fun AppDrawerScreenDemoPreview() {
    val theme = AppDrawerTheme()
    var searchQuery by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val demoApps = remember { demoAppList() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AppDrawerSearchBar(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            theme = theme,
            focusRequester = focusRequester
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(start = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(Modifier.height(2.dp)) }

            item {
                TopUsedAppsRow(
                    apps = demoApps.take(6),
                    onAppClick = {},
                    theme = theme,
                    onUpdateAppStartTimer = { _, _ -> }
                )
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(56.dp)
                            .height(2.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                    )
                }
            }

            item {
                AppGrid(
                    apps = demoApps,
                    onAppClick = {},
                    theme = theme,
                    onUpdateAppStartTimer = { _, _ -> }
                )
            }

            item { Spacer(Modifier.height(2.dp)) }
        }
    }
}

private fun demoAppList(): List<AppDrawerApp> = listOf(
    AppDrawerApp(
        id = "com.android.phone",
        name = "Phone",
        packageName = "com.android.phone",
        category = AppCategory.COMMUNICATION,
        appInstallTime = 1L
    ),
    AppDrawerApp(
        id = "com.android.contacts",
        name = "Contacts",
        packageName = "com.android.contacts",
        category = AppCategory.COMMUNICATION,
        appInstallTime = 2L
    ),
    AppDrawerApp(
        id = "com.android.camera",
        name = "Camera",
        packageName = "com.android.camera",
        category = AppCategory.UTILITY,
        appInstallTime = 3L
    ),
    AppDrawerApp(
        id = "com.android.settings",
        name = "Settings",
        packageName = "com.android.settings",
        category = AppCategory.UTILITY,
        appInstallTime = 4L
    ),
    AppDrawerApp(
        id = "com.android.chrome",
        name = "Chrome",
        packageName = "com.android.chrome",
        category = AppCategory.PRODUCTIVITY,
        appInstallTime = 5L
    ),
    AppDrawerApp(
        id = "com.spotify.music",
        name = "Spotify",
        packageName = "com.spotify.music",
        category = AppCategory.STREAMING,
        appInstallTime = 6L
    ),
    AppDrawerApp(
        id = "com.whatsapp",
        name = "WhatsApp",
        packageName = "com.whatsapp",
        category = AppCategory.COMMUNICATION,
        appInstallTime = 7L
    ),
    AppDrawerApp(
        id = "com.google.android.youtube",
        name = "YouTube",
        packageName = "com.google.android.youtube",
        category = AppCategory.ENTERTAINMENT,
        appInstallTime = 8L
    ),
    AppDrawerApp(
        id = "com.google.android.gm",
        name = "Gmail",
        packageName = "com.google.android.gm",
        category = AppCategory.PRODUCTIVITY,
        appInstallTime = 9L
    ),
    AppDrawerApp(
        id = "com.google.android.apps.maps",
        name = "Maps",
        packageName = "com.google.android.apps.maps",
        category = AppCategory.UTILITY,
        appInstallTime = 10L
    )
)
