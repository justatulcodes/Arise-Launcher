package com.expeknow.ariselauncher.data.repository.interfaces

import com.expeknow.ariselauncher.data.model.AppInfo

interface AppRepository {

    fun getInstalledApps(): List<AppInfo>
}