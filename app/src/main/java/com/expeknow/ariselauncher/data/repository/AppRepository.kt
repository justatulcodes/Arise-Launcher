package com.expeknow.ariselauncher.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.expeknow.ariselauncher.data.model.AppInfo

class AppRepository(private val context: Context) {
    fun getInstalledApps(): List<AppInfo> {
        val packageManager = context.packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val apps = packageManager.queryIntentActivities(mainIntent, PackageManager.MATCH_DEFAULT_ONLY)
        return apps.mapNotNull { resolveInfo ->
            val packageName = resolveInfo.activityInfo.packageName
            if (packageName != context.packageName) {
                AppInfo(
                    packageName = packageName,
                    name = resolveInfo.loadLabel(packageManager).toString(),
                    icon = resolveInfo.loadIcon(packageManager)
                )
            } else null
        }
    }
}