package com.expeknow.ariselauncher.data.repository

import com.expeknow.ariselauncher.data.database.dao.DriveItemDao
import com.expeknow.ariselauncher.data.model.DriveItem
import com.expeknow.ariselauncher.data.model.DriveItemType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DriveRepository @Inject constructor(
    private val driveItemDao: DriveItemDao
) {
    fun getAllDriveItems(): Flow<List<DriveItem>> = driveItemDao.getAllDriveItems()

    fun getDriveItemsByType(type: DriveItemType): Flow<List<DriveItem>> =
        driveItemDao.getDriveItemsByType(type)

    suspend fun getDriveItemById(itemId: String): DriveItem? =
        driveItemDao.getDriveItemById(itemId)

    suspend fun insertDriveItem(item: DriveItem) = driveItemDao.insertDriveItem(item)

    suspend fun updateDriveItem(item: DriveItem) = driveItemDao.updateDriveItem(item)

    suspend fun deleteDriveItem(item: DriveItem) = driveItemDao.deleteDriveItem(item)

    suspend fun deleteDriveItemById(itemId: String) = driveItemDao.deleteDriveItemById(itemId)
}

