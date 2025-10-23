package com.expeknow.ariselauncher.data.datasource.interfaces

import com.expeknow.ariselauncher.data.model.DriveItem
import com.expeknow.ariselauncher.data.model.DriveItemType
import kotlinx.coroutines.flow.Flow

interface DriveItemDataSource {
    fun getAllDriveItems(): Flow<List<DriveItem>>
    fun getDriveItemsByType(type: DriveItemType): Flow<List<DriveItem>>
    suspend fun getDriveItemById(itemId: String): DriveItem?
    suspend fun insertDriveItem(item: DriveItem)
    suspend fun updateDriveItem(item: DriveItem)
    suspend fun deleteDriveItem(item: DriveItem)
    suspend fun deleteDriveItemById(itemId: String)
    suspend fun deleteAllDriveItems()
}

