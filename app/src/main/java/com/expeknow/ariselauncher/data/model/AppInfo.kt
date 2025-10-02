package com.expeknow.ariselauncher.data.model

import android.graphics.drawable.Drawable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "app_info")
data class AppInfo(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val packageName: String,
    val category: String,
    val installTime: Long
) 