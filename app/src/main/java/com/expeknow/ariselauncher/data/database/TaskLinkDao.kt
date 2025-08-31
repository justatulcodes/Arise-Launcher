package com.expeknow.ariselauncher.data.database

import androidx.room.*
import com.expeknow.ariselauncher.data.model.TaskLink
import com.expeknow.ariselauncher.data.model.TaskLinkType
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskLinkDao {

    @Query("SELECT * FROM task_links")
    fun getAllTaskLinks(): Flow<List<TaskLink>>

    @Query("SELECT * FROM task_links WHERE type = :type")
    fun getTaskLinksByType(type: TaskLinkType): Flow<List<TaskLink>>

    @Query("SELECT * FROM task_links WHERE id = :linkId")
    suspend fun getTaskLinkById(linkId: String): TaskLink?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskLink(taskLink: TaskLink)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskLinks(taskLinks: List<TaskLink>)

    @Update
    suspend fun updateTaskLink(taskLink: TaskLink)

    @Delete
    suspend fun deleteTaskLink(taskLink: TaskLink)

    @Query("DELETE FROM task_links WHERE id = :linkId")
    suspend fun deleteTaskLinkById(linkId: String)

    @Query("DELETE FROM task_links")
    suspend fun deleteAllTaskLinks()
}