package com.expeknow.ariselauncher.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import com.expeknow.ariselauncher.data.model.AppInfo
import com.expeknow.ariselauncher.data.repository.interfaces.AppRepository
import com.expeknow.ariselauncher.ui.screens.apps.AppCategory
import com.expeknow.ariselauncher.ui.screens.apps.AppDrawerApp
import com.expeknow.ariselauncher.utils.AppClassifier
import com.expeknow.ariselauncher.utils.LauncherUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import com.expeknow.ariselauncher.utils.InstalledAppObject

class AppRepositoryImpl(private val context: Context) : AppRepository {

    override suspend fun getInstalledApps(): List<AppDrawerApp> {
        if(InstalledAppObject.installedAppList.isNotEmpty()){
            return InstalledAppObject.installedAppList
        }
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

            AppDrawerApp(
                name = name,
                packageName = packageName,
                icon = icon,
                id = packageName,
                category = AppCategory.MISCELLANEOUS,
                pointCost = AppClassifier.getAppPointCost(AppCategory.MISCELLANEOUS)
            )
        }.sortedBy { it.name }

        appDrawerApps.forEach { app ->
            CoroutineScope(Dispatchers.IO).launch {
                app.category = AppClassifier.classifyApp(context, app.packageName)
                app.pointCost = AppClassifier.getAppPointCost(app.category)
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
                        pointCost = AppClassifier.getAppPointCost(AppCategory.ESSENTIAL)
                    )
                )
            }
        }

        val smsIntent = Intent(Intent.ACTION_SENDTO, "smsto:".toUri())
        val smsApps = packageManager.queryIntentActivities(smsIntent, 0)

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
                        pointCost = AppClassifier.getAppPointCost(AppCategory.ESSENTIAL)
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
        }
    }

    override fun isDefaultLauncher(): Boolean {
        return LauncherUtils.isDefaultLauncher(context)
    }

    override fun openDefaultLauncherSettings() {
        LauncherUtils.openDefaultLauncherSettings(context)
    }
}