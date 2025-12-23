package com.aristopharma.dev.v2.features.dashboard.domain.model

data class AttendanceModel(
    val session: AttendanceSession = AttendanceSession.SESSION_STAGE
)

enum class AttendanceSession {
    SESSION_STAGE,
    SESSION_CHECK_IN,
    SESSION_CHECK_OUT
}

data class PostOrderInfo(
    val count: Int = 0
)

data class EmployeeInfo(
    val empId: String = "",
    val name: String = ""
)

data class DashboardNotice(
    val title: String,
    val description: String,
    val date: String
)
