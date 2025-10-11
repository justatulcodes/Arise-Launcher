package com.expeknow.ariselauncher.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.expeknow.ariselauncher.ui.screens.home.HomeScreen
import com.expeknow.ariselauncher.ui.screens.home.TaskDetailsScreen
import com.expeknow.ariselauncher.ui.screens.points.PointsScreen
import com.expeknow.ariselauncher.ui.screens.points.TaskHistoryScreen
import com.expeknow.ariselauncher.ui.screens.settings.SettingsScreen
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.expeknow.ariselauncher.AriseLauncherApplication
import com.expeknow.ariselauncher.data.repository.AppRepositoryImpl
import com.expeknow.ariselauncher.ui.theme.BannerTextGray
import com.expeknow.ariselauncher.ui.components.AppBottomNavigationBar
import com.expeknow.ariselauncher.ui.screens.apps.AppDrawerApp
import com.expeknow.ariselauncher.ui.screens.apps.AppDrawerScreen
import com.expeknow.ariselauncher.ui.screens.apps.AppDrawerViewModel
import com.expeknow.ariselauncher.ui.screens.drive.DriveScreen
import com.expeknow.ariselauncher.ui.screens.home.HomeViewModel
import com.expeknow.ariselauncher.ui.screens.home.TaskDetailsState
import com.expeknow.ariselauncher.ui.screens.home.TaskDetailsViewModel
import com.expeknow.ariselauncher.ui.screens.points.PointsViewModel
import com.expeknow.ariselauncher.ui.screens.settings.SettingsViewModel

@Composable
fun AppNavigation(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavRoutes = setOf(
        Screen.Focus.route,
        Screen.Points.route,
        Screen.Drive.route,
        Screen.Ctrl.route
    )

    val showBottomNav = currentRoute in bottomNavRoutes

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomNav,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(300)),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(durationMillis = 300, easing = FastOutLinearInEasing)
                ) + fadeOut(animationSpec = tween(300))
            ) {
                AppBottomNavigationBar(navController = navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Focus.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Focus.route) {

                val viewModel = it.sharedViewModel<HomeViewModel>(navController = navController)
                val appDrawerViewModel = it.sharedViewModel<AppDrawerViewModel>(navController)
                val state by viewModel.state.collectAsStateWithLifecycle()

                HomeScreen(navController, viewModel, appDrawerViewModel, state)
            }
            composable(Screen.Points.route) {
                val pointsViewModel = it.sharedViewModel<PointsViewModel>(navController)
                PointsScreen(navController, pointsViewModel)
            }
            composable(Screen.Drive.route) {
                DriveScreen(navController)
            }
            composable(Screen.Ctrl.route) {
                val viewModel = it.sharedViewModel<SettingsViewModel>(navController = navController)
                SettingsScreen(navController, viewModel)
            }
            composable(Screen.Settings.route) {

                val viewModel = it.sharedViewModel<SettingsViewModel>(navController = navController)
                SettingsScreen(navController, viewModel)
            }
            composable("taskdetails/{id}") { backStackEntry ->
                val taskDetailsViewModel = backStackEntry.sharedViewModel<TaskDetailsViewModel>(navController)
                val state by taskDetailsViewModel.state.collectAsStateWithLifecycle()
                val id = backStackEntry.arguments?.getString("id") ?: ""
                TaskDetailsScreen(navController, id, taskDetailsViewModel, state)
            }
            composable(Screen.TaskHistory.route) { backStackEntry ->
                val pointsViewModel = backStackEntry.sharedViewModel<PointsViewModel>(navController)
                val state by pointsViewModel.state.collectAsStateWithLifecycle()
                // Pass the completed tasks from the PointsViewModel state
                TaskHistoryScreen(
                    navController = navController,
                    completedTasks = state.completedTasks,
                    currentRank = state.debugCurrentRank ?: state.currentRank
                )
            }
        }
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(
    navController: NavController,
): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentEntry  = remember(this){
        navController.getBackStackEntry(navGraphRoute)
    }
    return hiltViewModel(parentEntry)
}