package com.aristopharma.dev.v2.features.dashboard.data.remote

import com.aristopharma.dev.v2.features.dashboard.data.model.DashboardBaseResponse
import com.aristopharma.dev.v2.features.dashboard.data.model.FirstSyncDto
import com.aristopharma.dev.v2.features.dashboard.data.model.MenuPermissionDto
import com.aristopharma.dev.v2.features.dashboard.data.model.NoticeDto
import retrofit2.http.GET
import retrofit2.http.Query

interface DashboardService {
    @GET("/api/v1/app/sync/first-sync")
    suspend fun getFirstSync(
        @Query("EmpId") employeeId: String,
    ): DashboardBaseResponse<FirstSyncDto>

    @GET("/api/v1/Menu/app-menu-permission")
    suspend fun getAppMenuPermission(): DashboardBaseResponse<List<MenuPermissionDto>?>

    @GET("/api/v1/broadcast/all-notification")
    suspend fun getNoticeList(
        @Query("Emp_Id") employeeId: String,
    ): DashboardBaseResponse<List<NoticeDto>?>
}
