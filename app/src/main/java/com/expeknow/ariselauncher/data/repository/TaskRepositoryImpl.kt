package com.expeknow.ariselauncher.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.expeknow.ariselauncher.data.datasource.`interface`.TaskDataSource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.expeknow.ariselauncher.data.model.Task
import com.expeknow.ariselauncher.data.model.TaskCategory
import com.expeknow.ariselauncher.data.model.ActivityType
import com.expeknow.ariselauncher.data.model.PointActivity
import com.expeknow.ariselauncher.data.model.PointsHistory
import com.expeknow.ariselauncher.data.repository.interfaces.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class TaskRepositoryImpl(
    private val offlineDataSource: TaskDataSource,
) : TaskRepository
{

    override fun getAllTasks(): Flow<List<Task>> = offlineDataSource.getAllTasks()

    override fun getActiveTasks(): Flow<List<Task>> = offlineDataSource.getActiveTasks()

    override fun getCompletedTasks(): Flow<List<Task>> = offlineDataSource.getCompletedTasks()

    override fun getTasksByCategory(category: TaskCategory): Flow<List<Task>> =
        offlineDataSource.getTasksByCategory(category)

    override suspend fun getTaskById(taskId: String): Task? = offlineDataSource.getTaskById(taskId)

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
        offlineDataSource.insertTask(task)
        return task
    }

    override suspend fun insertTask(task: Task) = offlineDataSource.insertTask(task)

    override suspend fun updateTask(task: Task) = offlineDataSource.updateTask(task)

    override suspend fun deleteTask(task: Task) = offlineDataSource.deleteTask(task)

    override suspend fun deleteTaskById(taskId: String) = offlineDataSource.deleteTaskById(taskId)

    override suspend fun completeTask(taskId: String) {
        val task = getTaskById(taskId)
        offlineDataSource.markTaskAsCompleted(taskId)
//        if(task != null)
//        offlineDataSource.insertPointsLogWithTask(task)

    }

    override suspend fun uncompleteTask(taskId: String) =
        offlineDataSource. markTaskAsIncomplete(taskId)

    override fun getActiveTaskCount(): Flow<Int> = offlineDataSource.getActiveTaskCount()

    override fun getCompletedTaskCount(): Flow<Int> = offlineDataSource.getCompletedTaskCount()

}