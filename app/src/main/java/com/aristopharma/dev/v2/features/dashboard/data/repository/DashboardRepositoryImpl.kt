package com.aristopharma.dev.v2.features.dashboard.data.repository

import com.aristopharma.dev.v2.features.dashboard.domain.repository.DashboardRepository
import com.aristopharma.dev.v2.core.utils.sharedPref.Prefs
import com.aristopharma.dev.v2.features.dashboard.domain.model.*
import com.aristopharma.dev.v2.features.dashboard.data.remote.DashboardService
import com.aristopharma.dev.v2.features.dashboard.data.dataSource.local.MenuPermissionDao
import com.aristopharma.dev.v2.features.dashboard.data.model.MenuPermissionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DashboardRepositoryImpl @Inject constructor(
    private val menuPermissionDao: MenuPermissionDao,
    private val dashboardService: DashboardService,
    private val prefs: Prefs
) : DashboardRepository {

    override fun getDashboardSummary(): Flow<DashboardSummary> {
        return menuPermissionDao.getMenuPermissions().map { _ ->
            val model = prefs.dashboardSummaryModel
            val fallbackId = prefs.empId?.takeIf { it.isNotEmpty() } ?: prefs.loginModel.empId
            val fallbackName = prefs.loginModel.empName
            DashboardSummary(
                employeeName = model.employeeName.takeIf { it.isNotEmpty() } ?: fallbackName,
                employeeId = model.employeeId.takeIf { it.isNotEmpty() } ?: fallbackId,
                attendanceStatus = getAttendanceStatus(),
                lastSyncTime = model.lastSyncTime,
                isFirstSyncDone = model.isFirstSyncDone,
                postOrderCount = prefs.postOrderInfo.count,
                dcrCount = 0,
                hasPendingApproval = prefs.hasPendingOrderApprovalRedDot
            )
        }
    }

    override fun getMenuPermissions(): Flow<List<MenuPermission>> {
        val dashboardMap = mapOf(
            "Draft Order" to DashboardItem.DRAFT_ORDER,
            "Post Order" to DashboardItem.POST_ORDER,
            "Post Special Order" to DashboardItem.POST_SPECIAL_ORDER,
            "Order History" to DashboardItem.ORDER_HISTORY_USER,
            "Order History (User)" to DashboardItem.ORDER_HISTORY_USER,
            "Order History (Manager)" to DashboardItem.ORDER_HISTORY_MANAGER,
            "Leave Management" to DashboardItem.LEAVE_MANAGEMENT,
            "Leave" to DashboardItem.LEAVE,
            "Live Location" to DashboardItem.MANAGER_LIVE_LOCATION,
            "Attendance Report" to DashboardItem.ATTENDANCE_REPORT,
            "Attendance" to DashboardItem.START_YOUR_DAY,
            "Chemist Sales Report" to DashboardItem.CHEMIST_SALES_REPORT,
            "Product Sales Report" to DashboardItem.PRODUCT_SALES_REPORT,
            "Sales Summary Report" to DashboardItem.SALES_SUMMARY_REPORT,
        )

        return menuPermissionDao.getMenuPermissions().map { permissions ->
            permissions.map { perm ->
                val item = dashboardMap[perm.title] ?: DashboardItem.DRAFT_ORDER
                MenuPermission(
                    title = perm.title,
                    dashboardItem = item,
                    isRedDotVisible = if (item == DashboardItem.ORDER_HISTORY_MANAGER) prefs.hasPendingOrderApprovalRedDot else false,
                    sequence = perm.sequence
                )
            }.sortedBy { it.sequence }
        }
    }

    override fun getNotices(): Flow<List<DashboardNotice>> = flow {
        try {
            // Priority: prefs.empId > prefs.loginModel.empId
            val employeeId = prefs.empId?.takeIf { it.isNotEmpty() } 
                ?: prefs.loginModel.empId.takeIf { it.isNotEmpty() }
            
            if (employeeId.isNullOrEmpty()) {
                emit(emptyList())
                return@flow
            }

            val response = dashboardService.getNoticeList(employeeId)
            if (response.statusCode == 200) {
                val notices = response.data?.map {
                    DashboardNotice(it.title ?: "", it.body ?: "", "")
                } ?: emptyList()
                emit(notices)
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override suspend fun syncNow(employeeId: String): Result<Unit> {
        if (employeeId.isEmpty()) return Result.failure(Exception("Employee ID is empty"))
        return try {
            // 1. Fetch First Sync Data (Employee Info, etc.)
            val firstSyncResponse = dashboardService.getFirstSync(employeeId)
            if (firstSyncResponse.statusCode == 200 && firstSyncResponse.data != null) {
                val empInfo = firstSyncResponse.data.employeeInfo
                if (empInfo != null) {
                    prefs.dashboardSummaryModel = prefs.dashboardSummaryModel.copy(
                        employeeName = empInfo.surName ?: prefs.dashboardSummaryModel.employeeName,
                        employeeId = empInfo.empId ?: employeeId
                    )
                }
                
                // Mark sync as done early to prevent loop
                prefs.dashboardSummaryModel = prefs.dashboardSummaryModel.copy(isFirstSyncDone = true)
                
                // 2. Fetch Menu Permissions
                val menuResponse = dashboardService.getAppMenuPermission()
                if (menuResponse.statusCode == 200 && menuResponse.data != null) {
                    val entities = menuResponse.data
                        .filter { it.isEnabled ?: true } // Treat null as enabled
                        .mapIndexed { index, dto ->
                            MenuPermissionEntity(
                                title = dto.title ?: "",
                                sequence = dto.sequence ?: index
                            )
                        }
                    menuPermissionDao.clearMenuPermissions()
                    menuPermissionDao.insertMenuPermissions(entities)
                }
                
                Result.success(Unit)
            } else {
                Result.failure(Exception(firstSyncResponse.message ?: "Sync failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        prefs.loginModel = prefs.loginModel.apply {
            isLoggedIn = false
        }
    }

    override suspend fun deleteAccount(): Result<Unit> {
        return try {
            // TODO: Implement proper account deletion when endpoint is found
            Result.failure(Exception("Account deletion not implemented"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getAttendanceStatus(): String {
        return when (prefs.attendanceModel.session) {
            AttendanceSession.SESSION_STAGE -> "Idle"
            AttendanceSession.SESSION_CHECK_IN -> "Checked In"
            AttendanceSession.SESSION_CHECK_OUT -> "Checked Out"
        }
    }
}
