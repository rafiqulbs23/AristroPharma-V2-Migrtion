package com.aristopharma.v2.feature.auth.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginPostModel(
    @SerialName("userName") val userName: String,
    @SerialName("fcmToken") val fcmToken: String,
)