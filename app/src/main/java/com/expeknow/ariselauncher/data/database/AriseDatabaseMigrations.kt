package com.expeknow.ariselauncher.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object AriseDatabaseMigrations {

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("""
                CREATE TABLE drive_items_new (
                    id TEXT NOT NULL PRIMARY KEY,
                    type TEXT NOT NULL,
                    content TEXT NOT NULL,
                    title TEXT NOT NULL,
                    author TEXT NOT NULL,
                    description TEXT NOT NULL,
                    createdAt INTEGER NOT NULL,
                    orderIndex INTEGER NOT NULL
                )
            """)
            database.execSQL("""
                INSERT INTO drive_items_new (id, type, content, title, author, description, createdAt, orderIndex)
                SELECT 
                    id,
                    CASE type
                        WHEN 0 THEN 'QUOTE'
                        WHEN 1 THEN 'IMAGE'
                        WHEN 2 THEN 'VIDEO'
                        ELSE COALESCE(type, 'QUOTE')
                    END,
                    content,
                    title,
                    author,
                    description,
                    createdAt,
                    orderIndex
                FROM drive_items
            """)
            database.execSQL("DROP TABLE drive_items")
            database.execSQL("ALTER TABLE drive_items_new RENAME TO drive_items")
        }
    }

    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE app_info ADD COLUMN launchCount INTEGER NOT NULL DEFAULT 0")
            database.execSQL("ALTER TABLE app_info ADD COLUMN lastUsedTimestamp INTEGER NOT NULL DEFAULT 0")
            database.execSQL("ALTER TABLE app_info ADD COLUMN totalTimeSpent INTEGER NOT NULL DEFAULT 0")
        }
    }
    
    val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS targets (
                    id TEXT NOT NULL PRIMARY KEY,
                    name TEXT NOT NULL,
                    description TEXT NOT NULL,
                    endDate INTEGER NOT NULL,
                    progress REAL NOT NULL,
                    createdAt INTEGER NOT NULL,
                    showOnHomeScreen INTEGER NOT NULL DEFAULT 0
                )
            """)
        }
    }
}
