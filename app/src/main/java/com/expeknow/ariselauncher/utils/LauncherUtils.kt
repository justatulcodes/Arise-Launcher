package com.expeknow.ariselauncher.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings

object LauncherUtils {

    fun isDefaultLauncher(context: Context): Boolean {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
        }
        val resolveInfo = context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        val currentHomePackage = resolveInfo?.activityInfo?.packageName
        return context.packageName == currentHomePackage
    }

    fun openDefaultLauncherSettings(context: Context) {
        val intent = Intent(Settings.ACTION_HOME_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        // Fall back to general applications settings if specific home settings intent is not available
        if (intent.resolveActivity(context.packageManager) == null) {
            intent.action = Settings.ACTION_APPLICATION_SETTINGS
        }

        context.startActivity(intent)
    }
}
