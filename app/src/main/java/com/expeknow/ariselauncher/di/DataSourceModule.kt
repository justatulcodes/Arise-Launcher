package com.expeknow.ariselauncher.di

import com.expeknow.ariselauncher.data.database.PointsLogDao
import com.expeknow.ariselauncher.data.database.TaskDao
import com.expeknow.ariselauncher.data.database.TaskLinkDao
import com.expeknow.ariselauncher.data.datasource.OfflinePointsLogDataSource
import com.expeknow.ariselauncher.data.datasource.OfflineTaskDataSource
import com.expeknow.ariselauncher.data.datasource.OfflineTaskLinkDataSource
import com.expeknow.ariselauncher.data.datasource.interfaces.PointsLogDataSource
import com.expeknow.ariselauncher.data.datasource.interfaces.TaskDataSource
import com.expeknow.ariselauncher.data.datasource.interfaces.TaskLinkDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
}