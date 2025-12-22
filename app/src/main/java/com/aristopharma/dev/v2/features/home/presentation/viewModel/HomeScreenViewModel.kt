package com.aristopharma.dev.v2.features.home.presentation.viewModel

import androidx.lifecycle.viewModelScope
import com.aristopharma.core.base.BaseViewModel
import com.aristopharma.dev.v2.features.home.domain.model.HomeUiEvent
import com.aristopharma.dev.v2.features.home.domain.model.HomeUiState
import com.aristopharma.dev.v2.features.login.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository
): BaseViewModel(){

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            HomeUiEvent.Logout -> {
                _uiState.update { it.copy(showLogoutDialog = true) }
            }
            HomeUiEvent.OnLogoutConfirmed -> {
                _uiState.update { it.copy(showLogoutDialog = false) }
                logout()
            }
            HomeUiEvent.OnLogoutCancelled -> {
                _uiState.update { it.copy(showLogoutDialog = false) }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            authRepository.clearLoginModel()
            _uiState.update { it.copy(isLoggedOut = true) }
        }
    }
}
