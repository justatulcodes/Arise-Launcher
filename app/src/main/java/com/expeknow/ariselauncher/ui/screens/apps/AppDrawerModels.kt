package com.expeknow.ariselauncher.ui.screens.apps

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class AppDrawerApp(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val category: AppCategory,
    val pointCost: Int,
    val description: String
)

enum class AppCategory(val displayName: String, val color: Color) {
    ESSENTIAL("Essential", Color(0xFF81C784)),
    PRODUCTIVITY("Productivity", Color(0xFF64B5F6)),
    SOCIAL("Social", Color(0xFFE57373)),
    ENTERTAINMENT("Entertainment", Color(0xFFFFD54F)),
    GAMES("Games", Color(0xFFBA68C8)),
    SHOPPING("Shopping", Color(0xFFFFB74D))
}

data class AppDrawerState(
    val countdown: Int = 60,
    val isUnlocked: Boolean = false,
    val selectedApp: AppDrawerApp? = null,
    val showWarning: Boolean = false,
    val currentPoints: Int = 245,
    val apps: List<AppDrawerApp> = getDefaultApps()
)

private fun getDefaultApps(): List<AppDrawerApp> = listOf(
    // Essential Apps
    AppDrawerApp(
        "1",
        "Phone",
        Icons.Default.Phone,
        AppCategory.ESSENTIAL,
        0,
        "Make and receive calls"
    ),
    AppDrawerApp(
        "2",
        "Messages",
        Icons.Default.Message,
        AppCategory.ESSENTIAL,
        0,
        "Text messaging"
    ),
    AppDrawerApp("3", "Mail", Icons.Default.Email, AppCategory.ESSENTIAL, 0, "Email communication"),
    AppDrawerApp(
        "4",
        "Calendar",
        Icons.Default.CalendarToday,
        AppCategory.ESSENTIAL,
        0,
        "Schedule management"
    ),

    // Productivity Apps
    AppDrawerApp(
        "5",
        "Camera",
        Icons.Default.PhotoCamera,
        AppCategory.PRODUCTIVITY,
        0,
        "Take photos and videos"
    ),
    AppDrawerApp(
        "6",
        "Maps",
        Icons.Default.Place,
        AppCategory.PRODUCTIVITY,
        0,
        "Navigation and location"
    ),
    AppDrawerApp(
        "7",
        "Calculator",
        Icons.Default.Calculate,
        AppCategory.PRODUCTIVITY,
        0,
        "Basic calculations"
    ),
    AppDrawerApp(
        "8",
        "Files",
        Icons.Default.InsertDriveFile,
        AppCategory.PRODUCTIVITY,
        0,
        "File management"
    ),
    AppDrawerApp("9", "Notes", Icons.Default.Note, AppCategory.PRODUCTIVITY, 0, "Quick notes"),
    AppDrawerApp(
        "10",
        "Slack",
        Icons.Default.Forum,
        AppCategory.PRODUCTIVITY,
        2,
        "Work communication"
    ),

    // Social Media Apps (High Point Cost)
    AppDrawerApp(
        "11",
        "Instagram",
        Icons.Default.CameraAlt,
        AppCategory.SOCIAL,
        25,
        "Photo sharing social media"
    ),
    AppDrawerApp(
        "12",
        "Twitter",
        Icons.Default.Chat,
        AppCategory.SOCIAL,
        20,
        "Microblogging platform"
    ),
    AppDrawerApp(
        "13",
        "Facebook",
        Icons.Default.People,
        AppCategory.SOCIAL,
        30,
        "Social networking"
    ),

    // Entertainment Apps (Medium Point Cost)
    AppDrawerApp(
        "14",
        "YouTube",
        Icons.Default.VideoLibrary,
        AppCategory.ENTERTAINMENT,
        15,
        "Video streaming platform"
    ),
    AppDrawerApp(
        "15",
        "Music Streaming",
        Icons.Default.MusicNote,
        AppCategory.ENTERTAINMENT,
        8,
        "Music streaming"
    ),
    AppDrawerApp(
        "16",
        "Music",
        Icons.Default.QueueMusic,
        AppCategory.ENTERTAINMENT,
        5,
        "Music player"
    ),
    AppDrawerApp(
        "17",
        "Gallery",
        Icons.Default.Photo,
        AppCategory.ENTERTAINMENT,
        3,
        "Photo gallery"
    ),

    // Games (Very High Point Cost)
    AppDrawerApp(
        "18",
        "Games",
        Icons.Default.SportsEsports,
        AppCategory.GAMES,
        50,
        "Mobile gaming"
    ),

    // Shopping Apps (Medium Point Cost)
    AppDrawerApp(
        "19",
        "Shopping",
        Icons.Default.ShoppingCart,
        AppCategory.SHOPPING,
        12,
        "Online shopping"
    ),
    AppDrawerApp(
        "20",
        "Food Delivery",
        Icons.Default.Restaurant,
        AppCategory.SHOPPING,
        10,
        "Food ordering"
    ),

    // Additional Apps
    AppDrawerApp("21", "Chrome", Icons.Default.Public, AppCategory.PRODUCTIVITY, 3, "Web browsing"),
    AppDrawerApp(
        "22",
        "Settings",
        Icons.Default.Settings,
        AppCategory.ESSENTIAL,
        0,
        "System settings"
    )
)

sealed class AppDrawerEvent {
    data class UpdateCountdown(val countdown: Int) : AppDrawerEvent()
    data object UnlockDrawer : AppDrawerEvent()
    data class SelectApp(val app: AppDrawerApp) : AppDrawerEvent()
    data object ShowWarning : AppDrawerEvent()
    data object HideWarning : AppDrawerEvent()
    data object ConfirmAppOpen : AppDrawerEvent()
    data object CloseDrawer : AppDrawerEvent()
}

data class AppDrawerTheme(
    val accent: Color = Color.White,
    val border: Color = Color.White.copy(alpha = 0.2f),
    val background: Color = Color(0xFF1F1F1F)
)