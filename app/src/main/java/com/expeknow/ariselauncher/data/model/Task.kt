package com.expeknow.ariselauncher.data.model

import java.util.UUID

data class TaskLink(
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
    URGENT, IMPORTANT, PERSONAL, WORK, INTELLIGENCE, PHYSICAL, WEALTH
}

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val points: Int = 0,
    val category: TaskCategory = TaskCategory.PERSONAL,
    val priority: Int = 1,
    val links: List<TaskLink> = emptyList(),
    val isCompleted: Boolean = false,
    // Legacy fields for backward compatibility
    val relatedArticle: String? = null,
    val relatedVideos: List<String> = emptyList(),
    val relatedMediaLinks: List<String> = emptyList()
)