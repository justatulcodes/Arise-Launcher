package com.expeknow.ariselauncher.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.expeknow.ariselauncher.MainActivity
import com.expeknow.ariselauncher.R
import java.util.Calendar

class TaskReminderNotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "task_reminder_channel"
        const val CHANNEL_NAME = "Task Reminders"
        const val CHANNEL_DESCRIPTION = "Notifications to remind you to update your tasks"
        const val NOTIFICATION_ID_FOCUS = 1001
        const val NOTIFICATION_ID_NORMAL = 1002

        enum class TimeOfDay {
            AFTERNOON,  // 4 PM - Light reminder
            EVENING,    // 8 PM - Finish tasks
            NIGHT       // 10 PM - Log progress
        }
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
            description = CHANNEL_DESCRIPTION
            enableVibration(true)
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun sendFocusModeReminder(completedTasks: Int, totalTasks: Int, timeOfDay: TimeOfDay = getCurrentTimeOfDay()) {
        if (!hasNotificationPermission()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val (title, message) = getFocusModeMessage(completedTasks, totalTasks, timeOfDay)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID_FOCUS, notification)
    }

    fun sendNormalModeReminder(completedTasks: Int, totalTasks: Int, timeOfDay: TimeOfDay = getCurrentTimeOfDay()) {
        if (!hasNotificationPermission()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val (title, message) = getNormalModeMessage(completedTasks, totalTasks, timeOfDay)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID_NORMAL, notification)
    }

    private fun getFocusModeMessage(completedTasks: Int, totalTasks: Int, timeOfDay: TimeOfDay): Pair<String, String> {
        val messageIndex = (System.currentTimeMillis() / 1000).toInt() % 5

        return when (timeOfDay) {
            TimeOfDay.AFTERNOON -> {
                // 4 PM - Light reminder to focus
                val title = when (messageIndex % 3) {
                    0 -> "Focus Mode Check-In"
                    1 -> "Afternoon Progress Review"
                    else -> "Task Status Update"
                }
                val message = when {
                    completedTasks == 0 && totalTasks > 0 -> when (messageIndex % 5) {
                        0 -> "You have $totalTasks focus tasks pending. Start with your highest priority item."
                        1 -> "Time to make progress on your focus goals. $totalTasks tasks are waiting."
                        2 -> "Your focus mode tasks need attention. Begin with the most impactful one."
                        3 -> "Consider tackling one of your $totalTasks focus tasks this afternoon."
                        else -> "Stay on track with your focus objectives. Review your task list."
                    }
                    completedTasks == 1 && totalTasks > 1 -> when (messageIndex % 5) {
                        0 -> "Good start with 1 task complete. ${totalTasks - 1} focus tasks remain."
                        1 -> "You've completed 1 task. Keep the momentum going."
                        2 -> "One task down, ${totalTasks - 1} to go. Maintain your focus."
                        3 -> "Progress noted: 1 of $totalTasks tasks complete. Continue building momentum."
                        else -> "You're making headway. ${totalTasks - 1} focus tasks still pending."
                    }
                    else -> when (messageIndex % 4) {
                        0 -> "Review your focus mode tasks and prioritize accordingly."
                        1 -> "Check in on your strategic goals and task alignment."
                        2 -> "Time to assess progress on your focus objectives."
                        else -> "Ensure your focus tasks reflect today's priorities."
                    }
                }
                title to message
            }

            TimeOfDay.EVENING -> {
                // 8 PM - Finish tasks or mark complete
                val title = when (messageIndex % 3) {
                    0 -> "Evening Task Review"
                    1 -> "End of Day Progress"
                    else -> "Task Completion Reminder"
                }
                val message = when {
                    completedTasks == 0 && totalTasks > 0 -> when (messageIndex % 5) {
                        0 -> "No tasks marked complete yet. Update your task status to track today's progress."
                        1 -> "Have you completed any tasks? Mark them as done to log your achievements."
                        2 -> "Remember to mark completed tasks as done. This helps track your productivity."
                        3 -> "Update your task list. Mark any completed work before the day ends."
                        else -> "Don't forget to log completed tasks. Your progress deserves to be recorded."
                    }
                    completedTasks < 2 && totalTasks > 0 -> when (messageIndex % 5) {
                        0 -> "Only $completedTasks task marked complete. Finish or log remaining work."
                        1 -> "Time is running short. Complete pending tasks or update your progress."
                        2 -> "Wrap up your focus tasks. $completedTasks of $totalTasks complete."
                        3 -> "End your workday strong. Address remaining focus tasks or mark them complete."
                        else -> "Day is ending soon. Finalize tasks or ensure completed work is logged."
                    }
                    else -> when (messageIndex % 4) {
                        0 -> "Review your focus task completion for today."
                        1 -> "Ensure all completed work is properly logged."
                        2 -> "Update your task status before concluding the day."
                        else -> "Verify your focus task progress is accurately reflected."
                    }
                }
                title to message
            }

            TimeOfDay.NIGHT -> {
                // 10 PM - Log progress and prepare
                val title = when (messageIndex % 3) {
                    0 -> "Daily Progress Log"
                    1 -> "End of Day Summary"
                    else -> "Preparation for Tomorrow"
                }
                val message = when {
                    completedTasks == 0 && totalTasks > 0 -> when (messageIndex % 5) {
                        0 -> "Mark today's completed tasks before you rest. Accurate tracking leads to better planning."
                        1 -> "Log your accomplishments. Even partial progress should be recorded."
                        2 -> "Update task status now. Tomorrow's planning depends on today's accurate records."
                        3 -> "Don't let today's progress go unrecorded. Mark completed tasks."
                        else -> "Ensure your task list reflects today's actual work. Update now."
                    }
                    completedTasks < 2 && totalTasks > 0 -> when (messageIndex % 5) {
                        0 -> "$completedTasks of $totalTasks tasks complete. Review and prepare for tomorrow."
                        1 -> "Log today's work and set priorities for tomorrow. Plan while it's fresh."
                        2 -> "Wrap up your day. Update tasks and organize tomorrow's objectives."
                        3 -> "Time to reflect. Mark completed work and outline tomorrow's focus."
                        else -> "Complete your daily log. Prepare your task list for a productive tomorrow."
                    }
                    else -> when (messageIndex % 4) {
                        0 -> "Review today's progress and plan tomorrow's priorities."
                        1 -> "Finalize your task log and set up for tomorrow's success."
                        2 -> "Reflect on today's achievements and prepare for tomorrow."
                        else -> "Close out today and organize your focus for tomorrow."
                    }
                }
                title to message
            }
        }
    }

    private fun getNormalModeMessage(completedTasks: Int, totalTasks: Int, timeOfDay: TimeOfDay): Pair<String, String> {
        val messageIndex = (System.currentTimeMillis() / 1000).toInt() % 5

        return when (timeOfDay) {
            TimeOfDay.AFTERNOON -> {
                val title = when (messageIndex % 3) {
                    0 -> "Personal Tasks Reminder"
                    1 -> "Afternoon Check-In"
                    else -> "Task Update"
                }
                val message = when {
                    completedTasks == 0 && totalTasks > 0 -> when (messageIndex % 5) {
                        0 -> "You have $totalTasks personal tasks pending. Take a moment to review them."
                        1 -> "$totalTasks personal tasks await your attention. Consider addressing one now."
                        2 -> "Check your personal task list. $totalTasks items need your focus."
                        3 -> "Don't forget your personal goals. $totalTasks tasks are waiting."
                        else -> "Balance your day by addressing personal tasks. $totalTasks pending."
                    }
                    completedTasks == 1 && totalTasks > 1 -> when (messageIndex % 5) {
                        0 -> "One personal task complete. ${totalTasks - 1} remaining."
                        1 -> "You've checked off 1 task. Keep going with the rest."
                        2 -> "Progress on personal tasks: 1 of $totalTasks done."
                        3 -> "Good work on 1 task. ${totalTasks - 1} more to address."
                        else -> "You're moving forward. ${totalTasks - 1} personal tasks left."
                    }
                    else -> when (messageIndex % 4) {
                        0 -> "Review your personal task list for today."
                        1 -> "Check in on your personal goals and tasks."
                        2 -> "Take a moment to assess your personal task progress."
                        else -> "Balance your focus by reviewing personal tasks."
                    }
                }
                title to message
            }

            TimeOfDay.EVENING -> {
                val title = when (messageIndex % 3) {
                    0 -> "Personal Tasks Review"
                    1 -> "Evening Update"
                    else -> "End of Day Check"
                }
                val message = when {
                    completedTasks == 0 && totalTasks > 0 -> when (messageIndex % 5) {
                        0 -> "No personal tasks marked complete. Update your progress before day's end."
                        1 -> "Mark completed personal tasks as done. Keep your records accurate."
                        2 -> "Have you finished any personal tasks? Log them now."
                        3 -> "Update your personal task status to reflect today's progress."
                        else -> "Remember to mark completed personal tasks. Track your achievements."
                    }
                    completedTasks < 2 && totalTasks > 0 -> when (messageIndex % 5) {
                        0 -> "$completedTasks personal task logged. Wrap up or update remaining items."
                        1 -> "Complete pending personal tasks or mark finished work as done."
                        2 -> "Time is limited. Address remaining personal tasks: $completedTasks of $totalTasks done."
                        3 -> "Finish your personal tasks or ensure completed ones are logged."
                        else -> "Day is ending. Update your personal task progress: $completedTasks/$totalTasks complete."
                    }
                    else -> when (messageIndex % 4) {
                        0 -> "Review your personal task completion status."
                        1 -> "Verify all completed personal tasks are logged."
                        2 -> "Check your personal task list before ending the day."
                        else -> "Ensure personal task progress is up to date."
                    }
                }
                title to message
            }

            TimeOfDay.NIGHT -> {
                val title = when (messageIndex % 3) {
                    0 -> "Daily Personal Log"
                    1 -> "Personal Tasks Summary"
                    else -> "Tomorrow's Preparation"
                }
                val message = when {
                    completedTasks == 0 && totalTasks > 0 -> when (messageIndex % 5) {
                        0 -> "Log today's personal task progress. Accurate records support better planning."
                        1 -> "Mark completed personal tasks before you rest. Keep your tracking current."
                        2 -> "Update personal task status now. Tomorrow's planning needs accurate data."
                        3 -> "Record today's personal accomplishments. Even small progress matters."
                        else -> "Ensure personal tasks reflect today's work. Update your list."
                    }
                    completedTasks < 2 && totalTasks > 0 -> when (messageIndex % 5) {
                        0 -> "$completedTasks of $totalTasks personal tasks done. Review and plan for tomorrow."
                        1 -> "Wrap up your day. Log personal progress and set tomorrow's priorities."
                        2 -> "Close out today's personal tasks. Prepare for tomorrow's goals."
                        3 -> "Review personal task completion: $completedTasks/$totalTasks. Plan ahead."
                        else -> "Finalize your personal task log and organize for tomorrow."
                    }
                    else -> when (messageIndex % 4) {
                        0 -> "Review personal progress and prepare tomorrow's task list."
                        1 -> "Reflect on today's personal achievements and plan ahead."
                        2 -> "Close out your personal tasks and set up for tomorrow."
                        else -> "Finalize today's personal log and prepare for tomorrow."
                    }
                }
                title to message
            }
        }
    }

    private fun getCurrentTimeOfDay(): TimeOfDay {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 15..17 -> TimeOfDay.AFTERNOON  // 3 PM - 5 PM
            in 19..21 -> TimeOfDay.EVENING    // 7 PM - 9 PM
            else -> TimeOfDay.NIGHT           // 10 PM and later
        }
    }

    private fun hasNotificationPermission(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}
