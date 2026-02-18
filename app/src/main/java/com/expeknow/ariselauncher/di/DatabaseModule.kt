package com.expeknow.ariselauncher.di

import android.content.Context
import com.expeknow.ariselauncher.data.database.dao.AppInfoDao
import com.expeknow.ariselauncher.data.database.AriseLauncherDatabase
import com.expeknow.ariselauncher.data.database.dao.DriveItemDao
import com.expeknow.ariselauncher.data.database.dao.PointsLogDao
import com.expeknow.ariselauncher.data.database.dao.TargetsDao
import com.expeknow.ariselauncher.data.database.dao.TaskDao
import com.expeknow.ariselauncher.data.database.dao.TaskLinkDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context : Context): AriseLauncherDatabase {
        return AriseLauncherDatabase.Companion.getDatabase(context)
    }

    @Provides
    fun provideTaskDao(database: AriseLauncherDatabase) : TaskDao {
        return database.taskDao()
    }

    @Provides
    fun provideTargetsDao(database: AriseLauncherDatabase) : TargetsDao {
        return database.targetsDao()
    }
    @Provides
    fun provideTaskLinkDao(database: AriseLauncherDatabase) : TaskLinkDao {
        return database.taskLinkDao()
    }

    @Provides
    fun providePointsLogDao(database: AriseLauncherDatabase) : PointsLogDao {
        return database.pointsLogDao()
    }

    @Provides
    fun provideAppInfoDao(database: AriseLauncherDatabase) : AppInfoDao {
        return database.appInfoDao()
    }

    @Provides
    fun provideDriveItemDao(database: AriseLauncherDatabase) : DriveItemDao {
        return database.driveItemDao()
    }

}