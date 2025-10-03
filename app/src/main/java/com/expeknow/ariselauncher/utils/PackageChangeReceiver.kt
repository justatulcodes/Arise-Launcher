package com.expeknow.ariselauncher.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.expeknow.ariselauncher.data.datasource.AppInfoDataSource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PackageChangeReceiver : BroadcastReceiver() {

    @Inject
    lateinit var appInfoDataSource: AppInfoDataSource

    override fun onReceive(context: Context, intent: Intent) {
        val packageName = intent.data?.schemeSpecificPart ?: return

        when (intent.action) {
            Intent.ACTION_PACKAGE_ADDED -> {
                val isUpdate = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)

                if (!isUpdate) {
                    Log.d("PackageReceiver", "New app installed: $packageName")
                    Toast.makeText(
                        context,
                        "Installed: $packageName",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Perform classification and persistence off the main thread
                    val pendingResult = goAsync()
                    CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
                        try {
                            val category = AppClassifier.classifyApp(context, packageName)
                            val installTime = context.packageManager
                                .getPackageInfo(packageName, 0)
                                .firstInstallTime

                            appInfoDataSource.addAppInfo(
                                packageName = packageName,
                                category = AppClassifier.getDefaultCategoryString(category.ordinal),
                                installTime = installTime
                            )
                        } catch (e: Exception) {
                            Log.e("PackageReceiver", "Failed to classify/save $packageName", e)
                        } finally {
                            pendingResult.finish()
                        }
                    }
                } else {
                    Log.d("PackageReceiver", "App updated: $packageName")
                    Toast.makeText(
                        context,
                        "Updated: $packageName",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            Intent.ACTION_PACKAGE_REMOVED -> {
                val isUpdate = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)

                if (!isUpdate) {
                    Log.d("PackageReceiver", "App uninstalled: $packageName")
                    Toast.makeText(
                        context,
                        "Uninstalled: $packageName",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Remove from local db on uninstall
                    val pendingResult = goAsync()
                    CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
                        try {
                            appInfoDataSource.deleteAppInfo(packageName)
                        } catch (e: Exception) {
                            Log.e("PackageReceiver", "Failed to delete $packageName from db", e)
                        } finally {
                            pendingResult.finish()
                        }
                    }
                } else {
                    Log.d("PackageReceiver", "App being replaced: $packageName")
                }
            }
        }
    }
}