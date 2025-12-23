/*
 * Copyright 2025 Md. Rafiqul Islam
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aristopharma.dev.v2.features.login.data.dataSource.remote

import com.aristopharma.core.base.BaseResponse
import com.aristopharma.v2.feature.auth.data.model.LoginPostModel
import com.aristopharma.dev.v2.features.login.data.model.LoginResponseModel
import com.aristopharma.v2.feature.auth.data.model.OTPValidationRequest
import com.aristopharma.v2.feature.auth.data.model.OTPValidationResponse
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit API service for authentication operations.
 */
interface AuthApiService {
    /**
     * Login with employee ID and FCM token to receive OTP.
     *
     * @param model The login request model containing userName and fcmToken.
     * @return A [BaseResponse] containing [LoginResponseModel] with OTP code.
     */
    @POST("/api/v1/app/auth/login")
    suspend fun login(
        @Body model: LoginPostModel,
    ): BaseResponse<LoginResponseModel>

    /**
     * Validate OTP code.
     *
     * @param model The OTP validation request model containing empId and otp.
     * @return A [BaseResponse] containing [OTPValidationResponse] with validation result.
     */
    @POST("/api/v1/app/auth/ValidateOtp")
    suspend fun validateOTP(
        @Body model: OTPValidationRequest,
    ): BaseResponse<OTPValidationResponse>

    /**
     * Update FCM token for the authenticated user.
     *
     * @param model The request model containing the new FCM token.
     * @return A [BaseResponse] indicating success or failure.
     */
    @POST("/api/v1/app/auth/updateFcmToken")
    suspend fun updateFcmToken(
        @Body model: UpdateFcmTokenRequest,
    ): BaseResponse<Unit>
}

@Serializable
data class UpdateFcmTokenRequest(
    val fcmToken: String
)

