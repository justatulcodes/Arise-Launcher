package com.expeknow.ariselauncher.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.UUID

@Entity(tableName = "task_links")
data class TaskLink(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val url: String,
    val title: String,
    val type: TaskLinkType,
    val thumbnail: String? = null,
    val description: String? = null
)

enum class TaskLinkType {
    ARTICLE, VIDEO, LINK
}

enum class TaskCategory {
    // Original categories for focused development
    BECOMING_INTELLIGENT, BECOMING_MUSCULAR, BECOMING_RICH, MISCELLANEOUS,

    // Additional categories for UI compatibility
    INTELLIGENCE, PHYSICAL, WEALTH,
    PERSONAL, WORK, URGENT, IMPORTANT
}

@Entity(tableName = "tasks")
@TypeConverters(TaskConverters::class)
data class Task(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val points: Int = 0,
    val category: TaskCategory = TaskCategory.MISCELLANEOUS,
    val priority: Int = 1,
    val relatedLinks: List<TaskLink> = emptyList(),
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)

class TaskConverters {
    @TypeConverter
    fun fromTaskLinkList(value: List<TaskLink>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toTaskLinkList(value: String): List<TaskLink> {
        val listType = object : TypeToken<List<TaskLink>>() {}.type
        return Gson().fromJson(value, listType)
    }
}