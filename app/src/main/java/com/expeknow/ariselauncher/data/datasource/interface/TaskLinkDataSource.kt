package com.expeknow.ariselauncher.data.datasource.`interface`

import com.expeknow.ariselauncher.data.model.TaskLink
import com.expeknow.ariselauncher.data.model.TaskLinkType
import kotlinx.coroutines.flow.Flow

interface TaskLinkDataSource {
    fun getAllTaskLinks(): Flow<List<TaskLink>>
    fun getTaskLinksByType(type: TaskLinkType): Flow<List<TaskLink>>
    suspend fun getTaskLinkById(linkId: String): TaskLink?
    suspend fun insertTaskLink(taskLink: TaskLink)
    suspend fun insertTaskLinks(taskLinks: List<TaskLink>)
    suspend fun updateTaskLink(taskLink: TaskLink)
    suspend fun deleteTaskLink(taskLink: TaskLink)
    suspend fun deleteTaskLinkById(linkId: String)
}
