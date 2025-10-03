package com.expeknow.ariselauncher.data.repository

import com.expeknow.ariselauncher.data.datasource.SettingsPreferencesDataSource
import com.expeknow.ariselauncher.data.repository.interfaces.SettingsRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val preferencesDataSource: SettingsPreferencesDataSource
) : SettingsRepository {

    override fun getHideCompletedTasks(): Boolean = preferencesDataSource.getHideCompletedTasks()
    override fun setHideCompletedTasks(hide: Boolean) =
        preferencesDataSource.setHideCompletedTasks(hide)

    override fun getTunnelVisionMode(): Boolean = preferencesDataSource.getTunnelVisionMode()
    override fun setTunnelVisionMode(enabled: Boolean) =
        preferencesDataSource.setTunnelVisionMode(enabled)

    override fun getAppDrawerDelay(): Float = preferencesDataSource.getAppDrawerDelay()
    override fun setAppDrawerDelay(delay: Float) = preferencesDataSource.setAppDrawerDelay(delay)

    override fun getDistractionAppsDelay(): Float = preferencesDataSource.getDistractionAppsDelay()
    override fun setDistractionAppsDelay(delay: Float) =
        preferencesDataSource.setDistractionAppsDelay(delay)

    override fun getPointThreshold(): Float = preferencesDataSource.getPointThreshold()
    override fun setPointThreshold(threshold: Float) =
        preferencesDataSource.setPointThreshold(threshold)

    override fun getWarningsEnabled(): Boolean = preferencesDataSource.getWarningsEnabled()
    override fun setWarningsEnabled(enabled: Boolean) =
        preferencesDataSource.setWarningsEnabled(enabled)

    override fun resetAllSettings() = preferencesDataSource.resetAllSettings()
}