package com.expeknow.ariselauncher.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "targets")
data class Targets(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val endDate: Long,
    val progress: Float,
    val createdAt: Long,
    val showOnHomeScreen: Boolean = false
)
