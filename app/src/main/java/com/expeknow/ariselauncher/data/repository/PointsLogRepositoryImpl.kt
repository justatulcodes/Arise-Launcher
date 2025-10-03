package com.expeknow.ariselauncher.data.repository

import com.expeknow.ariselauncher.data.datasource.interfaces.PointsLogDataSource
import com.expeknow.ariselauncher.data.model.PointsLog
import com.expeknow.ariselauncher.data.model.PointsLogType
import com.expeknow.ariselauncher.data.model.Task
import com.expeknow.ariselauncher.data.repository.interfaces.PointsLogRepository
import kotlinx.coroutines.flow.Flow

class PointsLogRepositoryImpl(
    private val pointsLogDataSource: PointsLogDataSource
) : PointsLogRepository {
    
    override fun getAllPointsLog(): Flow<List<PointsLog>> {
        return pointsLogDataSource.getAllPointsLog()
    }

    override suspend fun getPointsLogById(logId: String): PointsLog? {
        return pointsLogDataSource.getPointsLogById(logId)
    }

    override suspend fun insertPointsLog(pointsLog: PointsLog) {
        pointsLogDataSource.insertPointsLog(pointsLog)
    }

    override suspend fun insertPointsLogWithTask(task: Task) {
        val pointsLog = PointsLog(
            type = PointsLogType.EARNED,
            points = task.points,
            taskId = task.id,
            taskName = task.title
        )
        pointsLogDataSource.insertPointsLog(pointsLog)
    }

    override suspend fun insertPointsLogs(pointsLogs: List<PointsLog>) {
        pointsLogDataSource.insertPointsLogs(pointsLogs)
    }

    override suspend fun updatePointsLog(pointsLog: PointsLog) {
        pointsLogDataSource.updatePointsLog(pointsLog)
    }

    override suspend fun deletePointsLog(pointsLog: PointsLog) {
        pointsLogDataSource.deletePointsLog(pointsLog)
    }

    override suspend fun deletePointsLogById(logId: String) {
        pointsLogDataSource.deletePointsLogById(logId)
    }

    override suspend fun resetAllPointsLog() {
        pointsLogDataSource.resetPointsLog()
    }

    override fun getPointsLogByType(type: PointsLogType): Flow<List<PointsLog>> {
        return pointsLogDataSource.getPointsLogByType(type)
    }

    override fun getPointsLogFrom(fromTime: Long): Flow<List<PointsLog>> {
        return pointsLogDataSource.getPointsLogFrom(fromTime)
    }

    override fun getPointsLogUntil(toTime: Long): Flow<List<PointsLog>> {
        return pointsLogDataSource.getPointsLogUntil(toTime)
    }

    override fun getRecentPointsLog(limit: Int): Flow<List<PointsLog>> {
        return pointsLogDataSource.getRecentPointsLog(limit)
    }

    override fun getRecentPointsLogByType(
        type: PointsLogType,
        limit: Int
    ): Flow<List<PointsLog>> {
        return pointsLogDataSource.getRecentPointsLogByType(type, limit)
    }

    override fun getTotalPointsEarned(): Flow<Int?> {
        return pointsLogDataSource.getTotalPointsEarned()
    }

    override fun getTotalPointsSpent(): Flow<Int?> {
        return pointsLogDataSource.getTotalPointsSpent()
    }

    override fun getCurrentPointsBalance(): Flow<Int?> {
        return pointsLogDataSource.getCurrentPointsBalance()
    }

    override fun getAvailablePoints(): Flow<Int> {
        return pointsLogDataSource.getAvailablePoints()
    }

    override fun getTotalPointsLogCount(): Flow<Int> {
        return pointsLogDataSource.getTotalPointsLogCount()
    }

    override fun getPointsLogCountByType(type: PointsLogType): Flow<Int> {
        return pointsLogDataSource.getPointsLogCountByType(type)
    }

    override suspend fun earnPoints(
        amount: Int,
        taskId: String,
        taskName: String
    ) {
        pointsLogDataSource.earnPoints(amount, taskId, taskName)
    }

    override suspend fun spendPoints(
        amount: Int,
        taskId: String,
        taskName: String
    ) {
        pointsLogDataSource.spendPoints(amount, taskId, taskName)
    }
}