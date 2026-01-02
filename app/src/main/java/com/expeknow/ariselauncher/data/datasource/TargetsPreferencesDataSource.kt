package com.expeknow.ariselauncher.data.datasource

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class Target(
    val id: String,
    val name: String,
    val description: String,
    val endDate: Long,
    val progress: Float,
    val createdAt: Long,
    val isCompleted: Boolean = false
)

@Singleton
class TargetsPreferencesDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private val gson = Gson()

    private val _targetsFlow = MutableStateFlow<List<Target>>(emptyList())
    val targetsFlow: StateFlow<List<Target>> = _targetsFlow.asStateFlow()

    companion object {
        private const val PREFS_NAME = "arise_targets_prefs"
        private const val KEY_TARGETS = "targets"
    }

    init {
        _targetsFlow.value = getTargets()
    }

    fun getTargets(): List<Target> {
        val json = prefs.getString(KEY_TARGETS, null) ?: return emptyList()
        val type = object : TypeToken<List<Target>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addTarget(target: Target) {
        val targets = getTargets().toMutableList()
        targets.add(target)
        saveTargets(targets)
    }

    fun updateTarget(target: Target) {
        val targets = getTargets().toMutableList()
        val index = targets.indexOfFirst { it.id == target.id }
        if (index != -1) {
            targets[index] = target
            saveTargets(targets)
        }
    }

    fun deleteTarget(targetId: String) {
        val targets = getTargets().filter { it.id != targetId }
        saveTargets(targets)
    }

    private fun saveTargets(targets: List<Target>) {
        val json = gson.toJson(targets)
        prefs.edit { putString(KEY_TARGETS, json) }
        _targetsFlow.value = targets
    }
}

