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
    var searchQuery by remember { mutableStateOf("") }

    // Create a nested scroll connection that prevents bottom sheet from closing
    // when the list is not at the top
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // We don't consume anything in onPreScroll - let the LazyColumn handle it first
                // This allows the LazyColumn to scroll normally
                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                // This is called after LazyColumn has consumed what it can
                // 'consumed' = what LazyColumn used
                // 'available' = what's left over

                val leftoverDelta = available.y

                // If we're scrolling down (positive) and there's leftover scroll
                if (leftoverDelta > 0) {
                    // Check if we're at the top of the list
                    if (!listState.canScrollBackward) {
                        // We're at the top, let the bottom sheet handle the leftover
                        // (this allows dismiss gesture)
                        return Offset.Zero
                    } else {
                        // We're not at the top, consume the leftover to prevent
                        // the bottom sheet from moving
                        return available
                    }
                }

                // For upward scrolling (negative), if there's leftover it means
                // we've hit the bottom of the list - consume it to prevent
                // bottom sheet from moving
                if (leftoverDelta < 0) {
                    return available
                }

                return Offset.Zero
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
                searchQuery = searchQuery,
                onSearchQueryChange = { query ->
                    searchQuery = query
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
                    if (searchQuery.isNotEmpty()) {
                        // Show search results
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

//            // Footer Stats
//            AppDrawerFooter(
//                currentPoints = state.currentPoints,
//                totalFreeApps = state.apps.count { it.pointCost == 0 },
//                totalPremiumApps = state.apps.count { it.pointCost > 0 },
//                theme = theme
//            )
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