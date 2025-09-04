package com.expeknow.ariselauncher.data.database

import androidx.room.*
import com.expeknow.ariselauncher.data.model.PointsLog
import com.expeknow.ariselauncher.data.model.PointsLogType
import kotlinx.coroutines.flow.Flow

@Dao
interface PointsLogDao {

    // Basic CRUD operations
    @Query("SELECT * FROM points_log ORDER BY timestamp DESC")
    fun getAllPointsLog(): Flow<List<PointsLog>>

    @Query("SELECT * FROM points_log WHERE id = :logId")
    suspend fun getPointsLogById(logId: String): PointsLog?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPointsLog(pointsLog: PointsLog)

    //can be used when user tries to be smart and use two apps using split screen
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPointsLogs(pointsLogs: List<PointsLog>)

    @Update
    suspend fun updatePointsLog(pointsLog: PointsLog)

    @Delete
    suspend fun deletePointsLog(pointsLog: PointsLog)

    @Query("DELETE FROM points_log WHERE id = :logId")
    suspend fun deletePointsLogById(logId: String)


    @Query("SELECT * FROM points_log WHERE type = :type ORDER BY timestamp DESC")
    fun getPointsLogByType(type: PointsLogType): Flow<List<PointsLog>>

    @Query("SELECT * FROM points_log WHERE timestamp >= :fromTime ORDER BY timestamp DESC")
    fun getPointsLogFrom(fromTime: Long): Flow<List<PointsLog>>

    @Query("SELECT * FROM points_log WHERE timestamp <= :toTime ORDER BY timestamp DESC")
    fun getPointsLogUntil(toTime: Long): Flow<List<PointsLog>>

    @Query("SELECT SUM(points) FROM points_log WHERE type = 'EARNED'")
    fun getTotalPointsEarned(): Flow<Int?>

    @Query("SELECT SUM(points) FROM points_log WHERE type = 'SPENT'")
    fun getTotalPointsSpent(): Flow<Int?>

    @Query("SELECT (SELECT SUM(points) FROM points_log WHERE type = 'EARNED') - (SELECT SUM(points) FROM points_log WHERE type = 'SPENT')")
    fun getCurrentPointsBalance(): Flow<Int?>

    // Count queries
    @Query("SELECT COUNT(*) FROM points_log")
    fun getTotalPointsLogCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM points_log WHERE type = :type")
    fun getPointsLogCountByType(type: PointsLogType): Flow<Int>

    @Query("SELECT * FROM points_log ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentPointsLog(limit: Int): Flow<List<PointsLog>>

    @Query("SELECT * FROM points_log WHERE type = :type ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentPointsLogByType(type: PointsLogType, limit: Int): Flow<List<PointsLog>>

}