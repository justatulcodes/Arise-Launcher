package com.expeknow.ariselauncher.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.expeknow.ariselauncher.ui.screens.targets.TargetsEvent
import kotlinx.coroutines.flow.Flow

@Dao
interface TargetsDao {

    @Query("SELECT * FROM targets")
    fun getAllTargets(): Flow<List<Target>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTarget(target: Target)

    @Update
    suspend fun updateTarget(target: Target)

    @Query("DELETE FROM targets WHERE id = :targetId")
    suspend fun deleteTarget(targetId: String)

    @Query("UPDATE targets SET progress = :progress WHERE id = :targetId")
    suspend fun updateTargetProgress(targetId: String, progress: Float)


}