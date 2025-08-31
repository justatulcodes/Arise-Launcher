package com.expeknow.ariselauncher.ui.screens.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun AppDrawerScreen(
    navController: NavController,
    onClose: () -> Unit = {},
    viewModel: AppDrawerViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val theme = AppDrawerTheme()

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
                    viewModel.onEvent(AppDrawerEvent.CloseDrawer)
                    onClose()
                },
                theme = theme
            )

            // Warning Banner
            WarningBanner()

            // App Categories
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight() // Add this to constrain the height
                        .padding(16.dp),
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
    AppWarningDialog(
        app = state.selectedApp,
        onConfirm = {
            viewModel.onEvent(AppDrawerEvent.ConfirmAppOpen)
            onClose()
        },
        onDismiss = {
            viewModel.onEvent(AppDrawerEvent.HideWarning)
        }
    )
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