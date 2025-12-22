package com.aristopharma.v2.feature.auth.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OTPValidationRequest(
    @SerialName("emp_Id") val emp_Id: Int,
    @SerialName("otp") val otp: Int,
)