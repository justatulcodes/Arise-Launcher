package com.expeknow.ariselauncher.di

import android.content.Context
import com.expeknow.ariselauncher.data.datasource.SettingsPreferencesDataSource
import com.expeknow.ariselauncher.data.datasource.interfaces.PointsLogDataSource
import com.expeknow.ariselauncher.data.datasource.interfaces.TaskDataSource
import com.expeknow.ariselauncher.data.datasource.interfaces.TaskLinkDataSource
import com.expeknow.ariselauncher.data.repository.AppRepositoryImpl
import com.expeknow.ariselauncher.data.repository.PointsLogRepositoryImpl
import com.expeknow.ariselauncher.data.repository.SettingsRepositoryImpl
import com.expeknow.ariselauncher.data.repository.TaskLinkRepositoryImpl
import com.expeknow.ariselauncher.data.repository.TaskRepositoryImpl
import com.expeknow.ariselauncher.data.repository.interfaces.AppRepository
import com.expeknow.ariselauncher.data.repository.interfaces.PointsLogRepository
import com.expeknow.ariselauncher.data.repository.interfaces.SettingsRepository
import com.expeknow.ariselauncher.data.repository.interfaces.TaskLinkRepository
import com.expeknow.ariselauncher.data.repository.interfaces.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideAppRepository(@ApplicationContext context : Context) : AppRepository {
        return AppRepositoryImpl(context)
    }

    @Singleton
    @Provides
    fun providePointsLogRepository(pointsLogDataSource: PointsLogDataSource): PointsLogRepository {
        return PointsLogRepositoryImpl(pointsLogDataSource)
    }

    @Singleton
    @Provides
    fun provideTaskDataRepository(taskDataSource: TaskDataSource): TaskRepository {
        return TaskRepositoryImpl(taskDataSource)
    }

    @Singleton
    @Provides
    fun provideTaskLinkDataRepository(taskLinkDataSource: TaskLinkDataSource) : TaskLinkRepository {
        return TaskLinkRepositoryImpl(taskLinkDataSource)
    }

    @Singleton
    @Provides
    fun provideSettingsRepository(settingsPreferencesDataSource: SettingsPreferencesDataSource) : SettingsRepository {
        return SettingsRepositoryImpl(settingsPreferencesDataSource)
    }

}