package com.expeknow.ariselauncher.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.expeknow.ariselauncher.data.database.AriseDatabaseMigrations.MIGRATION_2_3
import com.expeknow.ariselauncher.data.database.AriseDatabaseMigrations.MIGRATION_3_4
import com.expeknow.ariselauncher.data.database.AriseDatabaseMigrations.MIGRATION_4_5
import com.expeknow.ariselauncher.data.database.dao.AppInfoDao
import com.expeknow.ariselauncher.data.database.dao.DriveItemDao
import com.expeknow.ariselauncher.data.database.dao.PointsLogDao
import com.expeknow.ariselauncher.data.database.dao.TaskDao
import com.expeknow.ariselauncher.data.database.dao.TaskLinkDao
import com.expeknow.ariselauncher.data.model.AppInfo
import com.expeknow.ariselauncher.data.model.DriveItem
import com.expeknow.ariselauncher.data.model.ModelTypeConverters
import com.expeknow.ariselauncher.data.model.PointsLog
import com.expeknow.ariselauncher.data.model.Task
import com.expeknow.ariselauncher.data.model.TaskLink

@Database(
    entities = [Task::class, TaskLink::class, PointsLog::class, AppInfo::class, DriveItem::class],
    version = 5,
    exportSchema = true
)
@TypeConverters(ModelTypeConverters::class)
abstract class AriseLauncherDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun taskLinkDao(): TaskLinkDao
    abstract fun pointsLogDao(): PointsLogDao
    abstract fun appInfoDao(): AppInfoDao
    abstract fun driveItemDao(): DriveItemDao

    companion object {
        @Volatile
        private var INSTANCE: AriseLauncherDatabase? = null

        fun getDatabase(context: Context): AriseLauncherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AriseLauncherDatabase::class.java,
                    "arise_launcher_database"
                )
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}