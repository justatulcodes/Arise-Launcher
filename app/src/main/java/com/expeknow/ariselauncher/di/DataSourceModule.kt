package com.expeknow.ariselauncher.di

import android.content.Context
import com.expeknow.ariselauncher.data.database.AppInfoDao
import com.expeknow.ariselauncher.data.database.PointsLogDao
import com.expeknow.ariselauncher.data.database.TaskDao
import com.expeknow.ariselauncher.data.database.TaskLinkDao
import com.expeknow.ariselauncher.data.datasource.AppInfoDataSource
import com.expeknow.ariselauncher.data.datasource.OfflinePointsLogDataSource
import com.expeknow.ariselauncher.data.datasource.OfflineTaskDataSource
import com.expeknow.ariselauncher.data.datasource.OfflineTaskLinkDataSource
import com.expeknow.ariselauncher.data.datasource.SettingsPreferencesDataSource
import com.expeknow.ariselauncher.data.datasource.interfaces.PointsLogDataSource
import com.expeknow.ariselauncher.data.datasource.interfaces.TaskDataSource
import com.expeknow.ariselauncher.data.datasource.interfaces.TaskLinkDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Provides
    fun providePointsLogDataSource(pointsLogDao: PointsLogDao): PointsLogDataSource {
        return OfflinePointsLogDataSource(pointsLogDao)
    }

    @Provides
    fun provideTaskDataSource(taskDao: TaskDao): TaskDataSource {
        return OfflineTaskDataSource(taskDao)
    }

    @Provides
    fun provideTaskLinkDataSource(taskLinkDao: TaskLinkDao): TaskLinkDataSource  {
        return OfflineTaskLinkDataSource(taskLinkDao)
    }

    @Provides
    fun provideSettingsPreferencesDataSource(@ApplicationContext context : Context): SettingsPreferencesDataSource {
        return SettingsPreferencesDataSource(context)
    }

    @Provides
    fun provideAppInfoDataSource(appInfoDao: AppInfoDao): AppInfoDataSource {
        return AppInfoDataSource(appInfoDao)
    }
}

