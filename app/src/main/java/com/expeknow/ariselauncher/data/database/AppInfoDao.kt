package com.expeknow.ariselauncher.data.database

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

    @Query("DELETE FROM app_info WHERE packageName = :packageName")
    fun deleteAppInfo(packageName: String)

    @Query("SELECT * FROM app_info ORDER BY installTime ASC")
    fun getAppsSortedByInstallTime() : List<AppInfo>


}