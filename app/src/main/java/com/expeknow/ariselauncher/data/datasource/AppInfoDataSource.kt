package com.expeknow.ariselauncher.data.datasource

import com.expeknow.ariselauncher.data.database.dao.AppInfoDao
import com.expeknow.ariselauncher.data.model.AppInfo

class AppInfoDataSource(
    private val appInfoDao: AppInfoDao
) {

    fun getAppInfo(packageName : String) = appInfoDao.getAppInfo(packageName)

    fun addAppInfo(packageName: String, category: String, installTime : Long) {
        appInfoDao.addAppInfo(
            AppInfo(
                packageName = packageName,
                category = category,
                installTime = installTime
            )
        )
    }

    fun deleteAppInfo(packageName: String) = appInfoDao.deleteAppInfo(packageName)

    fun getAppSortedByInstallTime() = appInfoDao.getAppsSortedByInstallTime()

    fun deleteAllAppInfo() = appInfoDao.deleteAllAppInfo()

    fun getAppCategory(packageName: String) = appInfoDao.getAppCategory(packageName)

}