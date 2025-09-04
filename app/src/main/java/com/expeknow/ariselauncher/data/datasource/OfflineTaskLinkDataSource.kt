package com.expeknow.ariselauncher.data.datasource

import com.expeknow.ariselauncher.data.database.TaskLinkDao
import com.expeknow.ariselauncher.data.datasource.`interface`.TaskLinkDataSource
import com.expeknow.ariselauncher.data.model.TaskLink
import com.expeknow.ariselauncher.data.model.TaskLinkType
import kotlinx.coroutines.flow.Flow

class OfflineTaskLinkDataSource(
    private val taskLinkDao: TaskLinkDao
) : TaskLinkDataSource {

    override fun getAllTaskLinks(): Flow<List<TaskLink>> = taskLinkDao.getAllTaskLinks()
    override fun getTaskLinksByType(type: TaskLinkType): Flow<List<TaskLink>> =
        taskLinkDao.getTaskLinksByType(type)

    override suspend fun getTaskLinkById(linkId: String): TaskLink? =
        taskLinkDao.getTaskLinkById(linkId)

    override suspend fun insertTaskLink(taskLink: TaskLink) =
        taskLinkDao.insertTaskLink(taskLink)

    override suspend fun insertTaskLinks(taskLinks: List<TaskLink>) =
        taskLinkDao.insertTaskLinks(taskLinks)

    override suspend fun updateTaskLink(taskLink: TaskLink) =
        taskLinkDao.updateTaskLink(taskLink)

    override suspend fun deleteTaskLink(taskLink: TaskLink) =
        taskLinkDao.deleteTaskLink(taskLink)

    override suspend fun deleteTaskLinkById(linkId: String) =
        taskLinkDao.deleteTaskLinkById(linkId)
}
