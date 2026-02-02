package com.expeknow.ariselauncher

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.expeknow.ariselauncher.utils.TestDataSeeder
import com.expeknow.ariselauncher.worker.TaskReminderScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class AriseLauncherApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        TaskReminderScheduler.scheduleTaskReminders(this)

        // Seed test data (runs only once, tracked via SharedPreferences)
        // To re-seed, call TestDataSeeder.forceReseed(this) or clear app data
        TestDataSeeder.seedIfNeeded(this)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}