package com.expeknow.ariselauncher.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import com.expeknow.ariselauncher.data.model.AppInfo
import com.expeknow.ariselauncher.data.repository.interfaces.AppRepository

class AppRepositoryImpl(private val context: Context) : AppRepository {
    override fun getInstalledApps(): List<AppInfo> {
        val packageManager = context.packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val apps = packageManager.queryIntentActivities(mainIntent, 0)

        Log.d("InstalledApps", "getInstalledApps: appList size = ${apps.size}")
        return apps.mapNotNull { resolveInfo ->
            val packageName = resolveInfo.activityInfo.packageName
            if (packageName != context.packageName) {
                AppInfo(
                    packageName = packageName,
                    name = resolveInfo.loadLabel(packageManager).toString(),
                    icon = resolveInfo.loadIcon(packageManager)
                )
            } else null
        }.sortedBy { it.name }
    }

    override fun launchApp(packageName: String) {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        launchIntent?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(it)
        }
    }
}