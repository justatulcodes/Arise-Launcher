package com.expeknow.ariselauncher.ui.screens.apps

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class AppDrawerApp(
    val id: String,
    val name: String,
    val packageName: String,
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
    val currentPoints: Int = 0,
    val apps: List<AppDrawerApp> = getDefaultApps()
)

fun getDefaultApps(): List<AppDrawerApp> = listOf(
    // Essential Apps
    AppDrawerApp(
        "1",
        "Phone",
        "com.google.android.dialer",
        Icons.Default.Phone,
        AppCategory.ESSENTIAL,
        0,
        "Make and receive calls"
    ),
    AppDrawerApp(
        "2",
        "Messages",
        "com.google.android.apps.messaging",
        Icons.Default.Message,
        AppCategory.ESSENTIAL,
        0,
        "Text messaging"
    ),
    AppDrawerApp(
        "3",
        "Mail",
        "com.google.android.gm",
        Icons.Default.Email,
        AppCategory.ESSENTIAL,
        0,
        "Email communication"
    ),
    AppDrawerApp(
        "4",
        "Calendar",
        "com.google.android.calendar",
        Icons.Default.CalendarToday,
        AppCategory.ESSENTIAL,
        0,
        "Schedule management"
    ),

    // Productivity Apps
    AppDrawerApp(
        "5",
        "Camera",
        "com.google.android.GoogleCamera",
        Icons.Default.PhotoCamera,
        AppCategory.PRODUCTIVITY,
        0,
        "Take photos and videos"
    ),
    AppDrawerApp(
        "6",
        "Maps",
        "com.google.android.apps.maps",
        Icons.Default.Place,
        AppCategory.PRODUCTIVITY,
        0,
        "Navigation and location"
    ),
    AppDrawerApp(
        "7",
        "Calculator",
        "com.google.android.calculator",
        Icons.Default.Calculate,
        AppCategory.PRODUCTIVITY,
        0,
        "Basic calculations"
    ),
    AppDrawerApp(
        "8",
        "Files",
        "com.marc.files",
        Icons.Default.InsertDriveFile,
        AppCategory.PRODUCTIVITY,
        0,
        "File management"
    ),
    AppDrawerApp(
        "9",
        "Notion",
        "notion.id",
        Icons.Default.Note,
        AppCategory.PRODUCTIVITY,
        0,
        "Quick notes"
    ),
    AppDrawerApp(
        "10",
        "Google Chat",
        "com.google.android.apps.dynamite",
        Icons.Default.Forum,
        AppCategory.PRODUCTIVITY,
        2,
        "Work communication"
    ),

    // Social Media Apps (High Point Cost)
    AppDrawerApp(
        "11",
        "Instagram",
        "com.instagram.android",
        Icons.Default.CameraAlt,
        AppCategory.SOCIAL,
        25,
        "Photo sharing social media"
    ),
    AppDrawerApp(
        "12",
        "Twitter",
        "com.twitter.android",
        Icons.Default.Chat,
        AppCategory.SOCIAL,
        20,
        "Microblogging platform"
    ),

    // Entertainment Apps (Medium Point Cost)
    AppDrawerApp(
        "14",
        "YouTube",
        "com.rvx.android.youtube",
        Icons.Default.VideoLibrary,
        AppCategory.ENTERTAINMENT,
        15,
        "Video streaming platform"
    ),
    AppDrawerApp(
        "15",
        "Music Streaming",
        "com.spotify.music",
        Icons.Default.MusicNote,
        AppCategory.ENTERTAINMENT,
        8,
        "Music streaming"
    ),
    AppDrawerApp(
        "16",
        "YT Music",
        "anddea.youtube.music",
        Icons.Default.QueueMusic,
        AppCategory.ENTERTAINMENT,
        5,
        "Music player"
    ),
    AppDrawerApp(
        "17",
        "Gallery",
        "com.google.android.apps.photos",
        Icons.Default.Photo,
        AppCategory.ENTERTAINMENT,
        3,
        "Photo gallery"
    ),

    // Additional Apps
    AppDrawerApp(
        "21",
        "Chrome",
        "com.android.chrome",
        Icons.Default.Public,
        AppCategory.PRODUCTIVITY,
        3,
        "Web browsing"
    ),
    AppDrawerApp(
        "22",
        "Settings",
        "com.android.settings",
        Icons.Default.Settings,
        AppCategory.ESSENTIAL,
        0,
        "System settings"
    )
)

sealed class AppDrawerEvent {
    data class UpdateCountdown(val countdown: Int) : AppDrawerEvent()
    data object UnlockDrawer : AppDrawerEvent()
    data class SelectApp(val app: AppDrawerApp, val context : Context) : AppDrawerEvent()
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