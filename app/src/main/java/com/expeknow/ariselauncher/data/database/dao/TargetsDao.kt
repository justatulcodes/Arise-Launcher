package com.expeknow.ariselauncher.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.expeknow.ariselauncher.data.model.Targets
import kotlinx.coroutines.flow.Flow

@Dao
interface TargetsDao {

    @Query("SELECT * FROM targets")
    fun getAllTargets(): Flow<List<Targets>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTarget(target: Targets)

    @Update
    suspend fun updateTarget(target: Targets)

    @Query("DELETE FROM targets WHERE id = :targetId")
    suspend fun deleteTarget(targetId: String)

    @Query("UPDATE targets SET progress = :progress WHERE id = :targetId")
    suspend fun updateTargetProgress(targetId: String, progress: Float)


}