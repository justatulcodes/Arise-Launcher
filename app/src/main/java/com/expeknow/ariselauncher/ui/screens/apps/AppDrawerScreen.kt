package com.expeknow.ariselauncher.ui.screens.apps

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.unit.Velocity

@Composable
fun AppDrawerScreen(
    onClose: () -> Unit = {},
    viewModel: AppDrawerViewModel = viewModel(),
    shouldShowCategorizedApps: Boolean,
    onDragDelta: (Float) -> Unit = {},
    isVisible: Boolean = true,
    onDragEnd: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val theme = AppDrawerTheme()
    val listState = rememberLazyListState()
    var searchQuery by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val shouldTriggerKeyboard = viewModel.getShouldTriggerKeyboard()
    val focusManager = LocalFocusManager.current

    var shouldShowKeyboard by remember { mutableStateOf(true) }
    var isBottomSheetMoving by remember { mutableStateOf(false) }

//    val nestedScrollConnection = remember {
//        object : NestedScrollConnection {
//            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
//                // Check if we're at the top and trying to scroll up (negative delta)
//                val isAtTop = listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
//                val isDraggingUp = available.y > 0 // Positive means dragging down (scrolling up)
//
//                if (isAtTop && isDraggingUp) {
//                    // Pass the scroll to the parent (app drawer drag handler)
//                    onDragDelta(available.y)
//                    // Hide keyboard when starting to drag
//                    if (shouldShowKeyboard) {
//                        keyboardController?.hide()
//                        focusManager.clearFocus(force = true)
//                        shouldShowKeyboard = false
//                    }
////                    return available // Consume the scroll
//                }
//                return Offset.Zero
//            }
//
//            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
//                return super.onPostFling(consumed, available)
//
//            }
//        }
//    }

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


    LaunchedEffect(isVisible) {
        if (!isVisible) {
            searchQuery = ""
            listState.scrollToItem(0)
            focusManager.clearFocus(force = true)
            keyboardController?.hide()
            shouldShowKeyboard = false
        }
    }

    LaunchedEffect(state.isUnlocked, shouldTriggerKeyboard) {
        if (state.isUnlocked && shouldTriggerKeyboard) {
            delay(200)
            focusRequester.requestFocus()
            keyboardController?.show()
            shouldShowKeyboard = true
        }
    }

    LaunchedEffect(listState, shouldTriggerKeyboard) {
        snapshotFlow { Triple(listState.isScrollInProgress, listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) }
            .collect { (inProgress, index, offset) ->
                if (inProgress) {
//                    // User reached the top - show keyboard
//                    if (index == 0 && offset == 0 && !shouldShowKeyboard && shouldTriggerKeyboard) {
//                        delay(200)
//                        focusRequester.requestFocus()
//                        keyboardController?.show()
//                        shouldShowKeyboard = true
//                    }
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

    Log.d("Taggzz", "State.countdown = ${state.countdown} and !state.isUnlocked = ${!state.isUnlocked}")
    if (false) {
        CountdownScreen(
            countdown = state.countdown,
            appDrawerDelay = viewModel.getAppDrawerDelay(),
            theme = theme,
            onReturnToTasks = onClose
        )
    }
    else
    {
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
                        .padding(top = 16.dp, start = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    state = listState
                ) {
                    if (searchQuery.isNotEmpty()) {
                        val searchResults = viewModel.getSearchResults(searchQuery)
                        item {
                            SearchResultsSection(
                                searchQuery = searchQuery,
                                searchResults = searchResults,
                                onAppClick = { app: AppDrawerApp ->
                                    viewModel.onEvent(AppDrawerEvent.SelectApp(app))
                                },
                                theme = theme
                            )
                        }
                    } else {

                        if(shouldShowCategorizedApps){
                            val categorizedApps = viewModel.getCategorizedApps()
                            categorizedApps.forEach { (category, apps) ->
                                item {
                                    AppCategorySection(
                                        category = category,
                                        apps = apps,
                                        onAppClick = { app: AppDrawerApp ->
                                            viewModel.onEvent(AppDrawerEvent.SelectApp(app))
                                        },
                                        theme = theme
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
                                        onClose()
                                    },
                                    theme = theme
                                )
                            }
                        }

                    }
                }
            }

        }
    }

}
