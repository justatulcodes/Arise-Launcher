package com.expeknow.ariselauncher.ui.navigation

sealed class Screen(val route: String) {

    data object PermissionOnboarding : Screen("permission_onboarding")

    data object Main : Screen("main")

    data object Focus : Screen("focus")
    data object Drive : Screen("drive")
    data object Ctrl : Screen("ctrl")

    data object TaskHistory : Screen("task_history")

    data class TaskDetails(val id: String) : Screen("taskdetails/{id}") {
        companion object {
            fun routeFor(id: String) = "taskdetails/$id"
        }
    }
}
