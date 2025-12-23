package com.aristopharma.dev.v2.features.login.data.dataSource.local

import com.aristopharma.dev.v2.features.login.data.model.LoginModel

fun LoginEntity.toModel() = LoginModel(
    phoneNumber = phoneNumber,
    fcmToken = fcmToken,
    empId = empId,
    empName = empName,
    mobileNo = mobileNo,
    password = password,
    accessToken = accessToken,
    refreshToken = refreshToken,
    otp = otp,
    isSignedUp = isSignedUp,
    isLoggedIn = isLoggedIn,
    isFirstSync = isFirstSync,
    isFirstLogin = isFirstLogin,
    userRoleType = userRoleType,
)

fun LoginModel.toEntity() = LoginEntity(
    id = 1,
    phoneNumber = phoneNumber,
    fcmToken = fcmToken,
    empId = empId,
    empName = empName,
    mobileNo = mobileNo,
    password = password,
    accessToken = accessToken,
    refreshToken = refreshToken,
    otp = otp,
    isSignedUp = isSignedUp,
    isLoggedIn = isLoggedIn,
    isFirstSync = isFirstSync,
    isFirstLogin = isFirstLogin,
    userRoleType = userRoleType,
)
