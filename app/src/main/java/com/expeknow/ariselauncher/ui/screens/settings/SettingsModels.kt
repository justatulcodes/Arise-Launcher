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
    val appDrawerDelay: Float = 0f,
    val distractionAppsDelay: Float = 30f,
    val pointThreshold: Float = 50f,
    val warningsEnabled: Boolean = true,
    val keyboardTriggerEnabled: Boolean = false,
    val showCategorizedAppsEnabled : Boolean = false,
    val showWeeklyScheduleEnabled: Boolean = false,
    val appTimerEnabled: Boolean = true,
    val isDefaultLauncher: Boolean = false,
    val showResetPointsDialog: Boolean = false,
    val showFactoryResetDialog: Boolean = false,
    val showAppRefreshDialog: Boolean = false,
)

sealed class SettingsEvent {
    data class ToggleCompletedTasks(val hide: Boolean) : SettingsEvent()
    data class ToggleTunnelVision(val enabled: Boolean) : SettingsEvent()
    data class UpdateAppDrawerDelay(val delay: Float) : SettingsEvent()
    data class UpdateDistractionDelay(val delay: Float) : SettingsEvent()
    data class UpdatePointThreshold(val threshold: Float) : SettingsEvent()
    data class ToggleWarnings(val enabled: Boolean) : SettingsEvent()
    data class ToggleKeyboardTrigger(val enabled: Boolean) : SettingsEvent()
    data class ToggleWeeklySchedule(val enabled: Boolean) : SettingsEvent()
    data class ToggleShowCategorizedApps(val enabled: Boolean) : SettingsEvent()
    data class ToggleAppTimer(val enabled: Boolean) : SettingsEvent()
    data object ShowResetPointsDialog : SettingsEvent()
    data object HideResetPointsDialog : SettingsEvent()
    data object ShowFactoryResetDialog : SettingsEvent()
    data object ShowAppRefreshDialog : SettingsEvent()
    data object HideAppRefreshDialog : SettingsEvent()
    data object HideFactoryResetDialog : SettingsEvent()
    data object ResetAllPoints : SettingsEvent()
    data object RefreshApps : SettingsEvent()
    data class SetDefaultLauncher(val isDefault: Boolean) : SettingsEvent()
    data object FactoryReset : SettingsEvent()
}