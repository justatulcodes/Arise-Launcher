package com.expeknow.ariselauncher.data.database

import androidx.room.*
import com.expeknow.ariselauncher.data.model.Task
import com.expeknow.ariselauncher.data.model.TaskCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY priority DESC, createdAt DESC")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY priority DESC, createdAt DESC")
    fun getActiveTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 1 ORDER BY completedAt DESC")
    fun getCompletedTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE category = :category ORDER BY priority DESC, createdAt DESC")
    fun getTasksByCategory(category: TaskCategory): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: String): Task?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: String)

    @Query("UPDATE tasks SET isCompleted = 1, completedAt = :completedAt WHERE id = :taskId")
    suspend fun markTaskAsCompleted(taskId: String, completedAt: Long = System.currentTimeMillis())

    @Query("UPDATE tasks SET isCompleted = 0, completedAt = NULL WHERE id = :taskId")
    suspend fun markTaskAsIncomplete(taskId: String)

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 0")
    fun getActiveTaskCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 1")
    fun getCompletedTaskCount(): Flow<Int>

}