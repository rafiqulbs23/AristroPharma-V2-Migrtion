/*
 * it.copyright 2025 Md. Rafiqul Islam
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a it.copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aristopharma.dev.v2.features.login.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.copy
import com.aristopharma.core.BuildConfig
import com.aristopharma.core.base.BaseViewModel
import com.aristopharma.core.utils.OneTimeEvent
import com.aristopharma.core.utils.TextFiledData
import com.aristopharma.dev.v2.features.login.domain.model.SignInEvent
import com.aristopharma.dev.v2.features.login.domain.model.SignInState
import com.aristopharma.dev.v2.features.login.domain.repository.AuthRepository

import com.aristopharma.v2.feature.auth.data.model.LoginModel
import com.aristopharma.v2.feature.auth.data.model.LoginPostModel
import com.aristopharma.v2.feature.auth.data.model.OTPValidationRequest
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import javax.inject.Inject
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


/**
 * ViewModel for OTP-based authentication.
 *
 * Handles login/signup flow with employee ID, password, and OTP validation.
 */
@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : BaseViewModel() {


    private val _uiState = MutableStateFlow(SignInState())
    val uiState: StateFlow<SignInState> = _uiState.asStateFlow()

    init {
        _uiState.update { inState: SignInState ->
            inState.copy(
                isBypassOTP = BuildConfig.ENABLE_BYPASS_OTP,
                isVisibleByPassOTP = BuildConfig.IS_AUTOMIC_OTP_ENABLE
            )
        }
    }

    private var loginModel: LoginModel = LoginModel()

    /**
     * Handles UI events to mutate state or trigger side effects.
     */
    fun onEvent(event: SignInEvent) {
        when (event) {
            is SignInEvent.FetchModel -> fetchModel()
            is SignInEvent.UpdateEmpId -> updateEmpId(event.empId)
            is SignInEvent.UpdatePassword -> updatePassword(event.password)
            is SignInEvent.UpdateConfirmPassword -> updateConfirmPassword(event.confirmPassword)
            is SignInEvent.UpdateOTP -> updateOTP(event.otp)
            is SignInEvent.UpdateBypassOTP -> updateBypassOTP(event.enabled)
            is SignInEvent.ValidateAndSignUp -> validateAndSignUp(
                event.empId,
                event.password,
                event.confirmPassword,
            )
            is SignInEvent.ValidateOTP -> validateOTP(event.otp)
            is SignInEvent.DeviceLogin -> deviceLogin(event.empId, event.password, event.openDashboard)
            is SignInEvent.Login -> validateAndSignIn(event.empId, event.password,)
            is SignInEvent.LoginBypass -> loginBypass(event.empId, event.password)
            is SignInEvent.DeleteAllData -> deleteAllData()
            is SignInEvent.ShowSignUpUi -> showSignUpUi()
            is SignInEvent.ResetError -> {
                _uiState.update {currentState: SignInState ->
                    currentState.copy(
                        error = OneTimeEvent(null)
                    )
                }
            }
        }
    }

    /**
     * Fetches saved login model and initializes UI state.
     */
      fun  fetchModel() {

        viewModelScope.launch {
            val savedModel = authRepository.getLoginModel()
            loginModel = savedModel ?: LoginModel()
            
            _uiState.update {currentState: SignInState ->
                currentState.copy(
                    empId = TextFiledData(loginModel.empId),
                    password = TextFiledData(loginModel.password),
                )
            }
        }
    }

    /**
     * Updates employee ID in the state.
     */
    fun updateEmpId(empId: String) {
        _uiState.update { currentState: SignInState ->
            currentState.copy(
            empId = TextFiledData(
                empId
            ))
        }
    }

    /**
     * Updates password in the state.
     */
    fun updatePassword(password: String) {
        _uiState.update{currentState: SignInState ->
            currentState.copy(password = TextFiledData(password))
        }
    }

    /**
     * Updates confirm password in the state.
     */
    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.update {currentState: SignInState ->
            currentState.copy(confirmPassword = TextFiledData(confirmPassword))
        }
    }

    /**
     * Updates OTP in the state.
     */
    fun updateOTP(otp: String) {
        _uiState.update {currentState: SignInState ->
            currentState.copy(otp = TextFiledData(otp))
        }
    }

    /**
     * Validates input and initiates sign up process.
     */
    fun validateAndSignUp(user: String, password: String, confirmPassword: String) {
        val validation = validateInput(user, password, confirmPassword)
        if (validation != null) {
            _uiState.update {currentState: SignInState ->
                currentState.copy(
                    error = OneTimeEvent(IllegalArgumentException(validation)),
                )
            }
            return
        }

        loginModel = loginModel.copy(
            empId = user,
            password = password,
        )

        generateFCMTokenAndSignUp()
    }

    /**
     * Validates input and initiates sign in process.
     */
    fun validateAndSignIn(user: String, password: String) {
        val validation = validateInput(user, password,)
        if (validation != null) {
            _uiState.update {currentState: SignInState ->
                currentState.copy(
                    error = OneTimeEvent(IllegalArgumentException(validation)),
                )
            }
            return
        }

        loginModel = loginModel.copy(
            empId = user,
            password = password,
        )

        generateFCMTokenAndSignUp()
    }

    /**
     * Validates user input for sign up.
     */
    private fun validateInput(user: String, password: String, confirmPassword: String): String? {
        return when {
            user.isEmpty() -> "User cannot be empty"
            password.isEmpty() || confirmPassword.isEmpty() -> "Password cannot be empty"
            password.length < 4 -> "Password must be at least 4 digits"
            password != confirmPassword -> "Passwords don't match"
            else -> null
        }
    }

    /**
     * Validates user input for sign in.
     */
    private fun validateInput(user: String, password: String): String? {
        return when {
            user.isEmpty() -> "User cannot be empty"
            password.isEmpty()  -> "Password cannot be empty"
            password.length < 4 -> "Password must be at least 4 digits"
            else -> null
        }
    }

    /**
     * Generates FCM token and initiates sign up.
     */
    private fun generateFCMTokenAndSignUp() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val isBypassOTP = _uiState.value.isBypassOTP
            val fcmToken = if (isBypassOTP) {
                // Dummy FCM token for bypass mode
                "dHUS-WkLTguhZN9DyYiNAc:APA91bEhZ4CxcswbCattBu5LFYML5UyEmQi1J-870R8TwLMiXHOTedwIdJg0grDN815m3jbOVF0Mb0JzcaE7CDUOf9ttuV8RFWq9lOVdJcxYOayICf3v2wWgyKEe6oj3PN8_m4AyJT7X"
            } else {
                getFCMToken()
            }

            if (fcmToken == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = OneTimeEvent(Exception("Failed to get FCM token")),
                    )
                }
                return@launch
            }

            loginModel = loginModel.copy(fcmToken = fcmToken)
            signUp(empId = loginModel.empId, fcmToken = fcmToken)
        }
    }

    /**
     * Retrieves FCM token using FcmTokenManager from notification feature.
     */
    private suspend fun getFCMToken(): String? {
        // TODO: Implement actual FCM token retrieval
        return "ffgfgjfgjfjffjgfjgfjfjf"
    }

    /**
     * Performs sign up/login with employee ID and FCM token.
     */
    private fun signUp(empId: String, fcmToken: String) {
        viewModelScope.launch {
            val result = authRepository.login(LoginPostModel(userName = empId, fcmToken = fcmToken))

            result.fold(
                onSuccess = { response ->
                    loginModel = loginModel.copy(
                        empId = empId,
                        empName = response.name,
                        mobileNo = response.mobileNo,
                        accessToken = response.token,
                        refreshToken = response.refreshToken,
                        otp = response.otpCode?.toString() ?: "",
                        isFirstLogin = response.isFirstLogin,
                        userRoleType = response.userRoleType,
                    )

                    authRepository.saveLoginModel(loginModel)

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isWaitingForSMS = OneTimeEvent(true),
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = OneTimeEvent(Exception(error.message)),
                        )
                    }
                },
            )
        }
    }

    /**
     * Validates OTP code.
     */
    private fun validateOTP(otp: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val empIdInt = loginModel.empId.toIntOrNull()
            val otpInt = otp.toIntOrNull()

            if (empIdInt == null || otpInt == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = OneTimeEvent(IllegalArgumentException("Invalid employee ID or OTP")),
                    )
                }
                return@launch
            }

            val result = authRepository.validateOTP(OTPValidationRequest(emp_Id = empIdInt, otp = otpInt))

            result.fold(
                onSuccess = { response ->
                    if (response.is_Validated) {
                        loginModel = loginModel.copy(
                            isLoggedIn = true,
                            isSignedUp = true,
                        )
                        authRepository.saveLoginModel(loginModel)

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isSignUpSuccessful = true,
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = OneTimeEvent(Exception("OTP validation failed")),
                            )
                        }
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = OneTimeEvent(Exception(error.message)),
                        )
                    }
                },
            )
        }
    }

    /**
     * Login bypass for testing purposes.
     */
    private fun loginBypass(empId: String, password: String) {
        if (empId.isEmpty() || password.isEmpty()) {
            _uiState.update {
                it.copy(
                    error = OneTimeEvent(IllegalArgumentException("Employee ID or password is empty")),
                )
            }
            return
        }

        loginModel = loginModel.copy(
            empId = empId,
            password = password,
        )

        val dummyFCM = "dHUS-WkLTguhZN9DyYiNAc:APA91bEhZ4CxcswbCattBu5LFYML5UyEmQi1J-870R8TwLMiXHOTedwIdJg0grDN815m3jbOVF0Mb0JzcaE7CDUOf9ttuV8RFWq9lOVdJcxYOayICf3v2wWgyKEe6oj3PN8_m4AyJT7X"
        mockSignup(empId = empId, fcmToken = dummyFCM)
    }

    /**
     * Mock signup for bypass mode.
     */
    private fun mockSignup(empId: String, fcmToken: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = authRepository.login(LoginPostModel(userName = empId, fcmToken = fcmToken))

            result.fold(
                onSuccess = { response ->
                    loginModel = loginModel.copy(
                        empId = empId,
                        empName = response.name,
                        accessToken = response.token,
                        refreshToken = response.refreshToken,
                        otp = response.otpCode?.toString() ?: "",
                        isLoggedIn = true,
                        isSignedUp = true,
                    )

                    authRepository.saveLoginModel(loginModel)

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSignUpSuccessful = true,
                            isWaitingForSMS = OneTimeEvent(true),
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = OneTimeEvent(Exception(error.message)),
                        )
                    }
                },
            )
        }
    }

    /**
     * Device login with saved credentials.
     */
    fun deviceLogin(user: String, password: String, openDashboard: () -> Unit) {
        when (loginModel.isSignedUp) {
            true -> {
                when (password.isNotEmpty() && loginModel.password == password && loginModel.empId == user) {
                    true -> {
                        loginModel = loginModel.copy(isLoggedIn = true)
                        viewModelScope.launch {
                            authRepository.saveLoginModel(loginModel)
                            openDashboard()
                        }
                    }
                    false -> {
                        _uiState.update {
                            it.copy(
                                error = OneTimeEvent(Exception("Credentials don't match")),
                            )
                        }
                    }
                }
            }
            false -> {
                _uiState.update {
                    it.copy(
                        error = OneTimeEvent(Exception("You are not logged in, please sign up")),
                    )
                }
            }
        }
    }

    /**
     * Shows sign up UI (hides login button, shows verify button).
     */
    private fun showSignUpUi() {
        _uiState.update {
            it.copy(
                isLoginBtnVisible = false,
                isSignUpBtnVisible = false,
                isVerifyBtnVisible = true,
            )
        }
    }

    /**
     * Updates the bypass OTP setting.
     *
     * @param enabled Whether to enable bypass OTP mode.
     */
    private fun updateBypassOTP(enabled: Boolean) {
        _uiState.update {
            it.copy(isBypassOTP = enabled)
        }
    }

    /**
     * Deletes all data (for testing/debugging).
     */
    private fun deleteAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.clearLoginModel()
        }
    }
}
