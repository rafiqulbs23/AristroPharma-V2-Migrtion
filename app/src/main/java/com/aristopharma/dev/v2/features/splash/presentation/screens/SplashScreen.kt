package com.aristopharma.dev.v2.features.splash.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.aristopharma.dev.v2.features.splash.domain.model.SplashUiState
import com.aristopharma.dev.v2.features.splash.presentation.viewModel.SplashViewModel
import com.aristopharma.dev.v2.navigation.DashboardScreenNav
import com.aristopharma.dev.v2.navigation.SignInScreenNav
import com.aristopharma.dev.v2.R

@Composable
fun SplashScreen(
    navController: NavHostController,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Observe navigation state from ViewModel
    LaunchedEffect(uiState) {
        when (uiState) {
            is SplashUiState.NavigateToHome -> {
                navController.navigate(DashboardScreenNav) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }
            is SplashUiState.NavigateToLogin -> {
                navController.navigate(SignInScreenNav) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }
            SplashUiState.Idle -> {}
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_aristo_logo), // Replace with your logo
            contentDescription = "App Logo",
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "AristroPharma MSFA",
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}