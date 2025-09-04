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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.expeknow.ariselauncher.AriseLauncherApplication
import com.expeknow.ariselauncher.data.repository.AppRepositoryImpl
import com.expeknow.ariselauncher.ui.theme.BannerTextGray
import com.expeknow.ariselauncher.ui.components.AppBottomNavigationBar
import com.expeknow.ariselauncher.ui.screens.apps.AppDrawerScreen
import com.expeknow.ariselauncher.ui.screens.apps.AppDrawerViewModel
import com.expeknow.ariselauncher.ui.screens.drive.DriveScreen
import com.expeknow.ariselauncher.ui.screens.home.HomeViewModel
import com.expeknow.ariselauncher.ui.screens.home.TaskDetailsViewModel
import com.expeknow.ariselauncher.ui.screens.points.PointsViewModel

@Composable
fun AppNavigation(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Define routes that should show bottom navigation
    val bottomNavRoutes = setOf(
        Screen.Focus.route,
        Screen.Points.route,
        Screen.Drive.route,
        Screen.Apps.route,
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
                val context = LocalContext.current
                val appRepositoryImpl = remember { AppRepositoryImpl(context) }
                val taskRepository = (context.applicationContext as AriseLauncherApplication).taskRepositoryImpl
                val viewModel: HomeViewModel = viewModel { HomeViewModel(appRepositoryImpl, taskRepository) }
                val state by viewModel.state.collectAsStateWithLifecycle()

                HomeScreen(navController, viewModel, state)
            }
            composable(Screen.Points.route) {
                val context = LocalContext.current
                val taskRepository =
                    (context.applicationContext as AriseLauncherApplication).taskRepositoryImpl
                val pointsViewModel: PointsViewModel = viewModel { PointsViewModel(taskRepository) }

                PointsScreen(navController, pointsViewModel)
            }
            composable(Screen.Drive.route) {
                DriveScreen(navController)
            }
            composable(Screen.Apps.route) {
                val context = LocalContext.current
                val taskRepository =
                    (context.applicationContext as AriseLauncherApplication).taskRepositoryImpl
                val appDrawerViewModel: AppDrawerViewModel =
                    viewModel { AppDrawerViewModel(taskRepository) }

                AppDrawerScreen(navController, {}, appDrawerViewModel)
            }
            composable(Screen.Ctrl.route) {
                SettingsScreen(navController)
            }
            composable(Screen.Settings.route) {
                SettingsScreen(navController)
            }
            composable("taskdetails/{id}") { backStackEntry ->
                val context = LocalContext.current
                val taskRepository = (context.applicationContext as AriseLauncherApplication).taskRepositoryImpl
                val viewModel: TaskDetailsViewModel = viewModel { TaskDetailsViewModel(taskRepository) }
                val state by viewModel.state.collectAsStateWithLifecycle()

                val id = backStackEntry.arguments?.getString("id") ?: ""
                TaskDetailsScreen(navController, id, viewModel, state)
            }
        }
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(
    navController: NavController,
): T {

    val navGraphRoute = destination.parent?.route ?: return viewModel()
    val parentEntry  = remember(this){
        navController.getBackStackEntry(navGraphRoute)
    }
    return viewModel(parentEntry)
}
