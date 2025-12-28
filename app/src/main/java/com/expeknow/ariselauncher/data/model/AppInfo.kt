package com.expeknow.ariselauncher.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "app_info")
data class AppInfo(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val packageName: String,
    val category: String,
    val installTime: Long,
    val launchCount : Int = 0,
    val lastUsedTimestamp : Long = 0,
    val totalTimeSpent : Long = 0,
    val name: String = "",
    val pointCost: Float = 1.0f
)
