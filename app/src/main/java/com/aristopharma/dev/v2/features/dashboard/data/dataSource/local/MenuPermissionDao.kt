package com.aristopharma.dev.v2.features.dashboard.data.dataSource.local

import androidx.room.*
import com.aristopharma.dev.v2.features.dashboard.data.model.MenuPermissionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MenuPermissionDao {
    @Query("SELECT * FROM menu_permissions ORDER BY sequence ASC")
    fun getMenuPermissions(): Flow<List<MenuPermissionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuPermissions(permissions: List<MenuPermissionEntity>)

    @Query("DELETE FROM menu_permissions")
    suspend fun clearMenuPermissions()
}
