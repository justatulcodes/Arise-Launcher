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

    fun getAppStartTimerValue(packageName: String) = appInfoDao.getAppStartTimerValue(packageName)

    fun setAppStartTimerValue(packageName: String, launchTimerValue: Long) {
        appInfoDao.setAppStartTimerValue(packageName, launchTimerValue)
    }
    fun deleteAppInfo(packageName: String) = appInfoDao.deleteAppInfo(packageName)

    fun getAppSortedByInstallTime() = appInfoDao.getAppsSortedByInstallTime()

    fun deleteAllAppInfo() = appInfoDao.deleteAllAppInfo()

    fun getAppCategory(packageName: String) = appInfoDao.getAppCategory(packageName)

    suspend fun getTopUsedApps(count: Int) = appInfoDao.getTopUsedApps(count)

    fun recordAppLaunch(packageName: String) {
        appInfoDao.recordAppLaunch(packageName, System.currentTimeMillis())
    }

    fun getRecentlyUsedApps(sinceDaysAgo: Int, count: Int): List<AppInfo> {
        val sinceTimestamp = System.currentTimeMillis() - (sinceDaysAgo * 24 * 60 * 60 * 1000L)
        return appInfoDao.getRecentlyUsedApps(sinceTimestamp, count)
    }
}