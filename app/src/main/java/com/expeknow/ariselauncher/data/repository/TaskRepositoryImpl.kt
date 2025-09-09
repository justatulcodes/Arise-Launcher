package com.expeknow.ariselauncher.data.repository

import com.expeknow.ariselauncher.data.datasource.interfaces.TaskDataSource
import com.expeknow.ariselauncher.data.model.Task
import com.expeknow.ariselauncher.data.model.TaskCategory
import com.expeknow.ariselauncher.data.repository.interfaces.TaskRepository
import kotlinx.coroutines.flow.Flow

class TaskRepositoryImpl(
    private val taskRepositoryDataSource: TaskDataSource,
) : TaskRepository
{

    override fun getAllTasks(): Flow<List<Task>> = taskRepositoryDataSource.getAllTasks()

    override fun getActiveTasks(): Flow<List<Task>> = taskRepositoryDataSource.getActiveTasks()

    override fun getCompletedTasks(): Flow<List<Task>> = taskRepositoryDataSource.getCompletedTasks()

    override fun getTasksByCategory(category: TaskCategory): Flow<List<Task>> =
        taskRepositoryDataSource.getTasksByCategory(category)

    override suspend fun getTaskById(taskId: String): Task? = taskRepositoryDataSource.getTaskById(taskId)

    override suspend fun addTask(
        title: String,
        description: String,
        points: Int,
        category: TaskCategory,
        priority: Int
    ): Task {
        val task = Task(
            title = title,
            description = description,
            points = points,
            category = category,
            priority = priority
        )
        taskRepositoryDataSource.insertTask(task)
        return task
    }

    override suspend fun insertTask(task: Task) = taskRepositoryDataSource.insertTask(task)

    override suspend fun updateTask(task: Task) = taskRepositoryDataSource.updateTask(task)

    override suspend fun deleteTask(task: Task) = taskRepositoryDataSource.deleteTask(task)

    override suspend fun deleteTaskById(taskId: String) = taskRepositoryDataSource.deleteTaskById(taskId)
    override suspend fun deleteAllTasks() {
        taskRepositoryDataSource.deleteAllTasks()
    }

    override suspend fun completeTask(taskId: String) {
        val task = getTaskById(taskId)
        taskRepositoryDataSource.markTaskAsCompleted(taskId)



    }

    override suspend fun uncompleteTask(taskId: String) =
        taskRepositoryDataSource. markTaskAsIncomplete(taskId)

    override fun getActiveTaskCount(): Flow<Int> = taskRepositoryDataSource.getActiveTaskCount()

    override fun getCompletedTaskCount(): Flow<Int> = taskRepositoryDataSource.getCompletedTaskCount()

}