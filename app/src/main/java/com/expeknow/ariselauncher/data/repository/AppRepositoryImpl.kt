package com.expeknow.ariselauncher.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
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

class AppRepositoryImpl(private val context: Context) : AppRepository {

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
            val category = AppClassifier.classifyApp(context, packageName)

            AppDrawerApp(
                name = name,
                packageName = packageName,
                icon = icon,
                id = packageName,
                category = category,
                pointCost = AppClassifier.getAppPointCost(category)
            )
        }.sortedBy { it.name }

        Log.d("AppRepositoryImpl", "getInstalledApps: ${appDrawerApps.size}")
        return appDrawerApps

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