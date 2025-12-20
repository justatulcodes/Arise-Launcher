package com.expeknow.ariselauncher.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.expeknow.ariselauncher.data.model.AppInfo

@Dao
interface AppInfoDao {

    @Query("SELECT * FROM app_info WHERE packageName = :packageName")
    fun getAppInfo(packageName: String): AppInfo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAppInfo(appInfo: AppInfo)

    @Query("SELECT category FROM app_info WHERE packageName = :packageName")
    fun getAppCategory(packageName: String): String

    @Query("DELETE FROM app_info WHERE packageName = :packageName")
    fun deleteAppInfo(packageName: String)

    @Query("SELECT * FROM app_info ORDER BY installTime ASC")
    fun getAppsSortedByInstallTime() : List<AppInfo>

    @Query("DELETE FROM app_info")
    fun deleteAllAppInfo()

    @Query("SELECT * FROM app_info ORDER BY launchCount DESC, lastUsedTimestamp DESC LIMIT :count")
    suspend fun getTopUsedApps(count: Int): List<AppInfo>

    @Query("UPDATE app_info SET launchCount = launchCount + 1, lastUsedTimestamp = :timestamp WHERE packageName = :packageName")
    fun recordAppLaunch(packageName: String, timestamp: Long)

    @Query("SELECT * FROM app_info WHERE lastUsedTimestamp > :since ORDER BY launchCount DESC LIMIT :count")
    fun getRecentlyUsedApps(since: Long, count: Int): List<AppInfo>
}