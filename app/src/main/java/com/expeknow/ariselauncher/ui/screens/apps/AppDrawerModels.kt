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
    val description: String = ""
)

enum class AppCategory(val displayName: String, val color: Color) {
    ESSENTIAL("Essential", Color(0xFF81C784)),
    PRODUCTIVITY("Productivity", Color(0xFF64B5F6)),
    SOCIAL("Social", Color(0xFFE57373)),
    ENTERTAINMENT("Entertainment", Color(0xFFFFD54F)),
    GAMES("Games", Color(0xFFBA68C8)),
    SHOPPING("Shopping", Color(0xFFFFB74D)),
    UTILITY("Utility", Color(0xFF9575CD)),
    MISCELLANEOUS("Miscellaneous", Color(0xFFA1887F))
}

data class AppDrawerState(
    val countdown: Int = 10,
    val isUnlocked: Boolean = false,
    val selectedApp: AppDrawerApp? = null,
    val showWarning: Boolean = false,
    val currentPoints: Int = 0,
    val apps: List<AppDrawerApp> = emptyList(),
    val searchQuery: String = ""
)


sealed class AppDrawerEvent {
    data class UpdateCountdown(val countdown: Int) : AppDrawerEvent()
    data object UnlockDrawer : AppDrawerEvent()
    data class SelectApp(val app: AppDrawerApp) : AppDrawerEvent()
    data object ShowWarning : AppDrawerEvent()
    data object HideWarning : AppDrawerEvent()
    data object ConfirmAppOpen : AppDrawerEvent()
    data object CloseDrawer : AppDrawerEvent()
    data class SearchApps(val query: String) : AppDrawerEvent()
}

data class AppDrawerTheme(
    val accent: Color = Color.White,
    val border: Color = Color.White.copy(alpha = 0.2f),
    val background: Color = Color(0xFF1F1F1F)
)