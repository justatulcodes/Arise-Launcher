package com.expeknow.ariselauncher.ui.screens.applist

import androidx.compose.ui.graphics.Color
import com.expeknow.ariselauncher.data.model.AppInfo

data class AppListState(
    val apps: List<AppInfo> = emptyList(),
    val isLoading: Boolean = true
)

sealed class AppListEvent {
    data object LoadApps : AppListEvent()
    data class LaunchApp(val app: AppInfo) : AppListEvent()
    data object NavigateToSettings : AppListEvent()
}

data class AppListTheme(
    val background: Color = Color.Black,
    val surface: Color = Color(0xFF1F1F1F),
    val textPrimary: Color = Color.White,
    val textSecondary: Color = Color(0xFF9CA3AF),
    val border: Color = Color(0xFF374151)
)