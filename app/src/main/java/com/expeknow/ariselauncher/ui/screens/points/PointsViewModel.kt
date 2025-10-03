package com.expeknow.ariselauncher.ui.screens.points

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import com.expeknow.ariselauncher.data.repository.TaskRepositoryImpl
import com.expeknow.ariselauncher.data.model.TaskStats
import com.expeknow.ariselauncher.data.repository.PointsLogRepositoryImpl
import com.expeknow.ariselauncher.data.repository.interfaces.PointsLogRepository
import com.expeknow.ariselauncher.data.repository.interfaces.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PointsViewModel @Inject constructor(
    private val pointsLogRepositoryImpl: PointsLogRepository,
    private val taskRepositoryImpl: TaskRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PointsState())
    val state: StateFlow<PointsState> = _state.asStateFlow()

    init {
        observePointsData()
        observeTaskStats()
        loadPointActivities()
    }

    private fun observePointsData() {
        viewModelScope.launch {
            combine(
                pointsLogRepositoryImpl.getTotalPointsEarned(),
                pointsLogRepositoryImpl.getAvailablePoints()
            ) { earnedPoints, availablePoints ->
                val earned = earnedPoints ?: 0
                val spent = earned - availablePoints
                
                _state.value = _state.value.copy(
                    currentPoints = availablePoints,
                    totalEarned = earned,
                    totalBurned = spent
                )
            }.collect { }
        }
    }

    private fun observeTaskStats() {
        viewModelScope.launch {
            combine(
                taskRepositoryImpl.getActiveTasks(),
                taskRepositoryImpl.getCompletedTasks(),
                taskRepositoryImpl.getActiveTaskCount(),
                taskRepositoryImpl.getCompletedTaskCount()
            ) { activeTasks, completedTasks, activeCount, completedCount ->
                val totalTasks = activeCount + completedCount
                val completionRatio = if (totalTasks > 0) {
                    (completedCount.toFloat() / totalTasks) * 100f
                } else 0f

                val taskStats = TaskStats(
                    totalTasks = totalTasks,
                    completedTasks = completedCount,
                    urgentTasks = activeTasks.count { it.category == com.expeknow.ariselauncher.data.model.TaskCategory.URGENT },
                    workTasks = activeTasks.count { it.category == com.expeknow.ariselauncher.data.model.TaskCategory.WORK },
                    personalTasks = activeTasks.count { it.category == com.expeknow.ariselauncher.data.model.TaskCategory.PERSONAL },
                    completionRatio = completionRatio,
                    todayCompleted = completedTasks.count { 
                        // Count tasks completed today (last 24 hours)
                        (it.completedAt ?: 0) > System.currentTimeMillis() - 24 * 60 * 60 * 1000
                    },
                    weeklyAverage = completedCount / 7f // Simple weekly average
                )

                _state.value = _state.value.copy(taskStats = taskStats)
            }.collect { }
        }
    }

    private fun loadPointActivities() {
        viewModelScope.launch {
//            val activities = pointsLogRepositoryImpl.getPointActivities()
//            val history = pointsLogRepositoryImpl.getPointsHistory()
//            _state.value = _state.value.copy(
//                activities = activities,
//                pointsHistory = history
//            )
        }
    }

    // Public method to refresh data
    fun refreshData() {
        loadPointActivities()
    }

    fun onEvent(event: PointsEvent) {
        when (event) {
            is PointsEvent.SelectTab -> {
                _state.value = _state.value.copy(selectedTabIndex = event.index)
            }

            is PointsEvent.SetDebugRank -> {
                _state.value = _state.value.copy(debugCurrentRank = event.rank)
            }

            is PointsEvent.NavigateToTaskHistory -> {
                // Handle navigation - this would typically involve a navigation callback
            }
        }
    }
}