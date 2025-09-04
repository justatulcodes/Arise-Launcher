package com.expeknow.ariselauncher.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.google.gson.annotations.Expose

data class PointActivity(
    val id: String,
    val type: ActivityType,
    val points: Int,
    val activity: String,
    val time: String,
    val iconType: String = "CheckCircle" // Serializable icon identifier
)

enum class ActivityType {
    EARN, BURN
}

fun getIconByType(iconType: String): ImageVector {
    return when (iconType) {
        "Smartphone" -> Icons.Filled.Smartphone
        "CheckCircle" -> Icons.Filled.CheckCircle
        else -> Icons.Filled.CheckCircle
    }
}

val PointActivity.icon: ImageVector
    get() = getIconByType(iconType)

data class Rank(
    val name: String,
    val minPoints: Int,
    val maxPoints: Int,
    val description: String,
    val icon: ImageVector,
    val colors: RankColors
)

data class RankColors(
    val accent: Color,
    val border: Color,
    val background: Color
)

data class TaskStats(
    val totalTasks: Int,
    val completedTasks: Int,
    val urgentTasks: Int,
    val workTasks: Int,
    val personalTasks: Int,
    val completionRatio: Float,
    val todayCompleted: Int,
    val weeklyAverage: Float
)

data class PointsHistory(
    val day: String,
    val points: Int
)

val ranks = listOf(
    Rank(
        name = "BRONZE NOVICE",
        minPoints = 0,
        maxPoints = 99,
        description = "Beginning your journey of discipline and focus",
        icon = Icons.Filled.Shield,
        colors = RankColors(
            accent = Color(0xFFD97706),
            border = Color(0x66D97706),
            background = Color(0x1AD97706)
        )
    ),
    Rank(
        name = "SILVER APPRENTICE",
        minPoints = 100,
        maxPoints = 249,
        description = "Building consistent habits and showing promise",
        icon = Icons.Filled.Star,
        colors = RankColors(
            accent = Color(0xFF9CA3AF),
            border = Color(0x669CA3AF),
            background = Color(0x1A9CA3AF)
        )
    ),
    Rank(
        name = "SHADOW MONARCH",
        minPoints = 250,
        maxPoints = 499,
        description = "Operating in the shadows, disciplined and focused",
        icon = Icons.Filled.Stars,
        colors = RankColors(
            accent = Color(0xFFA855F7),
            border = Color(0x66A855F7),
            background = Color(0x1AA855F7)
        )
    ),
    Rank(
        name = "ELITE STRATEGIST",
        minPoints = 500,
        maxPoints = 999,
        description = "Master of planning and execution",
        icon = Icons.Filled.Star,
        colors = RankColors(
            accent = Color(0xFF60A5FA),
            border = Color(0x6660A5FA),
            background = Color(0x1A60A5FA)
        )
    ),
    Rank(
        name = "DIAMOND EXECUTOR",
        minPoints = 1000,
        maxPoints = 1999,
        description = "Relentless in pursuit of excellence",
        icon = Icons.Filled.Shield,
        colors = RankColors(
            accent = Color(0xFF22D3EE),
            border = Color(0x6622D3EE),
            background = Color(0x1A22D3EE)
        )
    ),
    Rank(
        name = "GRANDMASTER SAGE",
        minPoints = 2000,
        maxPoints = 3999,
        description = "Wisdom through consistent action and discipline",
        icon = Icons.Filled.Stars,
        colors = RankColors(
            accent = Color(0xFF4ADE80),
            border = Color(0x664ADE80),
            background = Color(0x1A4ADE80)
        )
    ),
    Rank(
        name = "LEGEND OF DISCIPLINE",
        minPoints = 4000,
        maxPoints = 7999,
        description = "A living example of what discipline can achieve",
        icon = Icons.Filled.Star,
        colors = RankColors(
            accent = Color(0xFFFACC15),
            border = Color(0x66FACC15),
            background = Color(0x1AFACC15)
        )
    ),
    Rank(
        name = "THE APEX SOVEREIGN",
        minPoints = 8000,
        maxPoints = 15999,
        description = "Ruler of self, master of destiny",
        icon = Icons.Filled.Stars,
        colors = RankColors(
            accent = Color(0xFFF87171),
            border = Color(0x66F87171),
            background = Color(0x1AF87171)
        )
    ),
    Rank(
        name = "TRANSCENDENT BEING",
        minPoints = 16000,
        maxPoints = 999999,
        description = "Beyond mortal limitations of distraction and procrastination",
        icon = Icons.Filled.Star,
        colors = RankColors(
            accent = Color.White,
            border = Color(0x66FFFFFF),
            background = Color(0x1AFFFFFF)
        )
    )
)