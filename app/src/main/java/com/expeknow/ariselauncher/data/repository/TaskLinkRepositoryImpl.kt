package com.expeknow.ariselauncher.data.repository

import com.expeknow.ariselauncher.data.datasource.`interface`.TaskLinkDataSource
import com.expeknow.ariselauncher.data.model.TaskLink
import com.expeknow.ariselauncher.data.model.TaskLinkType
import com.expeknow.ariselauncher.data.repository.interfaces.TaskLinkRepository
import kotlinx.coroutines.flow.Flow

class TaskLinkRepositoryImpl(
    private val taskLinkDataSource: TaskLinkDataSource
) : TaskLinkRepository {

    override fun getAllTaskLinks(): Flow<List<TaskLink>> {
        return taskLinkDataSource.getAllTaskLinks()
    }

    override fun getTaskLinksByType(type: TaskLinkType): Flow<List<TaskLink>> {
        return taskLinkDataSource.getTaskLinksByType(type)
    }

    override suspend fun getTaskLinkById(linkId: String): TaskLink? {
        return taskLinkDataSource.getTaskLinkById(linkId)
    }

    override suspend fun insertTaskLink(taskLink: TaskLink) {
        taskLinkDataSource.insertTaskLink(taskLink)
    }

    override suspend fun insertTaskLinks(taskLinks: List<TaskLink>) {
        taskLinkDataSource.insertTaskLinks(taskLinks)
    }

    override suspend fun updateTaskLink(taskLink: TaskLink) {
        taskLinkDataSource.updateTaskLink(taskLink)
    }

    override suspend fun deleteTaskLink(taskLink: TaskLink) {
        taskLinkDataSource.deleteTaskLink(taskLink)
    }

    override suspend fun deleteTaskLinkById(linkId: String) {
        taskLinkDataSource.deleteTaskLinkById(linkId)
    }

}