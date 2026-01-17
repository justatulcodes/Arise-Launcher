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

    override fun getAppLaunchPopupEnabled(): Boolean = preferencesDataSource.getAppLaunchPopupEnabled()
    override fun setAppLaunchPopupEnabled(enabled: Boolean) = preferencesDataSource.setAppLaunchPopupEnabled(enabled)

    override fun getPointThreshold(): Float = preferencesDataSource.getPointThreshold()
    override fun setPointThreshold(threshold: Float) =
        preferencesDataSource.setPointThreshold(threshold)

    override fun getWarningsEnabled(): Boolean = preferencesDataSource.getWarningsEnabled()
    override fun setWarningsEnabled(enabled: Boolean) =
        preferencesDataSource.setWarningsEnabled(enabled)

    override fun getShouldTriggerKeyboardInAppDrawer() =
        preferencesDataSource.getShouldTriggerAppDrawerKeyboard()

    override fun setShouldTriggerKeyboardInAppDrawer(shouldTrigger: Boolean) =
        preferencesDataSource.setShouldTriggerAppDrawerKeyboard(shouldTrigger)

    override fun getShowEntireWeekSchedule(): Boolean = preferencesDataSource.getShowEntireWeekSchedule()
    override fun setShowEntireWeekSchedule(enabled: Boolean) =
        preferencesDataSource.setShowEntireWeekSchedule(enabled)

    override fun getShouldShowCategorizedApps(): Boolean = preferencesDataSource.getShouldShowCategorizedApps()
    override fun setShouldShowCategorizedApps(shouldShow: Boolean) =
        preferencesDataSource.setShouldShowCategorizedApps(shouldShow)

    override fun getIsFreshDatabaseInstance(): Boolean = preferencesDataSource.getIsFreshDbInstance()
    override fun setIsFreshDatabaseInstance(isFresh: Boolean) = preferencesDataSource.setIsFreshDbInstance(isFresh)

    override fun getAppTimerEnabled(): Boolean = preferencesDataSource.getAppTimerEnabled()
    override fun setAppTimerEnabled(enabled: Boolean) = preferencesDataSource.setAppTimerEnabled(enabled)

    override fun getShouldRefreshAppDrawer(): Boolean = preferencesDataSource.getShouldRefreshAppDrawer()
    override fun setShouldRefreshAppDrawer(enabled: Boolean) = preferencesDataSource.setShouldRefreshAppDrawer(enabled)

    override fun getShouldShowHomeScreen(): Boolean = preferencesDataSource.getShouldShowHomeScreen()
    override fun setShouldShowHomeScreen(enabled: Boolean) = preferencesDataSource.setShouldShowHomeScreen(enabled)

    override fun getHomeScreenQuote(): String? = preferencesDataSource.getHomeScreenQuote()
    override fun setHomeScreenQuote(quote: String?) = preferencesDataSource.setHomeScreenQuote(quote)

    override fun resetAllSettings() = preferencesDataSource.resetAllSettings()
}