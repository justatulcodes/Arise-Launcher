package com.expeknow.ariselauncher.ui.screens.points

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import com.expeknow.ariselauncher.data.model.TaskStats
import com.expeknow.ariselauncher.data.model.ranks
import com.expeknow.ariselauncher.data.repository.interfaces.PointsLogRepository
import com.expeknow.ariselauncher.data.repository.interfaces.TaskRepository
import com.expeknow.ariselauncher.data.repository.interfaces.SettingsRepository
import com.expeknow.ariselauncher.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import com.expeknow.ariselauncher.data.model.Task
import com.expeknow.ariselauncher.data.model.TaskCategory
import com.expeknow.ariselauncher.ui.screens.home.CategoryStat
import com.expeknow.ariselauncher.ui.screens.home.StatsUi
import java.util.Calendar

@HiltViewModel
class PointsViewModel @Inject constructor(
    private val pointsLogRepositoryImpl: PointsLogRepository,
    private val taskRepositoryImpl: TaskRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PointsState())
    val state: StateFlow<PointsState> = _state.asStateFlow()

    private var navController: NavController? = null

    fun setNavController(navController: NavController) {
        this.navController = navController
        refreshTunnelVisionMode()
    }

    private fun refreshTunnelVisionMode() {
        viewModelScope.launch {
            val isTunnelVision = settingsRepository.getTunnelVisionMode()
            _state.value = _state.value.copy(isTunnelVisionMode = isTunnelVision)
        }
    }

    init {
        observePointsData()
        observeTaskStats()
        loadPointActivities()
        loadCompletedTasks()
        observeMvpStats()
        observeTunnelVisionMode()
    }

    private fun observeTunnelVisionMode() {
        viewModelScope.launch {
            val isTunnelVision = settingsRepository.getTunnelVisionMode()
            _state.value = _state.value.copy(isTunnelVisionMode = isTunnelVision)
        }
    }

    private fun observeMvpStats() {
        viewModelScope.launch {
            taskRepositoryImpl.getAllTasks().collectLatest { tasks ->
                val stats = computeTodayStats(tasks)
                _state.value = _state.value.copy(mvpStats = stats)
            }
        }
    }

    private fun computeTodayStats(allTasks: List<Task>): StatsUi {
        val focusCats = setOf(
            TaskCategory.PEOPLE,
            TaskCategory.OPPORTUNITY,
            TaskCategory.SKILLS
        )
        val todayDow = getTodaysDayOfWeek()

        fun isAvailableToday(t: Task): Boolean =
            (!t.isRepeated && t.category in focusCats) ||
                    (t.isRepeated && t.category in focusCats &&
                            (t.repeatDays.isEmpty() || todayDow in t.repeatDays))

        val focusedAvailable = allTasks.filter { isAvailableToday(it) }
        val focusedCompleted = focusedAvailable.filter { it.isCompleted }
        val focusPotentialPoints = focusedAvailable.sumOf { it.points }
        val focusEarnedPoints = focusedCompleted.sumOf { it.points }

        val perCategory = focusCats.map { c ->
            val avail = focusedAvailable.filter { it.category == c }
            val comp = avail.count { it.isCompleted }
            val potentialPts = avail.sumOf { it.points }
            val earnedPts = avail.filter { it.isCompleted }.sumOf { it.points }
            CategoryStat(
                category = c,
                completed = comp,
                total = avail.size,
                percent = if (avail.isNotEmpty()) comp.toDouble() / avail.size else 0.0,
                earnedPoints = earnedPts,
                potentialPoints = potentialPts
            )
        }

        // Personal (normal) tasks
        val personal = allTasks.filter { it.category == TaskCategory.PERSONAL }
        val personalAvailable = personal.filter {
            !it.isRepeated || it.repeatDays.contains(todayDow) || it.repeatDays.isEmpty()
        }
        val personalCompleted = personalAvailable.count { it.isCompleted }

        return StatsUi(
            focusOverallCompleted = focusedCompleted.size,
            focusOverallTotal = focusedAvailable.size,
            focusOverallPercent = if (focusedAvailable.isNotEmpty())
                focusedCompleted.size.toDouble() / focusedAvailable.size else 0.0,
            categories = perCategory,
            focusEarnedPoints = focusEarnedPoints,
            focusPotentialPoints = focusPotentialPoints,
            personalCompleted = personalCompleted,
            personalTotal = personalAvailable.size,
            personalPercent = if (personalAvailable.isNotEmpty())
                personalCompleted.toDouble() / personalAvailable.size else 0.0
        )
    }

    private fun getTodaysDayOfWeek(): com.expeknow.ariselauncher.data.model.DaysOfWeek {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        return when(dayOfWeek) {
            Calendar.SUNDAY -> com.expeknow.ariselauncher.data.model.DaysOfWeek.SUNDAY
            Calendar.MONDAY -> com.expeknow.ariselauncher.data.model.DaysOfWeek.MONDAY
            Calendar.TUESDAY -> com.expeknow.ariselauncher.data.model.DaysOfWeek.TUESDAY
            Calendar.WEDNESDAY -> com.expeknow.ariselauncher.data.model.DaysOfWeek.WEDNESDAY
            Calendar.THURSDAY -> com.expeknow.ariselauncher.data.model.DaysOfWeek.THURSDAY
            Calendar.FRIDAY -> com.expeknow.ariselauncher.data.model.DaysOfWeek.FRIDAY
            Calendar.SATURDAY -> com.expeknow.ariselauncher.data.model.DaysOfWeek.SATURDAY
            else -> com.expeknow.ariselauncher.data.model.DaysOfWeek.SUNDAY
        }
    }

    private fun loadCompletedTasks() {
        viewModelScope.launch {
            taskRepositoryImpl.getCompletedTasks().collectLatest { tasks ->
                _state.value = _state.value.copy(completedTasks = tasks)
            }
        }
    }

    private fun observePointsData() {
        viewModelScope.launch {
            combine(
                pointsLogRepositoryImpl.getTotalPointsEarned(),
                pointsLogRepositoryImpl.getAvailablePoints()
            ) { earnedPoints, availablePoints ->
                val earned = earnedPoints ?: 0
                val spent = earned - availablePoints
                
                // Calculate current rank based on points
                val currentRank = ranks.find { rank ->
                    availablePoints >= rank.minPoints && availablePoints <= rank.maxPoints
                } ?: ranks[0]

                _state.value = _state.value.copy(
                    currentPoints = availablePoints,
                    totalEarned = earned,
                    totalBurned = spent,
                    currentRank = currentRank
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
                    personalTasks = activeTasks.count { it.category == com.expeknow.ariselauncher.data.model.TaskCategory.PERSONAL },
                    completionRatio = completionRatio,
                    todayCompleted = completedTasks.count { 
                        // Count tasks completed today (last 24 hours)
                        (it.completedAt ?: 0) > System.currentTimeMillis() - 24 * 60 * 60 * 1000
                    },
                    weeklyAverage = completedCount / 7f // Simple weekly average
                )

                _state.value = _state.value.copy(
                    taskStats = taskStats,
                    completedTasks = completedTasks
                )
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
        loadCompletedTasks()
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
                navController?.navigate(Screen.TaskHistory.route)
            }
        }
    }
}