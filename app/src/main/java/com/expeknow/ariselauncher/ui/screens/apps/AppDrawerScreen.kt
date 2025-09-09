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
    val canScrollUp = listState.canScrollBackward

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y

                // If scrolling down (positive delta) and we're at the top of the list,
                // let the bottom sheet handle it (for potential dismiss)
                if (delta > 0 && !canScrollUp) {
                    return Offset.Zero // Let bottom sheet handle
                }

                // Otherwise, let LazyColumn consume the scroll
                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                // After LazyColumn has consumed scroll, handle remaining
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
        ) {
            // Header
            AppDrawerHeader(
                onClose = {
//                    viewModel.onEvent(AppDrawerEvent.CloseDrawer)
//                    onClose()
                },
                theme = theme
            )

            // Warning Banner
//            WarningBanner()

            // App Categories
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(top = 16.dp, start = 16.dp, bottom = 16.dp)
                        .nestedScroll(nestedScrollConnection),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
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

            // Footer Stats
            AppDrawerFooter(
                currentPoints = state.currentPoints,
                totalFreeApps = state.apps.count { it.pointCost == 0 },
                totalPremiumApps = state.apps.count { it.pointCost > 0 },
                theme = theme
            )
        }
    }

    // Warning Dialog
//    AppWarningDialog(
//        app = state.selectedApp,
//        onConfirm = {
//            viewModel.onEvent(AppDrawerEvent.ConfirmAppOpen)
//            onClose()
//        },
//        onDismiss = {
//            viewModel.onEvent(AppDrawerEvent.HideWarning)
//        }
//    )
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