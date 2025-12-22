package com.aristopharma.v2.feature.auth.data.model

import kotlinx.serialization.Serializable


/**
 * Internal login model for the auth feature module.
 * Stores authentication credentials and session information.
 */
@Serializable
data class LoginModel(
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

