package com.expeknow.ariselauncher.data.datasource

import com.expeknow.ariselauncher.data.database.dao.TargetsDao
import com.expeknow.ariselauncher.data.datasource.interfaces.TargetsDataSource
import com.expeknow.ariselauncher.data.model.Targets
import kotlinx.coroutines.flow.Flow

class OfflineTargetsDataSource(
    private val targetDao : TargetsDao) : TargetsDataSource {

    override fun getAllTargets(): Flow<List<Targets>> {
        return targetDao.getAllTargets()
    }

    override suspend fun addTarget(target: Targets) {
        targetDao.insertTarget(target)
    }

    override suspend fun updateTarget(target: Targets) {
        targetDao.updateTarget(target)
    }

    override suspend fun deleteTarget(targetId: String) {
        targetDao.deleteTarget(targetId)
    }

    override suspend fun updateTargetProgress(targetId: String, progress: Float) {
        targetDao.updateTargetProgress(targetId, progress)
    }

}
