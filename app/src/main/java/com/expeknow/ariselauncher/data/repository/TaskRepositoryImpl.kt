package com.expeknow.ariselauncher.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.expeknow.ariselauncher.data.datasource.OfflineDataSource
import com.expeknow.ariselauncher.data.model.Task
import com.expeknow.ariselauncher.data.model.TaskCategory
import com.expeknow.ariselauncher.data.model.ActivityType
import com.expeknow.ariselauncher.data.model.PointActivity
import com.expeknow.ariselauncher.data.model.PointsHistory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class TaskRepositoryImpl(
    private val offlineDataSource: OfflineDataSource,
) {


    fun getAllTasks(): Flow<List<Task>> = offlineDataSource.getAllTasks()

    fun getActiveTasks(): Flow<List<Task>> = offlineDataSource.getActiveTasks()

    fun getCompletedTasks(): Flow<List<Task>> = offlineDataSource.getCompletedTasks()

    fun getTasksByCategory(category: TaskCategory): Flow<List<Task>> =
        offlineDataSource.getTasksByCategory(category)

    suspend fun getTaskById(taskId: String): Task? = offlineDataSource.getTaskById(taskId)

    suspend fun addTask(
        title: String,
        description: String = "",
        points: Int = 0,
        category: TaskCategory = TaskCategory.MISCELLANEOUS,
        priority: Int = 1
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

    suspend fun insertTask(task: Task) = offlineDataSource.insertTask(task)

    suspend fun updateTask(task: Task) = offlineDataSource.updateTask(task)

    suspend fun deleteTask(task: Task) = offlineDataSource.deleteTask(task)

    suspend fun deleteTaskById(taskId: String) = offlineDataSource.deleteTaskById(taskId)

    suspend fun completeTask(taskId: String) {
        val task = getTaskById(taskId)
        offlineDataSource.markTaskAsCompleted(taskId)
        if(task != null)
        offlineDataSource.insertPointsLogWithTask(task)

    }

    suspend fun uncompleteTask(taskId: String) =
        offlineDataSource.markTaskAsIncomplete(taskId)

    fun getActiveTaskCount(): Flow<Int> = offlineDataSource.getActiveTaskCount()

    fun getCompletedTaskCount(): Flow<Int> = offlineDataSource.getCompletedTaskCount()

}