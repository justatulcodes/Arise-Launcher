package com.expeknow.ariselauncher.data.datasource

import com.expeknow.ariselauncher.data.database.dao.DriveItemDao
import com.expeknow.ariselauncher.data.datasource.interfaces.DriveItemDataSource
import com.expeknow.ariselauncher.data.model.DriveItem
import com.expeknow.ariselauncher.data.model.DriveItemType
import kotlinx.coroutines.flow.Flow

class OfflineDriveItemDataSource(
    private val driveItemDao: DriveItemDao
) : DriveItemDataSource {

    override fun getAllDriveItems(): Flow<List<DriveItem>> = driveItemDao.getAllDriveItems()

    override fun getDriveItemsByType(type: DriveItemType): Flow<List<DriveItem>> =
        driveItemDao.getDriveItemsByType(type)

    override suspend fun getDriveItemById(itemId: String): DriveItem? =
        driveItemDao.getDriveItemById(itemId)

    override suspend fun insertDriveItem(item: DriveItem) =
        driveItemDao.insertDriveItem(item)

    override suspend fun updateDriveItem(item: DriveItem) =
        driveItemDao.updateDriveItem(item)

    override suspend fun deleteDriveItem(item: DriveItem) =
        driveItemDao.deleteDriveItem(item)

    override suspend fun deleteDriveItemById(itemId: String) =
        driveItemDao.deleteDriveItemById(itemId)

    override suspend fun deleteAllDriveItems() =
        driveItemDao.deleteAllDriveItems()
}

