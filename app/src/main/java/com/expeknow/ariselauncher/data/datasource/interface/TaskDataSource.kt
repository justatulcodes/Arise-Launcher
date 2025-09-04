package com.expeknow.ariselauncher.data.datasource.`interface`

import com.expeknow.ariselauncher.data.model.Task
import com.expeknow.ariselauncher.data.model.TaskCategory
import kotlinx.coroutines.flow.Flow

interface TaskDataSource {
    fun getAllTasks(): Flow<List<Task>>
    fun getActiveTasks(): Flow<List<Task>>
    fun getCompletedTasks(): Flow<List<Task>>
    fun getTasksByCategory(category: TaskCategory): Flow<List<Task>>
    suspend fun getTaskById(taskId: String): Task?
    suspend fun insertTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
    suspend fun deleteTaskById(taskId: String)
    suspend fun markTaskAsCompleted(taskId: String, completedAt: Long = System.currentTimeMillis())
    suspend fun markTaskAsIncomplete(taskId: String)
    fun getActiveTaskCount(): Flow<Int>
    fun getCompletedTaskCount(): Flow<Int>
}
