package com.expeknow.ariselauncher.data.repository.interfaces

import android.content.Context
import android.net.Uri
import com.expeknow.ariselauncher.data.model.DriveItem
import com.expeknow.ariselauncher.data.model.DriveItemType
import kotlinx.coroutines.flow.Flow

interface DriveRepository {
    fun getAllDriveItems(): Flow<List<DriveItem>>
    fun getDriveItemsByType(type: DriveItemType): Flow<List<DriveItem>>
    suspend fun getDriveItemById(itemId: String): DriveItem?
    suspend fun insertDriveItem(item: DriveItem)
    suspend fun updateDriveItem(item: DriveItem)
    suspend fun deleteDriveItem(item: DriveItem)
    suspend fun deleteDriveItemById(itemId: String)
    suspend fun deleteAllDriveItems()

    fun openVideo(context: Context, videoUrl: String)
    suspend fun saveImageFromUri(context: Context, uri: Uri): String?
}

