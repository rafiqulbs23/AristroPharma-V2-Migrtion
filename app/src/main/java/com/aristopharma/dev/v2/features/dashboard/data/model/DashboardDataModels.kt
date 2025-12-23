package com.aristopharma.dev.v2.features.dashboard.data.model

import com.google.gson.annotations.SerializedName

data class DashboardBaseResponse<T>(
    @SerializedName("status") val status: String? = null,
    @SerializedName("statusCode") val statusCode: Int? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: T? = null
)

data class FirstSyncDto(
    @SerializedName("employeeInfo") val employeeInfo: EmployeeInfoDto? = null
)

data class EmployeeInfoDto(
    @SerializedName("empId") val empId: String? = null,
    @SerializedName("surName") val surName: String? = null
)

data class MenuPermissionDto(
    @SerializedName("title") val title: String? = null,
    @SerializedName("sequence") val sequence: Int? = null,
    @SerializedName("isEnable") val isEnabled: Boolean? = null
)

data class NoticeDto(
    @SerializedName("noticeTitle") val title: String? = null,
    @SerializedName("noticeBody") val body: String? = null
)
