package com.expeknow.ariselauncher.data.database

import androidx.room.Dao
import androidx.room.Query
import com.expeknow.ariselauncher.data.model.AppInfo

@Dao
interface AppInfoDao {

    @Query("SELECT * FROM app_info WHERE packageName = :packageName")
    fun getAppInfo(packageName: String) : AppInfo

    @Query("INSERT INTO app_info (packageName, category, installTime) VALUES (:packageName, :category, :installTime)")
    fun addAppInfo(packageName: String, category: String, installTime: Long)

    @Query("DELETE FROM app_info WHERE packageName = :packageName")
    fun deleteAppInfo(packageName: String)

    @Query("SELECT * FROM app_info ORDER BY installTime ASC")
    fun getAppsSortedByInstallTime() : List<AppInfo>


}