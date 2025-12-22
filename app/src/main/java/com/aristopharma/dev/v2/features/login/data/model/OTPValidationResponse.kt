package com.aristopharma.v2.feature.auth.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OTPValidationResponse(
    @SerialName("is_Validated") val is_Validated: Boolean,
)