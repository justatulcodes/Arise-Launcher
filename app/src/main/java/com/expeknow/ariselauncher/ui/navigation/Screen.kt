package com.expeknow.ariselauncher.ui.navigation

sealed class Screen(val route: String) {

    data object Welcome : Screen("welcome")

    data object PermissionOnboarding : Screen("permission_onboarding")

    data object Main : Screen("main")

    data object Focus : Screen("focus")
    data object Stats : Screen("stats")
    data object Targets : Screen("targets")
    data object Drive : Screen("drive")
    data object Ctrl : Screen("ctrl")
    data object Support : Screen("support")

    data object TaskHistory : Screen("task_history")

    data class TaskDetails(val id: String) : Screen("taskdetails/{id}") {
        companion object {
            fun routeFor(id: String) = "taskdetails/$id"
        }
    }
}
