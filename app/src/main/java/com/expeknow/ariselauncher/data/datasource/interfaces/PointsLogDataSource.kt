package com.expeknow.ariselauncher.data.datasource.interfaces

import com.expeknow.ariselauncher.data.model.PointsLog
import com.expeknow.ariselauncher.data.model.PointsLogType
import com.expeknow.ariselauncher.data.model.Task
import kotlinx.coroutines.flow.Flow

interface PointsLogDataSource {
    fun getAllPointsLog(): Flow<List<PointsLog>>
    suspend fun getPointsLogById(logId: String): PointsLog?
    suspend fun insertPointsLog(pointsLog: PointsLog)
    suspend fun insertPointsLogWithTask(task: Task)
    suspend fun insertPointsLogs(pointsLogs: List<PointsLog>)
    suspend fun updatePointsLog(pointsLog: PointsLog)
    suspend fun deletePointsLog(pointsLog: PointsLog)
    suspend fun deletePointsLogById(logId: String)
    fun getPointsLogByType(type: PointsLogType): Flow<List<PointsLog>>
    fun getPointsLogFrom(fromTime: Long): Flow<List<PointsLog>>
    fun getPointsLogUntil(toTime: Long): Flow<List<PointsLog>>
    fun getRecentPointsLog(limit: Int): Flow<List<PointsLog>>
    fun getRecentPointsLogByType(type: PointsLogType, limit: Int): Flow<List<PointsLog>>
    fun getTotalPointsEarned(): Flow<Int?>
    fun getTotalPointsSpent(): Flow<Int?>
    fun getCurrentPointsBalance(): Flow<Int?>
    fun getTotalPointsLogCount(): Flow<Int>
    fun getPointsLogCountByType(type: PointsLogType): Flow<Int>
    suspend fun earnPoints(amount: Int, taskId: String, taskName: String)
    suspend fun spendPoints(amount: Int, taskId: String, taskName: String)
    fun getAvailablePoints(): Flow<Int>
    suspend fun resetPointsLog()
}
