package com.expeknow.ariselauncher

import android.app.Application
import com.expeknow.ariselauncher.data.database.AriseLauncherDatabase
import com.expeknow.ariselauncher.data.repository.TaskRepositoryImpl
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AriseLauncherApplication : Application() {
    // Database instance
    private val database by lazy {
        AriseLauncherDatabase.getDatabase(this)
    }

    // Data source instance
    private val offlineDataSource by lazy {
        OfflineDataSource(
            taskDao = database.taskDao(),
            taskLinkDao = database.taskLinkDao()
        )
    }

    // Repository instance
    val taskRepositoryImpl by lazy { TaskRepositoryImpl(offlineDataSource, this) }
}