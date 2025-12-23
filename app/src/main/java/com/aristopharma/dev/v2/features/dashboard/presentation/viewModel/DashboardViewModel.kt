package com.aristopharma.dev.v2.features.dashboard.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aristopharma.core.base.BaseViewModel
import com.aristopharma.dev.v2.features.dashboard.domain.model.DashboardNotice
import com.aristopharma.dev.v2.features.dashboard.domain.model.DashboardSummary
import com.aristopharma.dev.v2.features.dashboard.domain.model.MenuPermission
import com.aristopharma.dev.v2.features.dashboard.domain.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val summary: DashboardSummary? = null,
    val permissions: List<MenuPermission> = emptyList(),
    val notices: List<DashboardNotice> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: DashboardRepository
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            combine(
                repository.getDashboardSummary(),
                repository.getMenuPermissions(),
                repository.getNotices()
            ) { summary, permissions, notices ->
                DashboardUiState(
                    summary = summary,
                    permissions = permissions,
                    notices = notices,
                    isLoading = false
                )
            }.collect { newState ->
                _uiState.value = newState
                
                // Auto-sync if not done yet
                if (newState.summary != null && !newState.summary.isFirstSyncDone && !newState.isLoading) {
                    onSyncClick(newState.summary.employeeId)
                }
            }
        }
    }

    fun onSyncClick(employeeId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.syncNow(employeeId)
            _uiState.update { it.copy(isLoading = false) }
            if (result.isFailure) {
                _uiState.update { it.copy(error = result.exceptionOrNull()?.message) }
            }
        }
    }

    fun onLogoutClick() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}
