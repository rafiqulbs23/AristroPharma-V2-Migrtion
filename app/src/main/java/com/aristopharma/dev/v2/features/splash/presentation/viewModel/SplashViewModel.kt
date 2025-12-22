package com.aristopharma.dev.v2.features.splash.presentation.viewModel

import androidx.lifecycle.viewModelScope
import com.aristopharma.core.base.BaseViewModel
import com.aristopharma.dev.v2.features.login.domain.repository.AuthRepository
import com.aristopharma.dev.v2.features.splash.domain.model.SplashUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Idle)
    val uiState = _uiState.asStateFlow()

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            delay(2000) // 2-second delay for splash screen
            if (authRepository.isLoggedIn()) {
                _uiState.value = SplashUiState.NavigateToHome
            } else {
                _uiState.value = SplashUiState.NavigateToLogin
            }
        }
    }
}