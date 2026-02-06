package com.expeknow.ariselauncher

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.expeknow.ariselauncher.data.repository.interfaces.SettingsRepository
import com.expeknow.ariselauncher.service.AppUsageTimerService
import com.expeknow.ariselauncher.ui.navigation.AppNavigation
import com.expeknow.ariselauncher.ui.theme.AriseLauncherTheme
import com.expeknow.ariselauncher.utils.PackageChangeReceiver
import com.expeknow.ariselauncher.utils.PermissionHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val packageReceiver = PackageChangeReceiver()
    private val TAG = "MainActivity"

    @Inject
    lateinit var settingsRepository: SettingsRepository

    private var isAppTimerEnabled: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Cache the app timer setting to avoid main thread blocking
        isAppTimerEnabled = settingsRepository.getAppTimerEnabled()

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
        }
        registerReceiver(packageReceiver, filter)

        setContent {
            AriseLauncherTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    val prefs = getSharedPreferences("arise_prefs", MODE_PRIVATE)
                    val hasSeenWelcome = prefs.getBoolean("has_seen_welcome", false)

                    val startDestination = when {
                        !hasSeenWelcome -> com.expeknow.ariselauncher.ui.navigation.Screen.Welcome.route
                        hasSeenWelcome -> com.expeknow.ariselauncher.ui.navigation.Screen.Main.route
                        else -> com.expeknow.ariselauncher.ui.navigation.Screen.PermissionOnboarding.route
                    }

                    AppNavigation(navController = navController, startDestination = startDestination)
                }
            }
        }
    }

    private fun getForegroundAppInfo(context: Context): Pair<String, String>? {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val time = System.currentTimeMillis()
        val usageEvents = usageStatsManager.queryEvents(time - 2000, time)
        var lastEvent: UsageEvents.Event? = null

        while (usageEvents.hasNextEvent()) {
            val event = UsageEvents.Event()
            usageEvents.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                lastEvent = event
            }
        }

        lastEvent?.let { event ->
            val packageName = event.packageName
            val appName = try {
                val appInfo = context.packageManager.getApplicationInfo(packageName, 0)
                context.packageManager.getApplicationLabel(appInfo).toString()
            } catch (e: Exception) {
                packageName // fallback
            }
            return packageName to appName
        }
        return null
    }


    private fun startTimerForApp(context: Context, packageName: String, appName: String) {
        try {
            val intent = Intent(context, AppUsageTimerService::class.java).apply {
                action = AppUsageTimerService.ACTION_START_TRACKING
                putExtra(AppUsageTimerService.EXTRA_APP_PACKAGE, packageName)
                putExtra(AppUsageTimerService.EXTRA_APP_NAME, appName)
            }
            context.startService(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start timer service", e)
        }
    }

    private fun stopTimerForApp(context: Context) {
        try {
            val intent = Intent(context, AppUsageTimerService::class.java).apply {
                action = AppUsageTimerService.ACTION_STOP_TRACKING
            }
            context.startService(intent)
        }
        catch (e: Exception) {
            Log.e(TAG, "Failed to stop timer service", e)
        }
    }

    override fun onStop() {
        super.onStop()
        if (!isAppTimerEnabled) {
            return
        }

        // Perform heavy operation off the main thread
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val appInfo = getForegroundAppInfo(this@MainActivity)
                appInfo?.let { (packageName, appName) ->
                    withContext(Dispatchers.Main) {
                        startTimerForApp(this@MainActivity, packageName, appName)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in onStop", e)
            }
        }
    }

    override fun onStart() {
        super.onStart()

        // Update cached value
        isAppTimerEnabled = settingsRepository.getAppTimerEnabled()

        stopTimerForApp(this)
    }

    override fun onResume() {
        super.onResume()
        // Update cached value when returning to foreground
        isAppTimerEnabled = settingsRepository.getAppTimerEnabled()
    }

}