package com.expeknow.ariselauncher.data.repository

import android.content.Context
import android.content.Intent
import com.expeknow.ariselauncher.data.repository.interfaces.AppRepository
import com.expeknow.ariselauncher.ui.screens.apps.AppCategory
import com.expeknow.ariselauncher.ui.screens.apps.AppDrawerApp
import com.expeknow.ariselauncher.utils.AppClassifier
import com.expeknow.ariselauncher.utils.LauncherUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import com.expeknow.ariselauncher.data.datasource.AppInfoDataSource
import com.expeknow.ariselauncher.utils.AppClassifier.mapCategoryToAppCategory
import com.expeknow.ariselauncher.utils.InstalledAppObject

class AppRepositoryImpl(
    private val context: Context,
    private val appInfoDataSource: AppInfoDataSource
    ) : AppRepository {

    override suspend fun getInstalledApps(): List<AppDrawerApp> {
        val packageManager = context.packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val apps = packageManager.queryIntentActivities(mainIntent, 0)

        val appDrawerApps = apps.mapNotNull { resolveInfo ->
            val packageName = resolveInfo.activityInfo.packageName
            if (packageName == context.packageName) return@mapNotNull null

            val name = resolveInfo.loadLabel(packageManager).toString()
            val icon = resolveInfo.loadIcon(packageManager)
            val appInstallTime = packageManager.getPackageInfo(packageName, 0).firstInstallTime

            AppDrawerApp(
                name = name,
                packageName = packageName,
                icon = icon,
                id = packageName,
                category = AppCategory.MISCELLANEOUS,
                pointCost = AppClassifier.getAppPointCost(AppCategory.MISCELLANEOUS),
                appInstallTime = appInstallTime
            )
        }.sortedBy { it.name }
        appDrawerApps.forEach { app ->
            CoroutineScope(Dispatchers.IO).launch {
                val cachedCategory = appInfoDataSource.getAppInfo(packageName = app.packageName)
                if(cachedCategory != null) {
                    app.category = mapCategoryToAppCategory(cachedCategory.category)
                    app.pointCost = AppClassifier.getAppPointCost(app.category)
                }else{
                    val foundCategory = AppClassifier.classifyApp(context, app.packageName)
                    app.category = foundCategory
                    app.pointCost = AppClassifier.getAppPointCost(app.category)
                    appInfoDataSource.addAppInfo(
                        packageName = app.packageName,
                        category = AppClassifier.getDefaultCategoryString(foundCategory),
                        installTime = app.appInstallTime)

                }
            }

        }
        InstalledAppObject.installedAppList = appDrawerApps as MutableList<AppDrawerApp>
        return appDrawerApps

    }

    override suspend fun getCallingAndMessagingApps(): List<AppDrawerApp> {
        val packageManager = context.packageManager
        val result = mutableListOf<AppDrawerApp>()

        val callIntent = Intent(Intent.ACTION_DIAL)
        val callApps = packageManager.queryIntentActivities(callIntent, 0)
        val callAppInstallTime = packageManager.getPackageInfo(callApps[0].activityInfo.packageName, 0).firstInstallTime

        callApps.forEach { resolveInfo ->
            val packageName = resolveInfo.activityInfo.packageName
            if (packageName != context.packageName) {
                result.add(
                    AppDrawerApp(
                        name = resolveInfo.loadLabel(packageManager).toString(),
                        packageName = packageName,
                        icon = resolveInfo.loadIcon(packageManager),
                        id = packageName,
                        category = AppCategory.ESSENTIAL,
                        pointCost = AppClassifier.getAppPointCost(AppCategory.ESSENTIAL),
                        appInstallTime = callAppInstallTime
                    )
                )
            }
        }

        val smsIntent = Intent(Intent.ACTION_SENDTO, "smsto:".toUri())
        val smsApps = packageManager.queryIntentActivities(smsIntent, 0)
        val SMSappInstallTime = packageManager.getPackageInfo(smsApps[0].activityInfo.packageName, 0).firstInstallTime

        smsApps.forEach { resolveInfo ->
            val packageName = resolveInfo.activityInfo.packageName
            if (packageName != context.packageName &&
                result.none { it.packageName == packageName }) {
                result.add(
                    AppDrawerApp(
                        name = resolveInfo.loadLabel(packageManager).toString(),
                        packageName = packageName,
                        icon = resolveInfo.loadIcon(packageManager),
                        id = packageName,
                        category = AppCategory.ESSENTIAL,
                        pointCost = AppClassifier.getAppPointCost(AppCategory.ESSENTIAL),
                        appInstallTime = SMSappInstallTime
                    )
                )
            }
        }

        return result.sortedBy { it.name }
    }

    override fun launchApp(packageName: String) {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        launchIntent?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(it)

            CoroutineScope(Dispatchers.IO).launch {
                recordAppLaunch(packageName)
            }
        }
    }

    override fun isDefaultLauncher(): Boolean {
        return LauncherUtils.isDefaultLauncher(context)
    }

    override fun openDefaultLauncherSettings() {
        LauncherUtils.openDefaultLauncherSettings(context)
    }

    override fun getAppCategory(packageName: String): String {
        return appInfoDataSource.getAppCategory(packageName)
    }

    override fun recordAppLaunch(packageName: String) {
        appInfoDataSource.recordAppLaunch(packageName)
    }

    override suspend fun getTopUsedApps(count: Int): List<AppDrawerApp> {
        val topUsedAppInfos = appInfoDataSource.getTopUsedApps(count)
        val packageManager = context.packageManager

        return topUsedAppInfos.mapNotNull { appInfo ->
            try {
                val packageInfo = packageManager.getPackageInfo(appInfo.packageName, 0)
                val launchIntent = packageManager.getLaunchIntentForPackage(appInfo.packageName)

                // Only include apps that are still installed and launchable
                if (launchIntent != null) {
                    val appName = packageManager.getApplicationLabel(
                        packageManager.getApplicationInfo(appInfo.packageName, 0)
                    ).toString()
                    val icon = packageManager.getApplicationIcon(appInfo.packageName)

                    AppDrawerApp(
                        id = appInfo.packageName,
                        name = appName,
                        packageName = appInfo.packageName,
                        icon = icon,
                        category = mapCategoryToAppCategory(appInfo.category),
                        pointCost = AppClassifier.getAppPointCost(mapCategoryToAppCategory(appInfo.category)),
                        appInstallTime = appInfo.installTime
                    )
                } else {
                    null
                }
            } catch (e: Exception) {
                // App might have been uninstalled, skip it
                null
            }
        }
    }
}