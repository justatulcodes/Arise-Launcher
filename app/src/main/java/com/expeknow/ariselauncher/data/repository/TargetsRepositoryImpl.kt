package com.expeknow.ariselauncher.data.repository

import com.expeknow.ariselauncher.data.datasource.interfaces.TargetsDataSource
import com.expeknow.ariselauncher.data.model.Targets
import com.expeknow.ariselauncher.data.repository.interfaces.TargetsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TargetsRepositoryImpl @Inject constructor(
    private val targetsDataSource: TargetsDataSource
) : TargetsRepository {
    override fun getAllTargets(): Flow<List<Targets>> {
        return targetsDataSource.getAllTargets()
    }

    override suspend fun addTarget(target: Targets) {
        return targetsDataSource.addTarget(target)
    }

    override suspend fun updateTarget(target: Targets) {
        return targetsDataSource.updateTarget(target)
    }

    override suspend fun deleteTarget(targetId: String) {
        return targetsDataSource.deleteTarget(targetId = targetId)
    }

    override suspend fun updateTargetProgress(targetId: String, progress: Float) {
        return targetsDataSource.updateTargetProgress(targetId, progress)
    }


}