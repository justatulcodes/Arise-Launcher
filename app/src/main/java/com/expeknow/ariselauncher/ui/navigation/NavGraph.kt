package com.expeknow.ariselauncher.ui.navigation

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
//        Screen.Apps.route,
        Screen.Ctrl.route
    )

    val showBottomNav = currentRoute in bottomNavRoutes

    Scaffold(
        bottomBar = {
            if (showBottomNav) {
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
//            composable(Screen.Apps.route) {
//
//                val appDrawerViewModel = it.sharedViewModel<AppDrawerViewModel>(navController)
//                AppDrawerScreen(navController, {}, appDrawerViewModel)
//            }
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