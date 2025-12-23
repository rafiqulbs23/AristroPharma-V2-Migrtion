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

package com.aristopharma.dev.v2.features.login.data.repository

import com.aristopharma.core.base.BaseResponse
import com.aristopharma.dev.v2.features.login.data.dataSource.local.LoginDao
import com.aristopharma.dev.v2.features.login.data.dataSource.local.toEntity
import com.aristopharma.dev.v2.features.login.data.dataSource.local.toModel
import com.aristopharma.dev.v2.features.login.data.dataSource.remote.AuthApiService
import com.aristopharma.dev.v2.features.login.data.dataSource.remote.UpdateFcmTokenRequest
import com.aristopharma.dev.v2.features.login.domain.repository.AuthRepository
import com.aristopharma.dev.v2.features.login.data.model.LoginModel
import com.aristopharma.v2.feature.auth.data.model.LoginPostModel
import com.aristopharma.dev.v2.features.login.data.model.LoginResponseModel
import com.aristopharma.v2.feature.auth.data.model.OTPValidationRequest
import com.aristopharma.v2.feature.auth.data.model.OTPValidationResponse
import com.aristopharma.core.data.repository.TokenRepository
import com.aristopharma.dev.v2.core.utils.sharedPref.Prefs
import com.aristopharma.dev.v2.features.dashboard.domain.model.DashboardSummary
import javax.inject.Inject

/**
 * Implementation of the [AuthRepository] interface responsible for handling authentication operations.
 *
 * @param loginDao The local data source for storing user data.
 * @param apiService The remote data source for authentication operations.
 * @param prefs The shared preferences for application-wide session data.
 * @param tokenRepository The core repository for managing auth tokens and device-level session data.
 */
class AuthRepositoryImpl @Inject constructor(
    private val loginDao: LoginDao,
    private val apiService: AuthApiService,
    private val prefs: Prefs,
    private val tokenRepository: TokenRepository
) : AuthRepository {

    override suspend fun login(model: LoginPostModel): Result<LoginResponseModel> {
        return try {
            val response = apiService.login(model).data
                ?: throw IllegalStateException("No response received from server")

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun validateOTP(model: OTPValidationRequest): Result<OTPValidationResponse> {
        return try {
            val response = apiService.validateOTP(model).data
                ?: throw IllegalStateException("No response received from server")

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Validates the response and extracts the data if successful.
     *
     * @param response The response to validate.
     * @param operationName The name of the operation for error messages.
     * @return The data from the response if successful.
     * @throws IllegalStateException if the response indicates failure.
     */
    private fun <T> validateAndExtractResponse(
        response: BaseResponse<T>,
        operationName: String,
    ): T {
        // Check for success case first
        if (response.statusCode in 200..299 && response.data != null) {
            return response.data as T
        }
        
        // Check for HTTP error status codes
        if (response.statusCode != null && response.statusCode !in 200..299) {
            val errorMessage = getErrorMessage(
                response,
                operationName,
                "Status code ${response.statusCode}",
            )
            throw IllegalStateException(errorMessage)
        }
        
        // Check for missing data
        if (response.data == null) {
            val errorMessage = getErrorMessage(response, operationName, "No data received")
            throw IllegalStateException(errorMessage)
        }
        
        // Unknown error case
        throw IllegalStateException("$operationName failed: Unknown error")
    }

    /**
     * Extracts error message from response with fallback options.
     *
     * @param response The response containing error information.
     * @param operationName The name of the operation.
     * @param fallbackMessage The fallback message if no error details are available.
     * @return The error message.
     */
    private fun <T> getErrorMessage(
        response: BaseResponse<T>,
        operationName: String,
        fallbackMessage: String,
    ): String {
        return response.message
            ?: response.error?.joinToString(", ")
            ?: "$operationName failed: $fallbackMessage"
    }

    override suspend fun saveLoginModel(loginModel: LoginModel) {
        try {
            // 1. Save to Room (Database)
            loginDao.save(loginModel.toEntity())
            
            // 2. Sync with Prefs (SharedPreferences) - For features relying on legacy SharedPreferences
            prefs.loginModel = loginModel
            if (loginModel.empId.isNotEmpty()) {
                prefs.empId = loginModel.empId
            }
            
            // 3. Sync with TokenRepository (DataStoreManager) - Critical for NetworkFactory token interceptor
            if (loginModel.accessToken.isNotEmpty()) {
                tokenRepository.saveToken(loginModel.accessToken)
            }
            if (loginModel.empId.isNotEmpty()) {
                tokenRepository.saveEmpId(loginModel.empId)
            }
            tokenRepository.setLoggedOut(!loginModel.isLoggedIn)
            
            // 4. Initialize DashboardSummary if it's the first time or missing critical info
            if (prefs.dashboardSummaryModel.employeeId.isEmpty() && loginModel.empId.isNotEmpty()) {
                prefs.dashboardSummaryModel = prefs.dashboardSummaryModel.copy(
                    employeeName = loginModel.empName,
                    employeeId = loginModel.empId
                )
            }
        } catch (e: Exception) {
            throw IllegalStateException("Failed to save login model", e)
        }
    }

    override suspend fun getLoginModel(): LoginModel? {
        return try {
            loginDao.getLogin()?.toModel()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun clearLoginModel() {
        try {
            loginDao.clear()
            tokenRepository.clearToken()
            tokenRepository.setLoggedOut(true)
        } catch (e: Exception) {
            throw IllegalStateException("Failed to clear login model", e)
        }
    }

    override suspend fun isLoggedIn(): Boolean {
        return try {
            loginDao.getLogin()?.isLoggedIn ?: false
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun updateFcmToken(token: String): Result<Unit> {
        return try {
            val response = apiService.updateFcmToken(UpdateFcmTokenRequest(fcmToken = token))
            if (response.statusCode in 200..299) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message ?: "Failed to update FCM token"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
