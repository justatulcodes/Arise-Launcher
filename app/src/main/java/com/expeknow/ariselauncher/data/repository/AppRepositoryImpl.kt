package com.expeknow.ariselauncher.data.repository

import android.content.Context
import android.content.Intent
import android.util.Log
import com.expeknow.ariselauncher.data.repository.interfaces.AppRepository
import com.expeknow.ariselauncher.ui.screens.apps.AppCategory
import com.expeknow.ariselauncher.ui.screens.apps.AppDrawerApp
import com.expeknow.ariselauncher.utils.AppClassifier
import com.expeknow.ariselauncher.utils.LauncherUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import androidx.core.net.toUri
import com.expeknow.ariselauncher.data.datasource.AppInfoDataSource
import com.expeknow.ariselauncher.utils.AppClassifier.mapCategoryToAppCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import com.expeknow.ariselauncher.utils.AppIconCache

class AppRepositoryImpl(
    private val context: Context,
    private val appInfoDataSource: AppInfoDataSource
    ) : AppRepository {

    private val iconCache by lazy { AppIconCache(context) }

    override suspend fun getInstalledApps(): Flow<List<AppDrawerApp>> {
        val pm = context.packageManager

        // Seed DB if empty (first launch)
        if (appInfoDataSource.getAppCount() == 0) {
            seedAppInfoFromSystem()
        }

        return appInfoDataSource.getAllAppsFlow()
            .map { appInfos ->
                withContext(Dispatchers.IO) {
                    appInfos.mapNotNull { info ->
                        try {
                            val launchIntent = pm.getLaunchIntentForPackage(info.packageName)
                            if (launchIntent == null) {
                                // app no longer launchable; drop
                                appInfoDataSource.deleteAppInfo(info.packageName)
                                return@mapNotNull null
                            }

                            val icon = iconCache.get(info.packageName) ?: run {
                                val drawable = pm.getApplicationIcon(info.packageName)
                                iconCache.put(info.packageName, drawable)
                                drawable
                            }

                            AppDrawerApp(
                                id = info.packageName,
                                name = info.name.ifBlank {
                                    pm.getApplicationLabel(
                                        pm.getApplicationInfo(
                                            info.packageName,
                                            0
                                        )
                                    ).toString()
                                },
                                packageName = info.packageName,
                                icon = icon,
                                category = mapCategoryToAppCategory(info.category),
                                pointCost = info.pointCost,
                                appInstallTime = info.installTime
                            )
                        } catch (e: Exception) {
                            Log.e(
                                "AppRepository",
                                "Error loading app ${info.packageName}: ${e.message}",
                                e
                            )
                            null
                        }
                    }.sortedBy { it.name }
                }
            }
            .flowOn(Dispatchers.IO)
    }

    private suspend fun seedAppInfoFromSystem() = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val installedApps = pm.queryIntentActivities(mainIntent, 0)

        // Process apps in parallel for faster loading
        val jobs = installedApps.mapNotNull { resolveInfo ->
            val packageName = resolveInfo.activityInfo.packageName
            if (packageName == context.packageName) return@mapNotNull null

            async(Dispatchers.IO) {
                try {
                    val appName = resolveInfo.loadLabel(pm).toString()
                    val installTime = pm.getPackageInfo(packageName, 0).firstInstallTime

                    // Use getFallbackCategory instead of classifyApp for fast initial load
                    // classifyApp fetches from Play Store which is slow
                    val appInfo = pm.getApplicationInfo(packageName, 0)
                    val category = AppClassifier.getFallbackCategory(appInfo)
                    val pointCost = AppClassifier.getAppPointCost(category)

                    // Add to database (this is blocking but fast)
                    appInfoDataSource.addAppInfo(
                        packageName = packageName,
                        category = AppClassifier.getDefaultCategoryString(category),
                        installTime = installTime,
                        name = appName,
                        pointCost = pointCost
                    )

                    // Cache icon
                    val icon = resolveInfo.loadIcon(pm)
                    iconCache.put(packageName, icon)

                    true
                } catch (e: Exception) {
                    false
                }
            }
        }

        val results = awaitAll(*jobs.toTypedArray())
    }

    override suspend fun getCallingAndMessagingApps(): List<AppDrawerApp> {
        val packageManager = context.packageManager
        val result = mutableListOf<AppDrawerApp>()


        try {
            val callIntent = Intent(Intent.ACTION_DIAL)
            val callApps = packageManager.queryIntentActivities(callIntent, 0)

            // Priority 1: Look for known standard dialer packages
            val dialerApp = callApps.find { resolveInfo ->
                val pkgName = resolveInfo.activityInfo.packageName
                pkgName == "com.android.dialer" ||
                pkgName == "com.google.android.dialer" ||
                pkgName == "com.android.contacts" ||
                pkgName == "com.google.android.contacts"
            } ?:
            // Priority 2: Look for packages containing "dialer" or ".phone" (but not jio, whatsapp, etc.)
            callApps.find { resolveInfo ->
                val pkgName = resolveInfo.activityInfo.packageName
                val isSystemApp = (resolveInfo.activityInfo.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0

                // Exclude known non-dialer apps
                val excludedPackages = listOf("jio", "whatsapp", "telegram", "truecaller", "facebook", "instagram", "skype", "zoom", "viber")
                val isExcluded = excludedPackages.any { pkgName.contains(it, ignoreCase = true) }

                !isExcluded && isSystemApp && (
                    pkgName.contains("dialer", ignoreCase = true) ||
                    pkgName.contains(".phone", ignoreCase = true)
                )
            } ?:
            // Priority 3: Any system app that handles dial intents
            callApps.find { resolveInfo ->
                val pkgName = resolveInfo.activityInfo.packageName
                val isSystemApp = (resolveInfo.activityInfo.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0

                val excludedPackages = listOf("jio", "whatsapp", "telegram", "truecaller", "facebook", "instagram", "skype", "zoom", "viber")
                val isExcluded = excludedPackages.any { pkgName.contains(it, ignoreCase = true) }

                !isExcluded && isSystemApp
            }

            dialerApp?.let { resolveInfo ->
                val packageName = resolveInfo.activityInfo.packageName
                if (packageName != context.packageName) {
                    val appInstallTime = packageManager.getPackageInfo(packageName, 0).firstInstallTime
                    result.add(
                        AppDrawerApp(
                            name = resolveInfo.loadLabel(packageManager).toString(),
                            packageName = packageName,
                            icon = resolveInfo.loadIcon(packageManager),
                            id = packageName,
                            category = AppCategory.ESSENTIAL,
                            pointCost = AppClassifier.getAppPointCost(AppCategory.ESSENTIAL),
                            appInstallTime = appInstallTime
                        )
                    )
                }
            }
        } catch (e: Exception) {
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