package com.expeknow.ariselauncher.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.expeknow.ariselauncher.data.database.dao.TaskDao
import com.expeknow.ariselauncher.notification.TaskReminderNotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.Calendar

@HiltWorker
class TaskReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val taskDao: TaskDao
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val WORK_NAME = "task_reminder_work"
        private const val MIN_COMPLETED_TASKS_THRESHOLD = 2
    }

    override suspend fun doWork(): Result {
        return try {
            val (startOfDay, endOfDay) = getTodayTimestamps()

            // Get task completion stats for Focus Mode (PEOPLE, OPPORTUNITY, SKILLS)
            val focusModeCompleted = taskDao.getFocusModeTasksCompletedToday(startOfDay, endOfDay)
            val focusModeActive = taskDao.getActiveFocusModeTasks()

            // Get task completion stats for Normal Mode (PERSONAL)
            val normalModeCompleted = taskDao.getNormalModeTasksCompletedToday(startOfDay, endOfDay)
            val normalModeActive = taskDao.getActiveNormalModeTasks()

            val notificationHelper = TaskReminderNotificationHelper(context)

            // Send Focus Mode reminder if user has active tasks but completed very few
            if (focusModeActive > 0 && focusModeCompleted < MIN_COMPLETED_TASKS_THRESHOLD) {
                notificationHelper.sendFocusModeReminder(focusModeCompleted, focusModeActive)
            }

            // Send Normal Mode reminder if user has active tasks but completed very few
            if (normalModeActive > 0 && normalModeCompleted < MIN_COMPLETED_TASKS_THRESHOLD) {
                notificationHelper.sendNormalModeReminder(normalModeCompleted, normalModeActive)
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    private fun getTodayTimestamps(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()

        // Start of day (00:00:00)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis

        // End of day (23:59:59)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfDay = calendar.timeInMillis

        return Pair(startOfDay, endOfDay)
    }
}

