package com.expeknow.ariselauncher.data.database

import android.content.Context
import com.expeknow.ariselauncher.data.datasource.OfflineDataSource
import com.expeknow.ariselauncher.data.repository.TaskRepositoryImpl

object DatabaseModule {

    fun provideDatabase(context: Context): AriseLauncherDatabase {
        return AriseLauncherDatabase.getDatabase(context)
    }

    fun provideOfflineDataSource(context: Context): OfflineDataSource {
        val database = provideDatabase(context)
        return OfflineDataSource(
            taskDao = database.taskDao(),
            taskLinkDao = database.taskLinkDao()
        )
    }

    fun provideTaskRepository(context: Context): TaskRepositoryImpl {
        val offlineDataSource = provideOfflineDataSource(context)
        return TaskRepositoryImpl(offlineDataSource, context)
    }
}