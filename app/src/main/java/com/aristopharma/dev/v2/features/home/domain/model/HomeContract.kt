package com.aristopharma.dev.v2.features.home.domain.model

sealed class HomeUiEvent {
    object Logout : HomeUiEvent()
    object OnLogoutConfirmed : HomeUiEvent()
    object OnLogoutCancelled : HomeUiEvent()
}

data class HomeUiState(
    val showLogoutDialog: Boolean = false,
    val isLoggedOut: Boolean = false
)
