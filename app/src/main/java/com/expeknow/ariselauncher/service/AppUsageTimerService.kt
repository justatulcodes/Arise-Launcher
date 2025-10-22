package com.expeknow.ariselauncher.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.expeknow.ariselauncher.data.repository.AppRepositoryImpl
import com.expeknow.ariselauncher.data.repository.PointsLogRepositoryImpl
import com.expeknow.ariselauncher.data.repository.interfaces.AppRepository
import com.expeknow.ariselauncher.data.repository.interfaces.PointsLogRepository
import com.expeknow.ariselauncher.ui.screens.timer.DynamicIslandTimer
import com.expeknow.ariselauncher.ui.theme.AriseLauncherTheme
import com.expeknow.ariselauncher.utils.AppClassifier
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class AppUsageTimerService
    : Service(), LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    @Inject
    lateinit var pointsLogRepositoryImpl: PointsLogRepository

    @Inject
    lateinit var appInfoRepositoryImpl: AppRepository
    private var windowManager: WindowManager? = null
    private var overlayView: ComposeView? = null
    private var layoutParams: WindowManager.LayoutParams? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var timerJob: Job? = null
    private var currentAppName: String = ""
    private var currentAppPackage : String = ""
    private var isVisible by mutableStateOf(false)
    private var elapsedSeconds by mutableStateOf(0)
    private var pointsLost by mutableStateOf(0)

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    override val viewModelStore = ViewModelStore()

    private val stopReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_STOP_TRACKING) {
                stopTracking()
            }
        }
    }

    companion object {
        private const val TAG = "AppUsageTimerService"
        const val ACTION_START_TRACKING = "com.expeknow.ariselauncher.START_TRACKING"
        const val ACTION_STOP_TRACKING = "com.expeknow.ariselauncher.STOP_TRACKING"
        const val EXTRA_APP_PACKAGE = "app_package"
        const val EXTRA_APP_NAME = "app_name"

        // Point depletion rate: 1 point every 10 seconds
        private var POINT_DEPLETION_INTERVAL_SECONDS = 10
    }

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val filter = IntentFilter(ACTION_STOP_TRACKING)
        registerReceiver(stopReceiver, filter, RECEIVER_NOT_EXPORTED)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when (intent?.action) {
            ACTION_START_TRACKING -> {
                val appName = intent.getStringExtra(EXTRA_APP_NAME) ?: "App"
                currentAppPackage = intent.getStringExtra(EXTRA_APP_PACKAGE) ?: "com.expeknow.ariselauncher"
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        setPointDepletionRate(currentAppPackage)
                    }
                    if(POINT_DEPLETION_INTERVAL_SECONDS != Int.MAX_VALUE){
                        startTracking(appName)
                    } else {
                        Log.d(TAG, "App $currentAppPackage has no point cost. Timer will not start.")
                    }
                }
            }
            ACTION_STOP_TRACKING -> {
                stopTracking()
            }
        }
        return START_STICKY
    }

    private fun setPointDepletionRate(currentAppPackage: String) {
        val appCategoryString = appInfoRepositoryImpl.getAppCategory(currentAppPackage)
        val appCategory = AppClassifier.mapCategoryToAppCategory(appCategoryString)
        val appPointCost = AppClassifier.getAppPointCost(appCategory)
        val depletionIntervalSeconds = calculateDepletionInterval(appPointCost)
        Log.d(TAG, "App: $currentAppPackage | Category: $appCategory | Cost: $appPointCost | Interval: $depletionIntervalSeconds s")
        POINT_DEPLETION_INTERVAL_SECONDS = depletionIntervalSeconds

    }
    private fun calculateDepletionInterval(pointCost: Int): Int {
        return when {
            pointCost == 0 -> Int.MAX_VALUE // effectively no depletion
            pointCost <= 5 -> 30 // slower depletion
            pointCost <= 10 -> 20
            pointCost <= 20 -> 10
            pointCost <= 25 -> 7
            pointCost <= 30 -> 5
            else -> 3 // fastest depletion for most addictive apps
        }
    }


    private fun startTracking(appName: String) {
        val hasPermission = Settings.canDrawOverlays(this)
        if (!hasPermission) {
            Log.e(TAG, "No overlay permission! Cannot show timer.")
            return
        }

        elapsedSeconds = 0
        pointsLost = 0
        currentAppName = appName

        if (overlayView == null) {
            createOverlay(appName)
        } else {
            isVisible = false
            serviceScope.launch {
                delay(100)
                isVisible = true
            }
        }

        startTimer()

        if (overlayView == null) {
            Log.d(TAG, "Overlay will be shown after creation")
        } else {
            showOverlay()
        }
    }

    private fun createOverlay(appName: String) {
        try {
            lifecycleRegistry.currentState = Lifecycle.State.STARTED

            overlayView = ComposeView(this).apply {

                visibility = View.VISIBLE

                setViewTreeLifecycleOwner(this@AppUsageTimerService)
                setViewTreeViewModelStoreOwner(this@AppUsageTimerService)
                setViewTreeSavedStateRegistryOwner(this@AppUsageTimerService)

                setContent {
                    AriseLauncherTheme {
                        DynamicIslandTimer(
                            isVisible = isVisible,
                            appName = currentAppName,
                            elapsedSeconds = elapsedSeconds,
                            pointsLost = pointsLost,
                            onPositionChange = { x, y ->
                                updateOverlayPosition(x, y)
                            }
                        )
                    }
                }
            }

            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                y = 50
            }

            layoutParams = params

            windowManager?.addView(overlayView, params)
            lifecycleRegistry.currentState = Lifecycle.State.RESUMED

            overlayView?.post {
                serviceScope.launch {
                    delay(100)
                    isVisible = true
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Failed to add overlay - Exception: ${e.javaClass.simpleName}", e)
            Log.e(TAG, "Error message: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun showOverlay() {

        serviceScope.launch {
            delay(100)
            isVisible = true
        }
    }

    private fun hideOverlay() {
        isVisible = false
        serviceScope.launch {
            delay(300)
            removeOverlay()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = serviceScope.launch {
            while (true) {
                delay(1000)
                elapsedSeconds++
                if (elapsedSeconds % POINT_DEPLETION_INTERVAL_SECONDS == 0) {
                    pointsLost++
                    pointsLogRepositoryImpl.spendPoints(
                        amount = 1,
                        taskId = currentAppPackage,
                        taskName = "Used ${currentAppName}")
                }
            }
        }
    }

    private fun stopTracking() {
        hideOverlay()
        timerJob?.cancel()
        timerJob = null
    }

    private fun removeOverlay() {
        try {
            overlayView?.let {
                windowManager?.removeView(it)
            }
            overlayView = null
        } catch (e: Exception) {
            Log.e(TAG, "Failed to remove overlay", e)
            e.printStackTrace()
        }
    }

    private fun updateOverlayPosition(x: Int, y: Int) {
        try {
            layoutParams?.let {
                it.x = x
                it.y = y
                windowManager?.updateViewLayout(overlayView, it)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating overlay position", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(stopReceiver)
        } catch (e: Exception) {
            Log.e(TAG, "Error unregistering receiver", e)
        }

        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        timerJob?.cancel()
        removeOverlay()
        serviceScope.cancel()
        viewModelStore.clear()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry
}
