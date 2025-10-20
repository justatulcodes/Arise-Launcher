package com.expeknow.ariselauncher.service

import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Intent
import android.os.IBinder
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AppMonitorService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    private var monitorJob: Job? = null
    private var currentTrackedApp: String? = null

    companion object {
        private const val TAG = "AppMonitorService"
        private val MONITORED_APPS = mapOf(
            "com.instagram.android" to "Instagram"
        )
        private const val CHECK_INTERVAL_MS = 2000L
    }

    override fun onCreate() {
        super.onCreate()
        startMonitoring()
    }

    private fun startMonitoring() {
        monitorJob = serviceScope.launch {
            while (isActive) {
                checkForegroundApp()
                delay(CHECK_INTERVAL_MS)
            }
        }
    }

    private fun checkForegroundApp() {
        try {
            val usageStatsManager = getSystemService(USAGE_STATS_SERVICE) as? UsageStatsManager
            if (usageStatsManager == null) {
                Log.e(TAG, "UsageStatsManager is null - Usage access permission may not be granted!")
                return
            }

            Log.d(TAG, "UsageStatsManager obtained successfully")

            val endTime = System.currentTimeMillis()
            val startTime = endTime - 1000 * 10 // Look back 10 seconds

            Log.d(TAG, "Querying usage events from $startTime to $endTime (${(endTime - startTime) / 1000} seconds window)")

            // First, try to query usage stats to verify permission
            val stats = usageStatsManager.queryUsageStats(
                android.app.usage.UsageStatsManager.INTERVAL_BEST,
                startTime,
                endTime
            )
            Log.d(TAG, "Usage stats query returned ${stats?.size ?: 0} entries")
            if (stats.isNullOrEmpty()) {
                Log.w(TAG, "Usage stats query returned empty - this might indicate permission is not granted properly")
            } else {
                Log.d(TAG, "Recent apps from usage stats: ${stats.take(3).map { it.packageName }}")
            }

            val usageEvents = usageStatsManager.queryEvents(startTime, endTime)
            Log.d(TAG, "UsageEvents query executed")

            var currentApp: String? = null
            val event = UsageEvents.Event()
            var eventCount = 0

            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event)
                eventCount++

                // Log ALL events for now to see what we're getting
                Log.d(TAG, "Event #$eventCount: type=${event.eventType}, package=${event.packageName}, class=${event.className}, time=${event.timeStamp}")

                when (event.eventType) {
                    UsageEvents.Event.ACTIVITY_RESUMED -> {
                        currentApp = event.packageName
                        Log.d(TAG, ">>> ACTIVITY_RESUMED: $currentApp")
                    }
                    UsageEvents.Event.ACTIVITY_PAUSED -> {
                        Log.d(TAG, ">>> ACTIVITY_PAUSED: ${event.packageName}")
                        if (event.packageName == currentApp) {
                            currentApp = null
                        }
                    }
                    1 -> { // MOVE_TO_FOREGROUND
                        currentApp = event.packageName
                        Log.d(TAG, ">>> MOVE_TO_FOREGROUND: $currentApp")
                    }
                    2 -> { // MOVE_TO_BACKGROUND
                        Log.d(TAG, ">>> MOVE_TO_BACKGROUND: ${event.packageName}")
                        if (event.packageName == currentApp) {
                            currentApp = null
                        }
                    }
                }
            }

            Log.d(TAG, "Total events processed: $eventCount")
            Log.d(TAG, "Current foreground app: $currentApp")

            // If we got no events, try to get the current app differently
            if (eventCount == 0) {
                Log.w(TAG, "No events found! This suggests usage access permission is not working.")
                Log.w(TAG, "Please verify 'Usage Access' permission is granted in Settings > Apps > Arise Launcher > Usage Access")

                // Try to get recent app from usage stats
                if (!stats.isNullOrEmpty()) {
                    val mostRecent = stats.maxByOrNull { it.lastTimeUsed }
                    Log.d(TAG, "Most recently used app from stats: ${mostRecent?.packageName} at ${mostRecent?.lastTimeUsed}")
                }
            }

            // Log the current foreground app
            if (currentApp != null && currentApp != "com.expeknow.ariselauncher") {
                Log.d(TAG, "=== Foreground app detected: $currentApp ===")
            }

            // Check if the current foreground app is one we're monitoring
            val monitoredApp = MONITORED_APPS[currentApp]

            if (monitoredApp != null && currentTrackedApp != currentApp) {
                // Start tracking this app
                Log.d(TAG, "!!! Starting tracking for: $monitoredApp ($currentApp) !!!")
                currentTrackedApp = currentApp
                startTimerForApp(currentApp!!, monitoredApp)
            } else if (monitoredApp == null && currentTrackedApp != null) {
                // Stop tracking - monitored app is no longer in foreground
                Log.d(TAG, "!!! Stopping tracking for: $currentTrackedApp !!!")
                stopTimer()
                currentTrackedApp = null
            } else if (currentApp != null) {
                Log.d(TAG, "App $currentApp is in foreground but not monitored")
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException in checkForegroundApp - Usage access permission is required!", e)
        } catch (e: Exception) {
            Log.e(TAG, "Error in checkForegroundApp: ${e.javaClass.simpleName} - ${e.message}", e)
        }
        Log.d(TAG, "=== checkForegroundApp END ===")
    }

    private fun startTimerForApp(packageName: String, appName: String) {
        try {
            val intent = Intent(this, AppUsageTimerService::class.java).apply {
                action = AppUsageTimerService.ACTION_START_TRACKING
                putExtra(AppUsageTimerService.EXTRA_APP_PACKAGE, packageName)
                putExtra(AppUsageTimerService.EXTRA_APP_NAME, appName)
            }
            startService(intent)
            Log.d(TAG, "Timer service start intent sent for $appName")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start timer service", e)
        }
    }

    private fun stopTimer() {
        try {
            // Instead of starting a service from background, send a broadcast or use a different method
            // For now, we'll just stop trying to call the service
            Log.d(TAG, "Timer should stop (service call skipped to avoid background restrictions)")

            // Alternative: Send a broadcast that the timer service can listen to
            val intent = Intent(AppUsageTimerService.ACTION_STOP_TRACKING).apply {
                setPackage(packageName)
            }
            sendBroadcast(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping timer", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "AppMonitorService destroyed")
        monitorJob?.cancel()
        // Don't call stopTimer() here to avoid the background service start exception
        currentTrackedApp = null
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
