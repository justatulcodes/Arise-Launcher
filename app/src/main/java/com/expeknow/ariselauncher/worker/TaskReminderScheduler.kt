package com.expeknow.ariselauncher.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

object TaskReminderScheduler {

    private const val WORK_NAME_4PM = "task_reminder_4pm"
    private const val WORK_NAME_8PM = "task_reminder_8pm"
    private const val WORK_NAME_10PM = "task_reminder_10pm"

    fun scheduleTaskReminders(context: Context) {
        val workManager = WorkManager.getInstance(context)

        // Schedule reminders at 4 PM, 8 PM, and 10 PM
        scheduleReminderAt(workManager, WORK_NAME_4PM, 16, 0) // 4 PM
        scheduleReminderAt(workManager, WORK_NAME_8PM, 20, 0) // 8 PM
        scheduleReminderAt(workManager, WORK_NAME_10PM, 22, 0) // 10 PM
    }

    private fun scheduleReminderAt(
        workManager: WorkManager,
        workName: String,
        hour: Int,
        minute: Int
    ) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val currentTime = Calendar.getInstance()
        val scheduledTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // If the scheduled time has passed today, schedule for tomorrow
        if (scheduledTime.before(currentTime)) {
            scheduledTime.add(Calendar.DAY_OF_YEAR, 1)
        }

        val initialDelay = scheduledTime.timeInMillis - currentTime.timeInMillis

        val reminderWork = PeriodicWorkRequestBuilder<TaskReminderWorker>(
            repeatInterval = 24,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .addTag(workName)
            .build()

        workManager.enqueueUniquePeriodicWork(
            workName,
            ExistingPeriodicWorkPolicy.KEEP,
            reminderWork
        )
    }

    fun cancelTaskReminders(context: Context) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork(WORK_NAME_4PM)
        workManager.cancelUniqueWork(WORK_NAME_8PM)
        workManager.cancelUniqueWork(WORK_NAME_10PM)
    }
}

