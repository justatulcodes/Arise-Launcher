package com.expeknow.ariselauncher.data.repository.interfaces

import android.content.Context
import com.expeknow.ariselauncher.data.model.AppInfo

interface AppRepository {

    fun getInstalledApps(): List<AppInfo>
    fun launchApp(packageName: String)

    fun isDefaultLauncher(): Boolean

    fun openDefaultLauncherSettings()
}