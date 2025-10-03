package com.expeknow.ariselauncher.data.datasource

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import androidx.core.content.edit

@Singleton
class SettingsPreferencesDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    companion object {
        private const val PREFS_NAME = "arise_launcher_settings"
        private const val KEY_HIDE_COMPLETED_TASKS = "hide_completed_tasks"
        private const val KEY_TUNNEL_VISION_MODE = "tunnel_vision_mode"
        private const val KEY_APP_DRAWER_DELAY = "app_drawer_delay"
        private const val KEY_DISTRACTION_APPS_DELAY = "distraction_apps_delay"
        private const val KEY_POINT_THRESHOLD = "point_threshold"
        private const val KEY_WARNINGS_ENABLED = "warnings_enabled"
    }

    fun getHideCompletedTasks(): Boolean = prefs.getBoolean(KEY_HIDE_COMPLETED_TASKS, true)
    fun setHideCompletedTasks(hide: Boolean) =
        prefs.edit { putBoolean(KEY_HIDE_COMPLETED_TASKS, hide) }

    fun getTunnelVisionMode(): Boolean = prefs.getBoolean(KEY_TUNNEL_VISION_MODE, true)
    fun setTunnelVisionMode(enabled: Boolean) =
        prefs.edit { putBoolean(KEY_TUNNEL_VISION_MODE, enabled) }

    fun getAppDrawerDelay(): Float = prefs.getFloat(KEY_APP_DRAWER_DELAY, 60f)
    fun setAppDrawerDelay(delay: Float) = prefs.edit { putFloat(KEY_APP_DRAWER_DELAY, delay) }

    fun getDistractionAppsDelay(): Float = prefs.getFloat(KEY_DISTRACTION_APPS_DELAY, 30f)
    fun setDistractionAppsDelay(delay: Float) =
        prefs.edit { putFloat(KEY_DISTRACTION_APPS_DELAY, delay) }

    fun getPointThreshold(): Float = prefs.getFloat(KEY_POINT_THRESHOLD, 50f)
    fun setPointThreshold(threshold: Float) =
        prefs.edit { putFloat(KEY_POINT_THRESHOLD, threshold) }

    fun getWarningsEnabled(): Boolean = prefs.getBoolean(KEY_WARNINGS_ENABLED, true)
    fun setWarningsEnabled(enabled: Boolean) =
        prefs.edit { putBoolean(KEY_WARNINGS_ENABLED, enabled) }

    fun resetAllSettings() {
        prefs.edit { clear() }
    }
}