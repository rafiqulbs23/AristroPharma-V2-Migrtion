package com.aristopharma.dev.v2.features.login.data.dataSource.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "login")
data class LoginEntity(
    @PrimaryKey val id: Int = 1, // single row (current session/user)

    val phoneNumber: String = "",
    val fcmToken: String = "",
    val empId: String = "",
    val empName: String = "",
    val mobileNo: String = "",
    val password: String = "",
    val accessToken: String = "",
    val refreshToken: String = "",
    val otp: String = "",
    val isSignedUp: Boolean = false,
    val isLoggedIn: Boolean = false,
    val isFirstSync: Boolean = false,
    val isFirstLogin: Boolean = false,
    val userRoleType: String = "",
)
