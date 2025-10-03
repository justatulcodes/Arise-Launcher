package com.expeknow.ariselauncher.ui.navigation

sealed class Screen(val route: String) {
    data object Settings : Screen("settings")
    data object Focus : Screen("focus")
    data object Points : Screen("points")
    data object Drive : Screen("drive")
//    data object Apps : Screen("apps")
    data object Ctrl : Screen("ctrl")
    data class TaskDetails(val id: String) : Screen("taskdetails/{id}") {
        companion object {
            fun routeFor(id: String) = "taskdetails/$id"
        }
    }
}