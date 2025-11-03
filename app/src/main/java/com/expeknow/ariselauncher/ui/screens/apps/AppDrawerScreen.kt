package com.expeknow.ariselauncher.ui.screens.apps

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.platform.LocalFocusManager
import android.util.Log

@Composable
fun AppDrawerScreen(
    onClose: () -> Unit = {},
    viewModel: AppDrawerViewModel = viewModel(),
    shouldShowCategorizedApps: Boolean
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val theme = AppDrawerTheme()
    val listState = rememberLazyListState()
    var searchQuery by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val shouldTriggerKeyboard = viewModel.getShouldTriggerKeyboard()
    val focusManager = LocalFocusManager.current

    val TAG = "AppDrawerXXX"

    var shouldShowKeyboard by remember { mutableStateOf(true) }
    var isBottomSheetMoving by remember { mutableStateOf(false) }

    LaunchedEffect(state.isUnlocked, shouldTriggerKeyboard) {
        Log.d(TAG, "[Keyboard] LaunchedEffect: isUnlocked=${state.isUnlocked}, shouldTriggerKeyboard=$shouldTriggerKeyboard")
        if (state.isUnlocked && shouldTriggerKeyboard) {
            Log.d(TAG, "[Keyboard] App unlocked and shouldTriggerKeyboard=true -> requesting focus and showing keyboard")
            delay(200)
            focusRequester.requestFocus()
            keyboardController?.show()
            shouldShowKeyboard = true
            Log.d(TAG, "[Keyboard] Keyboard shown, shouldShowKeyboard=$shouldShowKeyboard")
        }
    }

    LaunchedEffect(listState, shouldTriggerKeyboard) {
        snapshotFlow { Triple(listState.isScrollInProgress, listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) }
            .collect { (inProgress, index, offset) ->
                Log.d(TAG, "[Scroll] listState change: inProgress=$inProgress index=$index " +
                        "offset=$offset canScrollBackward=${listState.canScrollBackward} canScrollForward=${listState.canScrollForward}")
                if (inProgress) {
                    // User reached the top - show keyboard
                    if (index == 0 && offset == 0 && !shouldShowKeyboard && shouldTriggerKeyboard) {
                        Log.d(TAG, "[Keyboard] At top of list and keyboard hidden -> showing keyboard")
                        delay(200)
                        focusRequester.requestFocus()
                        keyboardController?.show()
                        shouldShowKeyboard = true
                        Log.d(TAG, "[Keyboard] Keyboard shown from top-of-list, shouldShowKeyboard=$shouldShowKeyboard")
                    }
                    // User scrolled down - hide keyboard and clear focus
                    else if (shouldShowKeyboard && (index > 0 || offset > 50)) {
                        Log.d(TAG, "[Keyboard] User scrolled down (index=$index, offset=$offset) -> hiding keyboard and clearing focus")
                        keyboardController?.hide()
                        focusManager.clearFocus(force = true)
                        shouldShowKeyboard = false
                        Log.d(TAG, "[Keyboard] Keyboard hidden, shouldShowKeyboard=$shouldShowKeyboard")
                    }
                }
            }
    }

    LaunchedEffect(isBottomSheetMoving) {
        Log.d(TAG, "[BottomSheet] isBottomSheetMoving changed -> $isBottomSheetMoving")
        if (isBottomSheetMoving && shouldShowKeyboard) {
            Log.d(TAG, "[BottomSheet][Keyboard] Bottom sheet starting to move while keyboard visible -> hiding keyboard and clearing focus")
            keyboardController?.hide()
            focusManager.clearFocus(force = true)
            shouldShowKeyboard = false
            Log.d(TAG, "[BottomSheet][Keyboard] Keyboard hidden due to bottom sheet movement, shouldShowKeyboard=$shouldShowKeyboard")
        }
    }

    // Create a nested scroll connection that prevents bottom sheet from closing
    // when the list is not at the top
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                Log.d(TAG, "[NSC][onPreScroll] available=$available source=$source -> letting LazyColumn handle")
                // We don't consume anything in onPreScroll - let the LazyColumn handle it first
                // This allows the LazyColumn to scroll normally
                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                Log.d(TAG, "[NSC][onPostScroll] consumed=$consumed available=$available source=$source " +
                        "canScrollBackward=${listState.canScrollBackward}")
                // This is called after LazyColumn has consumed what it can
                // 'consumed' = what LazyColumn used
                // 'available' = what's left over

                val leftoverDelta = available.y
                Log.d(TAG, "[NSC][onPostScroll] leftoverDeltaY=$leftoverDelta")

                // If we're scrolling down (positive) and there's leftover scroll
                if (leftoverDelta > 0) {
                    // Check if we're at the top of the list
                    if (!listState.canScrollBackward) {
                        Log.d(TAG, "[BottomSheet][NSC] At top and scrolling down with leftover -> allow bottom sheet to move (dismiss gesture)")
                        // We're at the top, let the bottom sheet handle the leftover
                        // (this allows dismiss gesture)
                        // Bottom sheet is about to move - hide keyboard
                        if (!isBottomSheetMoving) {
                            Log.d(TAG, "[BottomSheet] Marking bottom sheet as moving")
                            isBottomSheetMoving = true
                        }
                        return Offset.Zero
                    } else {
                        Log.d(TAG, "[BottomSheet][NSC] Not at top and scrolling down with leftover -> consume to block bottom sheet movement")
                        // We're not at the top, consume the leftover to prevent
                        // the bottom sheet from moving
                        if (isBottomSheetMoving) {
                            Log.d(TAG, "[BottomSheet] Stopping bottom sheet movement flag")
                            isBottomSheetMoving = false
                        }
                        return available
                    }
                }

                // For upward scrolling (negative), if there's leftover it means
                // we've hit the bottom of the list - consume it to prevent
                // bottom sheet from moving
                if (leftoverDelta < 0) {
                    Log.d(TAG, "[BottomSheet][NSC] Upward leftover (likely bottom of list) -> consume to block bottom sheet movement")
                    if (isBottomSheetMoving) {
                        Log.d(TAG, "[BottomSheet] Stopping bottom sheet movement flag")
                        isBottomSheetMoving = false
                    }
                    return available
                }

                Log.d(TAG, "[NSC][onPostScroll] No leftover -> returning Offset.Zero")
                return Offset.Zero
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                Log.d(TAG, "[NSC][onPreFling] available=$available -> letting LazyColumn handle first")
                // Let the LazyColumn handle flings first; we will gate leftover in onPostFling
                return Velocity.Zero
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                val leftoverY = available.y
                Log.d(TAG, "[NSC][onPostFling] consumed=$consumed available=$available leftoverY=$leftoverY" +
                        " canScrollBackward=${listState.canScrollBackward}")

                // Downward fling: if not at the top, consume leftover to block bottom sheet
                if (leftoverY > 0f) {
                    return if (listState.canScrollBackward) {
                        if (isBottomSheetMoving) {
                            Log.d(TAG, "[BottomSheet] Stopping bottom sheet movement flag (downward fling, not at top)")
                            isBottomSheetMoving = false
                        }
                        Log.d(TAG, "[BottomSheet][NSC] Downward fling leftover while not at top -> consume to block bottom sheet")
                        available
                    } else {
                        // At top: allow bottom sheet to receive it (dismiss gesture)
                        if (!isBottomSheetMoving) {
                            Log.d(TAG, "[BottomSheet] Marking bottom sheet as moving (downward fling at top)")
                            isBottomSheetMoving = true
                        }
                        Log.d(TAG, "[BottomSheet][NSC] Downward fling leftover at top -> allow bottom sheet (return Velocity.Zero)")
                        Velocity.Zero
                    }
                }

                // Upward fling: consume leftover so bottom sheet doesn't react
                if (leftoverY < 0f) {
                    if (isBottomSheetMoving) {
                        Log.d(TAG, "[BottomSheet] Stopping bottom sheet movement flag (upward fling)")
                        isBottomSheetMoving = false
                    }
                    Log.d(TAG, "[BottomSheet][NSC] Upward fling leftover -> consume to block bottom sheet")
                    return available
                }

                Log.d(TAG, "[NSC][onPostFling] No leftover -> returning Velocity.Zero")
                return Velocity.Zero
            }
        }
    }

    if (!state.isUnlocked) {
        Log.d(TAG, "[AppDrawer] Showing CountdownScreen (locked state)")
        CountdownScreen(
            countdown = state.countdown,
            appDrawerDelay = viewModel.getAppDrawerDelay(),
            theme = theme,
            onReturnToTasks = onClose
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .nestedScroll(nestedScrollConnection)
        ) {
            AppDrawerSearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { query ->
                    Log.d(TAG, "[Search] Query changed: '$query'")
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
                        .padding(top = 16.dp, start = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    state = listState
                ) {
                    if (searchQuery.isNotEmpty()) {
                        // Show search results
                        val searchResults = viewModel.getSearchResults(searchQuery)
                        item {
                            SearchResultsSection(
                                searchQuery = searchQuery,
                                searchResults = searchResults,
                                onAppClick = { app: AppDrawerApp ->
                                    Log.d(TAG, "[AppClick][SearchResults] Selected app: $app")
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
                                            Log.d(TAG, "[AppClick][Category:$category] Selected app: $app")
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
                                        Log.d(TAG, "[AppClick][SearchResults] Selected app: $app")
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
