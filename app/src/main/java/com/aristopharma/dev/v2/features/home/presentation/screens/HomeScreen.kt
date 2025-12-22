package com.aristopharma.dev.v2.features.home.presentation.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.aristopharma.core.base.BaseScreen
import com.aristopharma.dev.v2.features.home.domain.model.HomeUiEvent
import com.aristopharma.dev.v2.features.home.presentation.viewModel.HomeScreenViewModel
import com.aristopharma.dev.v2.navigation.SignInScreenNav

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) {
            navController.navigate(SignInScreenNav) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    if (uiState.showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(HomeUiEvent.OnLogoutCancelled) },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onEvent(HomeUiEvent.OnLogoutConfirmed)
                }) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onEvent(HomeUiEvent.OnLogoutCancelled) }) {
                    Text("Cancel")
                }
            }
        )
    }

    BaseScreen(
        navController = navController,
        title = "Home",
        viewModel = viewModel,
        showBackButton = false,
        isLoading = false,
        actions = {
            IconButton(onClick = { viewModel.onEvent(HomeUiEvent.Logout) }) {
                Icon(imageVector = Icons.Default.Logout, contentDescription = "Logout")
            }
        }
    ) {
        Text("Welcome to the Home Screen!")
    }

}
