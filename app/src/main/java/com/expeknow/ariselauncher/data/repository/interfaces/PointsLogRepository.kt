package com.expeknow.ariselauncher.data.repository.interfaces

import com.expeknow.ariselauncher.data.model.PointActivity
import com.expeknow.ariselauncher.data.model.PointsHistory
import com.expeknow.ariselauncher.data.model.PointsLog
import com.expeknow.ariselauncher.data.model.PointsLogType
import com.expeknow.ariselauncher.data.model.Task
import kotlinx.coroutines.flow.Flow

interface PointsLogRepository {

    // Basic CRUD operations
    fun getAllPointsLog(): Flow<List<PointsLog>>

    suspend fun getPointsLogById(logId: String): PointsLog?

    suspend fun insertPointsLog(pointsLog: PointsLog)

    suspend fun insertPointsLogs(pointsLogs: List<PointsLog>)

    suspend fun insertPointsLogWithTask(task: Task)

    suspend fun updatePointsLog(pointsLog: PointsLog)

    suspend fun deletePointsLog(pointsLog: PointsLog)

    suspend fun deletePointsLogById(logId: String)

    suspend fun resetAllPointsLog()

    // Query operations
    fun getPointsLogByType(type: PointsLogType): Flow<List<PointsLog>>

    fun getPointsLogFrom(fromTime: Long): Flow<List<PointsLog>>

    fun getPointsLogUntil(toTime: Long): Flow<List<PointsLog>>

    fun getRecentPointsLog(limit: Int): Flow<List<PointsLog>>

    fun getRecentPointsLogByType(type: PointsLogType, limit: Int): Flow<List<PointsLog>>

    // Points calculations
    fun getTotalPointsEarned(): Flow<Int?>

    fun getTotalPointsSpent(): Flow<Int?>

    fun getCurrentPointsBalance(): Flow<Int?>

    fun getAvailablePoints(): Flow<Int>

    // Count queries
    fun getTotalPointsLogCount(): Flow<Int>

    fun getPointsLogCountByType(type: PointsLogType): Flow<Int>

    suspend fun earnPoints(amount: Int, taskId: String, taskName: String)

    suspend fun spendPoints(amount: Int, taskId: String, taskName: String)

}