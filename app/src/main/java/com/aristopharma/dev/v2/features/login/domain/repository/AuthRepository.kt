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

package com.aristopharma.dev.v2.features.login.domain.repository

import com.aristopharma.dev.v2.features.login.data.model.LoginModel
import com.aristopharma.v2.feature.auth.data.model.LoginPostModel
import com.aristopharma.dev.v2.features.login.data.model.LoginResponseModel
import com.aristopharma.v2.feature.auth.data.model.OTPValidationRequest
import com.aristopharma.v2.feature.auth.data.model.OTPValidationResponse

/**
 * Interface defining authentication-related operations.
 */
interface AuthRepository {
    /**
     * Login with employee ID and FCM token to receive OTP.
     *
     * @param model The login request model containing empId and fcmToken.
     * @return A [Result] representing the login operation result. It contains [LoginResponseModel] if
     * the login was successful, or an error if there was a problem.
     */
    suspend fun login(model: LoginPostModel): Result<LoginResponseModel>

    /**
     * Validate OTP code.
     *
     * @param model The OTP validation request model containing empId and otp.
     * @return A [Result] representing the OTP validation result. It contains [OTPValidationResponse] if
     * the validation was successful, or an error if there was a problem.
     */
    suspend fun validateOTP(model: OTPValidationRequest): Result<OTPValidationResponse>

    /**
     * Save login credentials locally.
     *
     * @param loginModel The login model to save.
     */
    suspend fun saveLoginModel(loginModel: LoginModel)

    /**
     * Get saved login credentials.
     *
     * @return The saved login model, or null if not found.
     */
    suspend fun getLoginModel(): LoginModel?

    /**
     * Clear saved login credentials.
     */
    suspend fun clearLoginModel()

    /**
     * Check if user is logged in.
     *
     * @return true if user is logged in, false otherwise.
     */
    suspend fun isLoggedIn(): Boolean

    /**
     * Update FCM token on the server.
     *
     * @param token The new FCM token.
     * @return A [Result] representing the update operation result.
     */
    suspend fun updateFcmToken(token: String): Result<Unit>
}



