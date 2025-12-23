package com.aristopharma.dev.v2.features.dashboard.domain.repository

import com.aristopharma.dev.v2.features.dashboard.domain.model.DashboardNotice
import com.aristopharma.dev.v2.features.dashboard.domain.model.DashboardSummary
import com.aristopharma.dev.v2.features.dashboard.domain.model.MenuPermission
import kotlinx.coroutines.flow.Flow

interface DashboardRepository {
    fun getDashboardSummary(): Flow<DashboardSummary>
    fun getMenuPermissions(): Flow<List<MenuPermission>>
    fun getNotices(): Flow<List<DashboardNotice>>
    suspend fun syncNow(employeeId: String): Result<Unit>
    suspend fun logout()
    suspend fun deleteAccount(): Result<Unit>
}
