package com.expeknow.ariselauncher.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navigation
import androidx.hilt.navigation.compose.hiltViewModel
import com.expeknow.ariselauncher.ui.components.AppBottomNavigationBar
import com.expeknow.ariselauncher.ui.screens.home.HomeScreen
import com.expeknow.ariselauncher.ui.screens.settings.SettingsScreen
import com.expeknow.ariselauncher.ui.screens.drive.DriveScreen
import com.expeknow.ariselauncher.ui.screens.points.TaskHistoryScreen
import com.expeknow.ariselauncher.ui.screens.home.TaskDetailsScreen
import com.expeknow.ariselauncher.ui.screens.onboarding.PermissionOnboardingScreen
import com.expeknow.ariselauncher.ui.screens.onboarding.WelcomeScreen
import com.expeknow.ariselauncher.ui.screens.apps.AppDrawerViewModel
import com.expeknow.ariselauncher.ui.screens.drive.DriveViewModel
import com.expeknow.ariselauncher.ui.screens.home.HomeViewModel
import com.expeknow.ariselauncher.ui.screens.settings.SettingsViewModel
import com.expeknow.ariselauncher.ui.screens.home.TaskDetailsViewModel
import com.expeknow.ariselauncher.ui.screens.points.PointsScreen
import com.expeknow.ariselauncher.ui.screens.points.PointsViewModel
import com.expeknow.ariselauncher.ui.screens.targets.TargetsScreen
import com.expeknow.ariselauncher.ui.screens.targets.TargetsViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: String = Screen.PermissionOnboarding.route
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavRoutes = setOf(
        Screen.Focus.route,
        Screen.Drive.route,
        Screen.Ctrl.route,
        Screen.Stats.route,
        Screen.Targets.route
    )

    val showBottomNav = currentRoute in bottomNavRoutes

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomNav,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(300, easing = LinearOutSlowInEasing)
                ) + fadeIn(tween(300)),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(300, easing = FastOutLinearInEasing)
                ) + fadeOut(tween(300))
            ) {
                AppBottomNavigationBar(navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = androidx.compose.ui.Modifier.padding(paddingValues)
        ) {
            composable(Screen.Welcome.route) {
                WelcomeScreen(navController)
            }

            composable(Screen.PermissionOnboarding.route) {
                PermissionOnboardingScreen(navController)
            }

            navigation(
                startDestination = Screen.Focus.route,
                route = Screen.Main.route
            ) {

                composable(Screen.Focus.route) { backStackEntry ->
                    val homeViewModel = backStackEntry.sharedViewModel<HomeViewModel>(navController)
                    val appDrawerViewModel =
                        backStackEntry.sharedViewModel<AppDrawerViewModel>(navController)
                    val state by homeViewModel.state.collectAsStateWithLifecycle()
                    HomeScreen(
                        navController = navController,
                        viewModel = homeViewModel,
                        appDrawerViewModel = appDrawerViewModel,
                        state = state
                    )
                }

                composable(Screen.Stats.route) { backStackEntry ->
                    val pointsViewModel = backStackEntry.sharedViewModel<PointsViewModel>(navController)
                    PointsScreen(navController, pointsViewModel)
                }

                composable(Screen.Targets.route) { backStackEntry ->
                    val targetsViewModel = backStackEntry.sharedViewModel<TargetsViewModel>(navController)
                    TargetsScreen(navController, targetsViewModel)
                }

                composable(Screen.Drive.route) { backStackEntry ->
                    val driveViewModel = backStackEntry.sharedViewModel<DriveViewModel>(navController)
                    DriveScreen(navController, driveViewModel)
                }

                composable(Screen.Ctrl.route) { backStackEntry ->
                    val settingsViewModel =
                        backStackEntry.sharedViewModel<SettingsViewModel>(navController)
                    SettingsScreen(navController, settingsViewModel)
                }
            }

            composable("taskdetails/{id}") { backStackEntry ->
                val taskDetailsViewModel =
                    backStackEntry.sharedViewModel<TaskDetailsViewModel>(navController)
                val state by taskDetailsViewModel.state.collectAsStateWithLifecycle()
                val id = backStackEntry.arguments?.getString("id") ?: ""
                TaskDetailsScreen(navController, id, taskDetailsViewModel, state)
            }

            composable(Screen.TaskHistory.route) { backStackEntry ->
                val pointsViewModel =
                    backStackEntry.sharedViewModel<PointsViewModel>(navController)
                val state by pointsViewModel.state.collectAsStateWithLifecycle()
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
    navController: NavController
): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentEntry = remember(this) { navController.getBackStackEntry(navGraphRoute) }
    return hiltViewModel(parentEntry)
}