package com.expeknow.ariselauncher.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
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
    PEOPLE, OPPORTUNITY, SKILLS, PERSONAL
}

@Entity(tableName = "tasks")
@TypeConverters(ModelTypeConverters::class)
data class Task(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val points: Int = 0,
    val category: TaskCategory = TaskCategory.PERSONAL,
    val priority: Int = 1,
    val relatedLinks: List<TaskLink> = emptyList(),
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val isRepeated : Boolean,
    val repeatDays: List<DaysOfWeek> = emptyList()
)

@Entity(tableName = "points_log")
data class PointsLog(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val taskId: String,
    val taskName : String,
    val type: PointsLogType,
    val points: Float,
    val timestamp: Long = System.currentTimeMillis()
)
enum class DaysOfWeek {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}

enum class PointsLogType {
    EARNED, SPENT
}
