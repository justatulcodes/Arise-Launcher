# Task Reminder Notification System - Implementation Guide

## Overview
This notification system reminds users to update their tasks if they haven't completed enough during the day. It differentiates between **Focus Mode** tasks (PEOPLE, OPPORTUNITY, SKILLS categories) and **Normal Mode** tasks (PERSONAL category).

## What Was Implemented

### 1. **Dependencies Added**
- **WorkManager** (`androidx.work:work-runtime-ktx`) - For scheduling daily notifications
- **Hilt WorkManager** (`androidx.hilt:hilt-work`) - For dependency injection in Workers

### 2. **Database Queries Added** (`TaskDao.kt`)
New queries to track task completion:
- `getFocusModeTasksCompletedToday()` - Count Focus Mode tasks completed today
- `getNormalModeTasksCompletedToday()` - Count Normal Mode tasks completed today
- `getActiveFocusModeTasks()` - Count pending Focus Mode tasks
- `getActiveNormalModeTasks()` - Count pending Normal Mode tasks

### 3. **Core Components Created**

#### `TaskReminderNotificationHelper.kt`
- Creates notification channel for task reminders
- Sends customized notifications based on completion status
- Handles Android 13+ notification permissions
- **Messages:**
  - **0 tasks completed**: "You haven't completed any [Focus/Personal] tasks today..."
  - **1 task completed**: "Only 1 [Focus/Personal] task completed today..."
  - **Fallback**: "Remember to update your tasks!"

#### `TaskReminderWorker.kt`
- Background worker that checks task completion
- Runs at scheduled times (4 PM, 8 PM, 10 PM)
- Sends notifications if:
  - User has active tasks AND
  - Completed fewer than 2 tasks today
- Integrated with Hilt for dependency injection

#### `TaskReminderScheduler.kt`
- Schedules three daily notification checks:
  - **4:00 PM** - Afternoon reminder
  - **8:00 PM** - Evening reminder
  - **10:00 PM** - Night reminder
- Uses `PeriodicWorkRequest` with 24-hour intervals
- Calculates initial delay to sync with scheduled times

#### `NotificationPermissionHelper.kt`
- Utility class for checking notification permissions
- Contains helper methods for requesting permissions in Compose UI
- Handles Android version compatibility

### 4. **App Initialization** (`AriseLauncherApplication.kt`)
- Configured WorkManager with Hilt
- Schedules task reminders on app startup
- Ensures notifications are scheduled even after device reboot

### 5. **Permissions** (`AndroidManifest.xml`)
- Added `POST_NOTIFICATIONS` permission for Android 13+

## How It Works

### Task Categorization
- **Focus Mode Tasks**: Tasks with categories `PEOPLE`, `OPPORTUNITY`, or `SKILLS`
- **Normal Mode Tasks**: Tasks with category `PERSONAL`

### Notification Logic
```
For each scheduled time (4 PM, 8 PM, 10 PM):
  1. Check Focus Mode tasks completed today
  2. Check Normal Mode tasks completed today
  3. If Focus Mode has active tasks AND < 2 completed → Send Focus notification
  4. If Normal Mode has active tasks AND < 2 completed → Send Normal notification
```

### Notification Threshold
- Minimum completed tasks before notification is suppressed: **2 tasks**
- You can adjust this in `TaskReminderWorker.kt` by changing `MIN_COMPLETED_TASKS_THRESHOLD`

## Next Steps for Full Integration

### 1. **Request Notification Permission in UI**
You need to request notification permission from users (Android 13+). Here's an example using Accompanist Permissions:

```kotlin
// In your main screen or settings screen
@Composable
fun RequestNotificationPermission() {
    val context = LocalContext.current
    
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val notificationPermissionState = rememberPermissionState(
            permission = Manifest.permission.POST_NOTIFICATIONS
        )
        
        LaunchedEffect(Unit) {
            if (!notificationPermissionState.status.isGranted) {
                notificationPermissionState.launchPermissionRequest()
            }
        }
    }
}
```

### 2. **Sync Gradle Dependencies**
After opening the project in Android Studio:
1. Click **"Sync Now"** when the banner appears
2. Or go to **File → Sync Project with Gradle Files**
3. Wait for dependencies to download

### 3. **Build and Test**
```bash
./gradlew clean build
```

### 4. **Testing the Notifications**
To test without waiting for scheduled times:

**Option A - Manually trigger the worker:**
```kotlin
// Add this temporarily in your MainActivity or a test button
val workManager = WorkManager.getInstance(applicationContext)
val testWork = OneTimeWorkRequestBuilder<TaskReminderWorker>()
    .build()
workManager.enqueue(testWork)
```

**Option B - Change notification times:**
Edit `TaskReminderScheduler.kt` and change the hours to test sooner:
```kotlin
scheduleReminderAt(workManager, WORK_NAME_4PM, 14, 30) // Test at 2:30 PM
```

## Customization Options

### Change Notification Times
Edit `TaskReminderScheduler.kt`:
```kotlin
scheduleReminderAt(workManager, WORK_NAME_4PM, 16, 0)  // Hour, Minute
scheduleReminderAt(workManager, WORK_NAME_8PM, 20, 0)
scheduleReminderAt(workManager, WORK_NAME_10PM, 22, 0)
```

### Change Completion Threshold
Edit `TaskReminderWorker.kt`:
```kotlin
private const val MIN_COMPLETED_TASKS_THRESHOLD = 2 // Change this value
```

### Customize Notification Messages
Edit `TaskReminderNotificationHelper.kt`:
```kotlin
val message = when {
    completedTasks == 0 && totalTasks > 0 -> "Your custom message"
    // ... add more cases
}
```

### Add More Notification Times
Edit `TaskReminderScheduler.kt`:
```kotlin
scheduleReminderAt(workManager, "task_reminder_12pm", 12, 0)
```

## Architecture Benefits

✅ **No Database Changes Required** - Works with existing schema
✅ **Hilt Integration** - Clean dependency injection
✅ **Battery Efficient** - Uses WorkManager's optimized scheduling
✅ **Survives Reboots** - WorkManager persists schedules
✅ **Separate Notifications** - Different messages for Focus vs Normal mode
✅ **Smart Logic** - Only notifies when needed

## Troubleshooting

### Notifications Not Showing?
1. Check if notification permission is granted (Settings → Apps → Arise Launcher → Notifications)
2. Ensure the app isn't in battery optimization
3. Check WorkManager status: `adb shell dumpsys jobscheduler`

### Workers Not Running?
1. Check if workers are scheduled: `WorkManager.getInstance(context).getWorkInfosByTag("task_reminder_4pm")`
2. Look at logs for any errors in `TaskReminderWorker`

### Testing Issues?
1. Use `adb shell am broadcast -a android.intent.action.TIME_SET` to trigger time-based workers
2. Or use the manual trigger approach mentioned in testing section

## Files Modified/Created

### New Files:
- `app/src/main/java/com/expeknow/ariselauncher/notification/TaskReminderNotificationHelper.kt`
- `app/src/main/java/com/expeknow/ariselauncher/worker/TaskReminderWorker.kt`
- `app/src/main/java/com/expeknow/ariselauncher/worker/TaskReminderScheduler.kt`
- `app/src/main/java/com/expeknow/ariselauncher/di/WorkManagerModule.kt`
- `app/src/main/java/com/expeknow/ariselauncher/utils/NotificationPermissionHelper.kt`

### Modified Files:
- `gradle/libs.versions.toml` - Added WorkManager dependencies
- `app/build.gradle.kts` - Added WorkManager and Hilt Work libraries
- `app/src/main/java/com/expeknow/ariselauncher/data/database/dao/TaskDao.kt` - Added query methods
- `app/src/main/java/com/expeknow/ariselauncher/AriseLauncherApplication.kt` - Initialize scheduling
- `app/src/main/AndroidManifest.xml` - Added notification permission

---

**Implementation Complete! 🚀**

The notification system is ready to use. Once you sync Gradle dependencies and request notification permission from users, the app will automatically send reminders at 4 PM, 8 PM, and 10 PM if users haven't completed enough tasks.

