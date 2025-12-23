package com.aristopharma.dev.v2.features.dashboard.domain.model

data class DashboardSummary(
    val employeeName: String,
    val employeeId: String,
    val attendanceStatus: String,
    val lastSyncTime: String,
    val isFirstSyncDone: Boolean,
    val postOrderCount: Int = 0,
    val dcrCount: Int = 0,
    val hasPendingApproval: Boolean = false
)

data class MenuPermission(
    val title: String,
    val dashboardItem: DashboardItem,
    val isRedDotVisible: Boolean = false,
    val sequence: Int = 0
)

enum class DashboardItem {
    START_YOUR_DAY,
    POST_ORDER,
    POST_SPECIAL_ORDER,
    ORDER_HISTORY_USER,
    ORDER_HISTORY_MANAGER,
    MANAGER_LIVE_LOCATION,
    ATTENDANCE_REPORT,
    LEAVE_MANAGEMENT,
    LEAVE,
    DRAFT_ORDER,
    SALES_SUMMARY_REPORT,
    PRODUCT_SALES_REPORT,
    CHEMIST_SALES_REPORT
}
