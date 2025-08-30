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
import com.expeknow.ariselauncher.ui.screens.applist.AppListScreen
import com.expeknow.ariselauncher.ui.screens.home.HomeScreen
import com.expeknow.ariselauncher.ui.screens.home.TaskDetailsScreen
import com.expeknow.ariselauncher.ui.screens.points.PointsScreen
import com.expeknow.ariselauncher.ui.screens.settings.SettingsScreen
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import com.expeknow.ariselauncher.ui.theme.BannerTextGray
import com.expeknow.ariselauncher.ui.components.AppBottomNavigationBar
import com.expeknow.ariselauncher.ui.screens.apps.AppDrawerScreen
import com.expeknow.ariselauncher.ui.screens.drive.DriveScreen

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object AppList : Screen("applist")
    data object Settings : Screen("settings")
    data object Focus : Screen("focus")
    data object Points : Screen("points")
    data object Drive : Screen("drive")
    data object Apps : Screen("apps")
    data object Ctrl : Screen("ctrl")
    data class TaskDetails(val id: String) : Screen("taskdetails/{id}") {
        companion object {
            fun routeFor(id: String) = "taskdetails/$id"
        }
    }
}

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
                HomeScreen(navController)
            }
            composable(Screen.Points.route) {
                PointsScreen(navController)
            }
            composable(Screen.Drive.route) {
                DriveScreen(navController)
            }
            composable(Screen.Apps.route) {
                AppDrawerScreen(navController)
            }
            composable(Screen.Ctrl.route) {
                SettingsScreen(navController)
            }
            composable(Screen.Settings.route) {
                SettingsScreen(navController)
            }
            composable("taskdetails/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: ""
                TaskDetailsScreen(navController, id)
            }
        }
    }
}

@Composable
fun EmptyScreen(label: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("$label Page", style = MaterialTheme.typography.displayLarge, color = BannerTextGray)
    }
}

// Legacy function for backward compatibility - will be removed
@Composable
fun NavGraph(navController: NavHostController) = AppNavigation(navController)