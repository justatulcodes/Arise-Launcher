package com.expeknow.ariselauncher.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class DriveItemType {
    QUOTE,      // Text-based motivational quote
    IMAGE,      // Image with optional caption
    VIDEO       // Video URL (YouTube, etc.)
}

@Entity(tableName = "drive_items")
data class DriveItem(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val type: DriveItemType,
    val content: String,           // Quote text, image URL, or video URL
    val title: String = "",        // Title for images/videos
    val author: String = "",       // Author for quotes
    val description: String = "",  // Description for images/videos
    val createdAt: Long = System.currentTimeMillis(),
    val orderIndex: Int = 0        // For custom ordering
)

