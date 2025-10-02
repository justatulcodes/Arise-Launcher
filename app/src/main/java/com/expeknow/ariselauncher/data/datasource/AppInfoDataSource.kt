package com.expeknow.ariselauncher.data.datasource

import com.expeknow.ariselauncher.data.database.AppInfoDao

class AppInfoDataSource(
    private val appInfoDao: AppInfoDao
) {

    fun getAppInfo(packageName : String) = appInfoDao.getAppInfo(packageName)

    fun addAppInfo(packageName: String, category: String, installTime : Long) = appInfoDao.addAppInfo(packageName, category, installTime)

    fun deleteAppInfo(packageName: String) = appInfoDao.deleteAppInfo(packageName)

    fun getAppSortedByInstallTime() = appInfoDao.getAppsSortedByInstallTime()

}