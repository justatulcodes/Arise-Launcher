package com.expeknow.ariselauncher.data.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.URLUtil
import com.expeknow.ariselauncher.data.datasource.interfaces.DriveItemDataSource
import com.expeknow.ariselauncher.data.model.DriveItem
import com.expeknow.ariselauncher.data.model.DriveItemType
import com.expeknow.ariselauncher.data.repository.interfaces.DriveRepository
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DriveRepositoryImpl @Inject constructor(
    private val driveItemDataSource: DriveItemDataSource
) : DriveRepository {

    override fun getAllDriveItems(): Flow<List<DriveItem>> =
        driveItemDataSource.getAllDriveItems()

    override fun getDriveItemsByType(type: DriveItemType): Flow<List<DriveItem>> =
        driveItemDataSource.getDriveItemsByType(type)

    override suspend fun getDriveItemById(itemId: String): DriveItem? =
        driveItemDataSource.getDriveItemById(itemId)

    override suspend fun insertDriveItem(item: DriveItem) =
        driveItemDataSource.insertDriveItem(item)

    override suspend fun updateDriveItem(item: DriveItem) =
        driveItemDataSource.updateDriveItem(item)

    override suspend fun deleteDriveItem(item: DriveItem) =
        driveItemDataSource.deleteDriveItem(item)

    override suspend fun deleteDriveItemById(itemId: String) =
        driveItemDataSource.deleteDriveItemById(itemId)

    override suspend fun deleteAllDriveItems() =
        driveItemDataSource.deleteAllDriveItems()

    override fun openVideo(context: Context, videoUrl: String) {
        try {
            val intent = when {
                videoUrl.contains("youtube.com") || videoUrl.contains("youtu.be") -> {
                    Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl)).apply {
                        setPackage("com.google.android.youtube")
                    }
                }
                videoUrl.contains("instagram.com") -> {
                    Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl)).apply {
                        setPackage("com.instagram.android")
                    }
                }
                URLUtil.isValidUrl(videoUrl) -> {
                    Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
                }
                else -> {
                    Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(Uri.parse(videoUrl), "video/*")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                }
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
                fallbackIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(fallbackIntent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun saveImageFromUri(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val imagesDir = File(context.filesDir, "drive_images")
            if (!imagesDir.exists()) {
                imagesDir.mkdirs()
            }

            val fileName = "image_${System.currentTimeMillis()}.jpg"
            val outputFile = File(imagesDir, fileName)

            FileOutputStream(outputFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            inputStream.close()

            outputFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
