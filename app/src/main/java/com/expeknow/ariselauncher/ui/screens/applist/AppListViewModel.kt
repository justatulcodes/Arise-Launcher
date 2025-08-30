package com.expeknow.ariselauncher.ui.screens.applist

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.expeknow.ariselauncher.data.model.AppInfo
import com.expeknow.ariselauncher.data.repository.AppRepository

class AppListViewModel(
    private val appRepository: AppRepository,
    private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(AppListState())
    val state: StateFlow<AppListState> = _state.asStateFlow()

    init {
        loadApps()
    }

    fun onEvent(event: AppListEvent) {
        when (event) {
            is AppListEvent.LoadApps -> {
                loadApps()
            }

            is AppListEvent.LaunchApp -> {
                launchApp(event.app)
            }

            is AppListEvent.NavigateToSettings -> {
                // Handle navigation - this would typically involve a navigation callback
            }
        }
    }

    private fun loadApps() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val apps = appRepository.getInstalledApps()
            _state.value = _state.value.copy(
                apps = apps,
                isLoading = false
            )
        }
    }

    private fun launchApp(app: AppInfo) {
        val intent = context.packageManager.getLaunchIntentForPackage(app.packageName)
        intent?.let {
            context.startActivity(it)
        }
    }
}