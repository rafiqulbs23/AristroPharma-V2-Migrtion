package com.aristopharma.dev.v2.features.dashboard.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "menu_permissions")
data class MenuPermissionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val sequence: Int
)
