package com.expeknow.ariselauncher.data.repository.interfaces

interface SettingsRepository {
    fun getHideCompletedTasks(): Boolean
    fun setHideCompletedTasks(hide: Boolean)

    fun getTunnelVisionMode(): Boolean
    fun setTunnelVisionMode(enabled: Boolean)

    fun getAppLaunchPopupEnabled(): Boolean
    fun setAppLaunchPopupEnabled(enabled: Boolean)

    fun getPointThreshold(): Float
    fun setPointThreshold(threshold: Float)

    fun getWarningsEnabled(): Boolean
    fun setWarningsEnabled(enabled: Boolean)

    fun getShouldTriggerKeyboardInAppDrawer(): Boolean
    fun setShouldTriggerKeyboardInAppDrawer(shouldTrigger: Boolean)

    fun getShowEntireWeekSchedule(): Boolean
    fun setShowEntireWeekSchedule(enabled: Boolean)

    fun getShouldShowCategorizedApps(): Boolean
    fun setShouldShowCategorizedApps(shouldShow: Boolean)

    fun getIsFreshDatabaseInstance(): Boolean
    fun setIsFreshDatabaseInstance(isFresh: Boolean)

    fun getAppTimerEnabled(): Boolean
    fun setAppTimerEnabled(enabled: Boolean)

    fun getShouldRefreshAppDrawer() : Boolean
    fun setShouldRefreshAppDrawer(enabled: Boolean)

    fun getShouldShowHomeScreen() : Boolean
    fun setShouldShowHomeScreen(enabled: Boolean)


    fun resetAllSettings()
}