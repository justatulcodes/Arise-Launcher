package com.expeknow.ariselauncher.data.datasource

import com.expeknow.ariselauncher.data.database.PointsLogDao
import com.expeknow.ariselauncher.data.datasource.interfaces.PointsLogDataSource
import com.expeknow.ariselauncher.data.model.PointsLog
import com.expeknow.ariselauncher.data.model.PointsLogType
import com.expeknow.ariselauncher.data.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OfflinePointsLogDataSource(
    private val pointsDao: PointsLogDao
) : PointsLogDataSource {

    override fun getAllPointsLog(): Flow<List<PointsLog>> = pointsDao.getAllPointsLog()
    override suspend fun getPointsLogById(logId: String): PointsLog? = pointsDao.getPointsLogById(logId)
    override suspend fun insertPointsLog(pointsLog: PointsLog) = pointsDao.insertPointsLog(pointsLog)

    override suspend fun insertPointsLogWithTask(task: Task) {
        val pointsLog = PointsLog(
            taskId = task.id,
            taskName = task.title,
            type = PointsLogType.EARNED,
            points = task.points
        )
        pointsDao.insertPointsLog(pointsLog)
    }

    override suspend fun insertPointsLogs(pointsLogs: List<PointsLog>) =
        pointsDao.insertPointsLogs(pointsLogs)

    override suspend fun updatePointsLog(pointsLog: PointsLog) =
        pointsDao.updatePointsLog(pointsLog)

    override suspend fun deletePointsLog(pointsLog: PointsLog) =
        pointsDao.deletePointsLog(pointsLog)

    override suspend fun deletePointsLogById(logId: String) =
        pointsDao.deletePointsLogById(logId)

    override fun getPointsLogByType(type: PointsLogType): Flow<List<PointsLog>> =
        pointsDao.getPointsLogByType(type)

    override fun getPointsLogFrom(fromTime: Long): Flow<List<PointsLog>> =
        pointsDao.getPointsLogFrom(fromTime)

    override fun getPointsLogUntil(toTime: Long): Flow<List<PointsLog>> =
        pointsDao.getPointsLogUntil(toTime)

    override fun getRecentPointsLog(limit: Int): Flow<List<PointsLog>> =
        pointsDao.getRecentPointsLog(limit)

    override fun getRecentPointsLogByType(type: PointsLogType, limit: Int): Flow<List<PointsLog>> =
        pointsDao.getRecentPointsLogByType(type, limit)

    override fun getTotalPointsEarned(): Flow<Int?> = pointsDao.getTotalPointsEarned()
    override fun getTotalPointsSpent(): Flow<Int?> = pointsDao.getTotalPointsSpent()
    override fun getCurrentPointsBalance(): Flow<Int?> = pointsDao.getCurrentPointsBalance()
    override fun getTotalPointsLogCount(): Flow<Int> = pointsDao.getTotalPointsLogCount()
    override fun getPointsLogCountByType(type: PointsLogType): Flow<Int> =
        pointsDao.getPointsLogCountByType(type)

    override suspend fun earnPoints(amount: Int, taskId: String, taskName: String) {
        val pointsLog = PointsLog(taskId = taskId, taskName = taskName, type = PointsLogType.EARNED, points = amount)
        pointsDao.insertPointsLog(pointsLog)
    }

    override suspend fun spendPoints(amount: Int, taskId: String, taskName: String) {
        val pointsLog = PointsLog(taskId = taskId, taskName = taskName, type = PointsLogType.SPENT, points = amount)
        pointsDao.insertPointsLog(pointsLog)
    }

    override fun getAvailablePoints(): Flow<Int> =
        getCurrentPointsBalance().map { it ?: 0 }

    override suspend fun resetPointsLog() {
        pointsDao.resetAllPointsLog()
    }
}
