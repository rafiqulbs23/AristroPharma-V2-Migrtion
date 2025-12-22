package com.aristopharma.dev.v2.features.splash.domain.model

sealed class SplashUiState {
    object Idle : SplashUiState()
    object NavigateToHome : SplashUiState()
    object NavigateToLogin : SplashUiState()
}
