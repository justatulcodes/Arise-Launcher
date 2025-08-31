package com.expeknow.ariselauncher.data.datasource

import com.expeknow.ariselauncher.data.database.TaskDao
import com.expeknow.ariselauncher.data.database.TaskLinkDao
import com.expeknow.ariselauncher.data.model.Task
import com.expeknow.ariselauncher.data.model.TaskCategory
import com.expeknow.ariselauncher.data.model.TaskLink
import com.expeknow.ariselauncher.data.model.TaskLinkType
import kotlinx.coroutines.flow.Flow

class OfflineDataSource(
    private val taskDao: TaskDao,
    private val taskLinkDao: TaskLinkDao
) {

    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()

    fun getActiveTasks(): Flow<List<Task>> = taskDao.getActiveTasks()

    fun getCompletedTasks(): Flow<List<Task>> = taskDao.getCompletedTasks()

    fun getTasksByCategory(category: TaskCategory): Flow<List<Task>> =
        taskDao.getTasksByCategory(category)

    suspend fun getTaskById(taskId: String): Task? = taskDao.getTaskById(taskId)

    suspend fun insertTask(task: Task) = taskDao.insertTask(task)

    suspend fun insertTasks(tasks: List<Task>) = taskDao.insertTasks(tasks)

    suspend fun updateTask(task: Task) = taskDao.updateTask(task)

    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    suspend fun deleteTaskById(taskId: String) = taskDao.deleteTaskById(taskId)

    suspend fun markTaskAsCompleted(
        taskId: String,
        completedAt: Long = System.currentTimeMillis()
    ) =
        taskDao.markTaskAsCompleted(taskId, completedAt)

    suspend fun markTaskAsIncomplete(taskId: String) = taskDao.markTaskAsIncomplete(taskId)

    suspend fun deleteAllTasks() = taskDao.deleteAllTasks()

    fun getActiveTaskCount(): Flow<Int> = taskDao.getActiveTaskCount()

    fun getCompletedTaskCount(): Flow<Int> = taskDao.getCompletedTaskCount()

    fun getTotalPointsEarned(): Flow<Int?> = taskDao.getTotalPointsEarned()

    // TaskLink operations
    fun getAllTaskLinks(): Flow<List<TaskLink>> = taskLinkDao.getAllTaskLinks()

    fun getTaskLinksByType(type: TaskLinkType): Flow<List<TaskLink>> =
        taskLinkDao.getTaskLinksByType(type)

    suspend fun getTaskLinkById(linkId: String): TaskLink? = taskLinkDao.getTaskLinkById(linkId)

    suspend fun insertTaskLink(taskLink: TaskLink) = taskLinkDao.insertTaskLink(taskLink)

    suspend fun insertTaskLinks(taskLinks: List<TaskLink>) = taskLinkDao.insertTaskLinks(taskLinks)

    suspend fun updateTaskLink(taskLink: TaskLink) = taskLinkDao.updateTaskLink(taskLink)

    suspend fun deleteTaskLink(taskLink: TaskLink) = taskLinkDao.deleteTaskLink(taskLink)

    suspend fun deleteTaskLinkById(linkId: String) = taskLinkDao.deleteTaskLinkById(linkId)

    suspend fun deleteAllTaskLinks() = taskLinkDao.deleteAllTaskLinks()
}