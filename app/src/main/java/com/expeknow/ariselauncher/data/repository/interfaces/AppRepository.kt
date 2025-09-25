package com.expeknow.ariselauncher.data.repository.interfaces

import android.content.Context
import com.expeknow.ariselauncher.data.model.AppInfo
import com.expeknow.ariselauncher.ui.screens.apps.AppDrawerApp

interface AppRepository {

    suspend fun getInstalledApps(): List<AppDrawerApp>
    fun launchApp(packageName: String)

    fun isDefaultLauncher(): Boolean

    fun openDefaultLauncherSettings()
}