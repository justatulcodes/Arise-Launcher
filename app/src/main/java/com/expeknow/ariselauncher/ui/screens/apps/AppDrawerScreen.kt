package com.expeknow.ariselauncher.ui.screens.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.expeknow.ariselauncher.ui.screens.home.HomeViewModel
import com.expeknow.ariselauncher.ui.screens.apps.AppDrawerViewModel

@Composable
fun AppDrawerScreen(
    navController: NavController,
    onClose: () -> Unit = {},
    viewModel: AppDrawerViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val theme = AppDrawerTheme()
    val listState = rememberLazyListState()

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y

                // Check if we can scroll in the direction of the gesture
                val canScrollUp = listState.canScrollBackward
                val canScrollDown = listState.canScrollForward

                // If scrolling up (negative delta) and list can scroll up,
                // consume the scroll to prevent bottom sheet from intercepting
                if (delta < 0 && canScrollDown) {
                    // Let the LazyColumn handle it by not consuming here
                    return Offset.Zero
                }

                // If scrolling down (positive delta) and list can scroll up,
                // consume the scroll to prevent bottom sheet from closing
                if (delta > 0 && canScrollUp) {
                    // Let the LazyColumn handle it by not consuming here
                    return Offset.Zero
                }

                // Only when at the very top and scrolling down further,
                // let the bottom sheet handle it (for dismiss gesture)
                if (delta > 0 && !canScrollUp) {
                    return Offset.Zero // Let bottom sheet handle the dismiss
                }

                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                // If there's remaining scroll after LazyColumn consumed what it could,
                // and we're at the top of the list scrolling down, let bottom sheet handle it
                if (available.y > 0 && !listState.canScrollBackward) {
                    return Offset.Zero // Let bottom sheet consume the remaining
                }

                // Otherwise consume any remaining to prevent bottom sheet from moving
                return available
            }
        }
    }

    if (!state.isUnlocked) {
        CountdownScreen(
            countdown = state.countdown,
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
                searchQuery = state.searchQuery,
                onSearchQueryChange = { query ->
                    viewModel.onEvent(AppDrawerEvent.SearchApps(query))
                },
                theme = theme
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
                    if (state.searchQuery.isNotEmpty()) {
                        // Show search results
                        val searchResults = viewModel.getSearchResults()
                        item {
                            SearchResultsSection(
                                searchQuery = state.searchQuery,
                                searchResults = searchResults,
                                onAppClick = { app: AppDrawerApp ->
                                    viewModel.onEvent(AppDrawerEvent.SelectApp(app))
                                },
                                theme = theme
                            )
                        }
                    } else {
                        // Show categorized apps
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
                }
            }

            // Footer Stats
            AppDrawerFooter(
                currentPoints = state.currentPoints,
                totalFreeApps = state.apps.count { it.pointCost == 0 },
                totalPremiumApps = state.apps.count { it.pointCost > 0 },
                theme = theme
            )
        }
    }

}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun AppDrawerScreenPreview() {
    AppDrawerScreen(
        navController = androidx.navigation.compose.rememberNavController(),
        onClose = {},
        viewModel = viewModel()
    )
}