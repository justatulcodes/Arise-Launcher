package com.expeknow.ariselauncher.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class DriveItemType {
    QUOTE,
    IMAGE,
    VIDEO
}

@Entity(tableName = "drive_items")
data class DriveItem(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val type: DriveItemType,
    val content: String,
    val title: String = "",
    val author: String = "",
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val orderIndex: Int = 0
)

