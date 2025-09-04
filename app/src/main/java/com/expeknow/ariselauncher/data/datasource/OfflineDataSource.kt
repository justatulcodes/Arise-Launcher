package com.expeknow.ariselauncher.data.datasource

import com.expeknow.ariselauncher.data.database.PointsLogDao
import com.expeknow.ariselauncher.data.database.TaskDao
import com.expeknow.ariselauncher.data.database.TaskLinkDao
import com.expeknow.ariselauncher.data.model.PointsLog
import com.expeknow.ariselauncher.data.model.PointsLogType
import com.expeknow.ariselauncher.data.model.Task
import com.expeknow.ariselauncher.data.model.TaskCategory
import com.expeknow.ariselauncher.data.model.TaskLink
import com.expeknow.ariselauncher.data.model.TaskLinkType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OfflineDataSource(
    private val taskDao: TaskDao,
    private val taskLinkDao: TaskLinkDao,
    private val pointsDao: PointsLogDao
) {

    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()

    fun getActiveTasks(): Flow<List<Task>> = taskDao.getActiveTasks()

    fun getCompletedTasks(): Flow<List<Task>> = taskDao.getCompletedTasks()

    fun getTasksByCategory(category: TaskCategory): Flow<List<Task>> =
        taskDao.getTasksByCategory(category)

    suspend fun getTaskById(taskId: String): Task? = taskDao.getTaskById(taskId)

    suspend fun insertTask(task: Task) = taskDao.insertTask(task)


    suspend fun updateTask(task: Task) = taskDao.updateTask(task)

    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    suspend fun deleteTaskById(taskId: String) = taskDao.deleteTaskById(taskId)

    suspend fun markTaskAsCompleted(
        taskId: String,
        completedAt: Long = System.currentTimeMillis()
    ) =
        taskDao.markTaskAsCompleted(taskId, completedAt)

    suspend fun markTaskAsIncomplete(taskId: String) = taskDao.markTaskAsIncomplete(taskId)

    fun getActiveTaskCount(): Flow<Int> = taskDao.getActiveTaskCount()

    fun getCompletedTaskCount(): Flow<Int> = taskDao.getCompletedTaskCount()

    fun getTotalPointsEarned(): Flow<Int?> = pointsDao.getTotalPointsEarned()

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

    // PointsLog operations
    fun getAllPointsLog(): Flow<List<PointsLog>> = pointsDao.getAllPointsLog()

    suspend fun getPointsLogById(logId: String): PointsLog? = pointsDao.getPointsLogById(logId)

    suspend fun insertPointsLog(pointsLog: PointsLog) = pointsDao.insertPointsLog(pointsLog)

    suspend fun insertPointsLogWithTask(task: Task) {
        val pointsLog = PointsLog(
            taskId = task.id,
            taskName = task.title,
            type = PointsLogType.EARNED,
            points = task.points
        )
        pointsDao.insertPointsLog(pointsLog)
    }

    suspend fun insertPointsLogs(pointsLogs: List<PointsLog>) =
        pointsDao.insertPointsLogs(pointsLogs)

    suspend fun updatePointsLog(pointsLog: PointsLog) = pointsDao.updatePointsLog(pointsLog)

    suspend fun deletePointsLog(pointsLog: PointsLog) = pointsDao.deletePointsLog(pointsLog)

    suspend fun deletePointsLogById(logId: String) = pointsDao.deletePointsLogById(logId)

    fun getPointsLogByType(type: PointsLogType): Flow<List<PointsLog>> =
        pointsDao.getPointsLogByType(type)

    fun getPointsLogFrom(fromTime: Long): Flow<List<PointsLog>> =
        pointsDao.getPointsLogFrom(fromTime)

    fun getPointsLogUntil(toTime: Long): Flow<List<PointsLog>> = pointsDao.getPointsLogUntil(toTime)

    fun getRecentPointsLog(limit: Int): Flow<List<PointsLog>> = pointsDao.getRecentPointsLog(limit)

    fun getRecentPointsLogByType(type: PointsLogType, limit: Int): Flow<List<PointsLog>> =
        pointsDao.getRecentPointsLogByType(type, limit)

    fun getTotalPointsSpent(): Flow<Int?> = pointsDao.getTotalPointsSpent()

    fun getCurrentPointsBalance(): Flow<Int?> = pointsDao.getCurrentPointsBalance()

    fun getTotalPointsLogCount(): Flow<Int> = pointsDao.getTotalPointsLogCount()

    fun getPointsLogCountByType(type: PointsLogType): Flow<Int> =
        pointsDao.getPointsLogCountByType(type)

    // Helper methods for points management
    suspend fun earnPoints(amount: Int, taskId: String, taskName: String) {
        val pointsLog = PointsLog(
            taskId = taskId,
            taskName = taskName,
            type = PointsLogType.EARNED,
            points = amount
        )
        pointsDao.insertPointsLog(pointsLog)
    }

    suspend fun spendPoints(amount: Int, taskId: String, taskName: String) {
        val pointsLog = PointsLog(
            taskId = taskId,
            taskName = taskName,
            type = PointsLogType.SPENT,
            points = amount
        )
        pointsDao.insertPointsLog(pointsLog)
    }

    fun getAvailablePoints(): Flow<Int> =
        getCurrentPointsBalance().map { it ?: 0 }

}