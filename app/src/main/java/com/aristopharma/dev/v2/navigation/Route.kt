package com.aristopharma.dev.v2.navigation

import kotlinx.serialization.Serializable

@Serializable
object SplashScreenNav

@Serializable
object HomeScreenNav

@Serializable
object SignInScreenNav

@Serializable
data class OtpScreenNav(
    val username: String,
    val password: String
)
