package com.expeknow.ariselauncher.data.repository

import androidx.compose.runtime.mutableStateListOf
import com.expeknow.ariselauncher.data.model.Task

class TaskRepository {
    private val tasks = mutableStateListOf<Task>()

    fun getAllTasks(): List<Task> {
        return tasks.filter { !it.isCompleted }
    }

    fun addTask(title: String, description: String = "", points: Int = 0): Task {
        val task = Task(title = title, description = description, points = points)
        tasks.add(task)
        return task
    }

    fun completeTask(taskId: String) {
        val index = tasks.indexOfFirst { it.id == taskId }
        if (index != -1) {
            val task = tasks[index]
            tasks[index] = task.copy(isCompleted = true)
        }
    }
}