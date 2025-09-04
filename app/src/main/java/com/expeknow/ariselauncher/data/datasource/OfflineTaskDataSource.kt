package com.expeknow.ariselauncher.data.datasource

import com.expeknow.ariselauncher.data.database.TaskDao
import com.expeknow.ariselauncher.data.datasource.interfaces.TaskDataSource
import com.expeknow.ariselauncher.data.model.Task
import com.expeknow.ariselauncher.data.model.TaskCategory
import kotlinx.coroutines.flow.Flow

class OfflineTaskDataSource(
    private val taskDao: TaskDao
) : TaskDataSource {

    override fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()
    override fun getActiveTasks(): Flow<List<Task>> = taskDao.getActiveTasks()
    override fun getCompletedTasks(): Flow<List<Task>> = taskDao.getCompletedTasks()
    override fun getTasksByCategory(category: TaskCategory): Flow<List<Task>> =
        taskDao.getTasksByCategory(category)

    override suspend fun getTaskById(taskId: String): Task? = taskDao.getTaskById(taskId)
    override suspend fun insertTask(task: Task) = taskDao.insertTask(task)
    override suspend fun updateTask(task: Task) = taskDao.updateTask(task)
    override suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)
    override suspend fun deleteTaskById(taskId: String) = taskDao.deleteTaskById(taskId)

    override suspend fun markTaskAsCompleted(taskId: String, completedAt: Long) =
        taskDao.markTaskAsCompleted(taskId, completedAt)

    override suspend fun markTaskAsIncomplete(taskId: String) =
        taskDao.markTaskAsIncomplete(taskId)

    override fun getActiveTaskCount(): Flow<Int> = taskDao.getActiveTaskCount()
    override fun getCompletedTaskCount(): Flow<Int> = taskDao.getCompletedTaskCount()
}
