package com.expeknow.ariselauncher.data.repository.interfaces

interface SettingsRepository {
    fun getHideCompletedTasks(): Boolean
    fun setHideCompletedTasks(hide: Boolean)

    fun getTunnelVisionMode(): Boolean
    fun setTunnelVisionMode(enabled: Boolean)

    fun getAppDrawerDelay(): Float
    fun setAppDrawerDelay(delay: Float)

    fun getDistractionAppsDelay(): Float
    fun setDistractionAppsDelay(delay: Float)

    fun getPointThreshold(): Float
    fun setPointThreshold(threshold: Float)

    fun getWarningsEnabled(): Boolean
    fun setWarningsEnabled(enabled: Boolean)

    fun resetAllSettings()
}