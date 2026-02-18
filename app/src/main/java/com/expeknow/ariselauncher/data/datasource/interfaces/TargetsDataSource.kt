package com.expeknow.ariselauncher.data.datasource.interfaces

import com.expeknow.ariselauncher.data.model.Targets
import kotlinx.coroutines.flow.Flow

interface TargetsDataSource {

    fun getAllTargets() : Flow<List<Targets>>

    suspend fun addTarget(target : Targets)

    suspend fun updateTarget(target: Targets)

    suspend fun deleteTarget(targetId : String)

    suspend fun updateTargetProgress(targetId: String, progress: Float)
}