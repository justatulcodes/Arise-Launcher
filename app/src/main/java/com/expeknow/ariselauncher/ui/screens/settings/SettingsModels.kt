package com.expeknow.ariselauncher.ui.screens.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class SettingsAppInfo(
    val id: String,
    val name: String,
    val essential: Boolean,
    val icon: ImageVector,
    val category: AppCategory
)

enum class AppCategory(val displayName: String) {
    ESSENTIAL("Essential"),
    PRODUCTIVITY("Productivity"),
    SOCIAL("Social"),
    ENTERTAINMENT("Entertainment")
}

data class SettingsTheme(
    val accent: Color = Color.White,
    val border: Color = Color.White.copy(alpha = 0.2f),
    val background: Color = Color(0xFF1A1A1A)
)

data class SettingsState(
    val hideCompletedTasks: Boolean = true,
    val tunnelVisionMode: Boolean = true,
    val appDrawerDelay: Float = 60f,
    val distractionAppsDelay: Float = 30f,
    val pointThreshold: Float = 50f,
    val warningsEnabled: Boolean = true,
    val apps: List<SettingsAppInfo> = getDefaultApps()
)

private fun getDefaultApps(): List<SettingsAppInfo> = listOf(
    SettingsAppInfo("1", "Phone", true, Icons.Default.Phone, AppCategory.ESSENTIAL),
    SettingsAppInfo("2", "Messages", true, Icons.Default.Message, AppCategory.ESSENTIAL),
    SettingsAppInfo("3", "Mail", false, Icons.Default.Email, AppCategory.PRODUCTIVITY),
    SettingsAppInfo("4", "Calendar", false, Icons.Default.CalendarToday, AppCategory.PRODUCTIVITY)
)

sealed class SettingsEvent {
    data class ToggleCompletedTasks(val hide: Boolean) : SettingsEvent()
    data class ToggleTunnelVision(val enabled: Boolean) : SettingsEvent()
    data class UpdateAppDrawerDelay(val delay: Float) : SettingsEvent()
    data class UpdateDistractionDelay(val delay: Float) : SettingsEvent()
    data class UpdatePointThreshold(val threshold: Float) : SettingsEvent()
    data class ToggleWarnings(val enabled: Boolean) : SettingsEvent()
    data class ToggleAppEssential(val appId: String) : SettingsEvent()
    data object ResetAllPoints : SettingsEvent()
    data object FactoryReset : SettingsEvent()
}