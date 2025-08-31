package com.expeknow.ariselauncher.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.expeknow.ariselauncher.data.model.Task
import com.expeknow.ariselauncher.data.model.TaskConverters
import com.expeknow.ariselauncher.data.model.TaskLink

@Database(
    entities = [Task::class, TaskLink::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(TaskConverters::class)
abstract class AriseLauncherDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun taskLinkDao(): TaskLinkDao

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
                    .fallbackToDestructiveMigration() // For development - remove in production
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}