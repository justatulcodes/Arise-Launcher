package com.expeknow.ariselauncher.data.database.dao

import androidx.room.*
import com.expeknow.ariselauncher.data.model.DriveItem
import com.expeknow.ariselauncher.data.model.DriveItemType
import kotlinx.coroutines.flow.Flow

@Dao
interface DriveItemDao {

    @Query("SELECT * FROM drive_items ORDER BY orderIndex ASC, createdAt DESC")
    fun getAllDriveItems(): Flow<List<DriveItem>>

    @Query("SELECT * FROM drive_items WHERE type = :type ORDER BY orderIndex ASC, createdAt DESC")
    fun getDriveItemsByType(type: DriveItemType): Flow<List<DriveItem>>

    @Query("SELECT * FROM drive_items WHERE id = :itemId")
    suspend fun getDriveItemById(itemId: String): DriveItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDriveItem(item: DriveItem)

    @Update
    suspend fun updateDriveItem(item: DriveItem)

    @Delete
    suspend fun deleteDriveItem(item: DriveItem)

    @Query("DELETE FROM drive_items WHERE id = :itemId")
    suspend fun deleteDriveItemById(itemId: String)

    @Query("DELETE FROM drive_items")
    suspend fun deleteAllDriveItems()
}

