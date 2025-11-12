package com.expeknow.ariselauncher.ui.screens.apps

import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.Color

data class AppDrawerApp(
    val id: String,
    val name: String,
    val packageName: String,
    val icon: Drawable? = null,
    var category: AppCategory,
    var pointCost: Int = 10,
    val description: String = "",
    val appInstallTime : Long
)


enum class AppCategory(
    val displayName: String,
    val color: Color,
    val priority: Int // Lower number = higher priority
) {
    ESSENTIAL("Essential", Color(0xFF81C784), 4),
    PRODUCTIVITY("Productivity", Color(0xFF64B5F6), 1),
    HEALTH("Health & Fitness", Color(0xFF66BB6A), 3),
    FINANCE("Finance", Color(0xFF26A69A), 2),
    UTILITY("Utility", Color(0xFF9575CD), 5),
    COMMUNICATION("Communication", Color(0xFF42A5F5), 6),
    SHOPPING("Shopping", Color(0xFFFFB74D), 7),
    ENTERTAINMENT("Entertainment", Color(0xFFFFD54F), 8),
    STREAMING("Streaming", Color(0xFFFF7043), 9),
    SOCIAL_MEDIA("Social Media", Color(0xFFE57373), 10),
    GAMES("Games", Color(0xFFBA68C8), 11),
    MISCELLANEOUS("Miscellaneous", Color(0xFFA1887F), 12)
}
data class AppDrawerState(
    val countdown: Int = 0,
    val isUnlocked: Boolean = true,
    val selectedApp: AppDrawerApp? = null,
    val currentPoints: Int = 0,
    val apps: List<AppDrawerApp> = emptyList(),
    val showTimerDialog: Boolean = false,
    val timerCountdown: Int = 0,
    val timerApp: AppDrawerApp? = null
)


sealed class AppDrawerEvent {
    data class UpdateCountdown(val countdown: Int) : AppDrawerEvent()
    data object UnlockDrawer : AppDrawerEvent()
    data class SelectApp(val app: AppDrawerApp) : AppDrawerEvent()
    data object ShowWarning : AppDrawerEvent()
    data object HideWarning : AppDrawerEvent()
    data object ConfirmAppOpen : AppDrawerEvent()
    data object CloseDrawer : AppDrawerEvent()
    data object OpenDrawer : AppDrawerEvent()
    data class SearchApps(val query: String) : AppDrawerEvent()
    data object DismissTimerDialog : AppDrawerEvent()
}

data class AppDrawerTheme(
    val accent: Color = Color.White,
    val border: Color = Color.White.copy(alpha = 0.2f),
    val background: Color = Color(0xFF1F1F1F)
)