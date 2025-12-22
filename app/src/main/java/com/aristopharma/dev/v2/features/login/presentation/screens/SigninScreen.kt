package com.aristopharma.dev.v2.features.login.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.aristopharma.core.base.BaseScreen
import com.aristopharma.dev.v2.features.login.domain.model.SignInEvent
import com.aristopharma.dev.v2.features.login.domain.model.SignInState
import com.aristopharma.dev.v2.features.login.presentation.viewModel.SignInViewModel
import com.aristopharma.dev.v2.R
import kotlinx.coroutines.delay

@Composable
fun SignInScreen(
    navController: NavHostController,
    onNavigateToDashboard: () -> Unit,
    signInViewModel: SignInViewModel = hiltViewModel(),
    navigateToOtp : (username: String, password: String)->Unit
) {
    val signInState by signInViewModel.uiState.collectAsStateWithLifecycle()
    val event = signInViewModel::onEvent
    val context = LocalContext.current


    // Handle navigation to OTP screen after signup
    LaunchedEffect(signInState.isWaitingForSMS) {
        signInState.isWaitingForSMS.getContentIfNotHandled()?.let { shouldNavigate ->
            if (shouldNavigate) {

                if (signInState.isBypassOTP){
                    onNavigateToDashboard()

                }else{
                    navigateToOtp(
                        signInState.empId.value,
                        signInState.password.value
                    )
                }
                // Navigate to OTP screen with username and password using type-safe navigation

            }
        }
    }

    BaseScreen(
        isLoading = signInState.isLoading,
        title = "",
        viewModel = signInViewModel,
        navController = navController

    ) {
        SignInScreenContent(
            state = signInState,
            onEvent = event,
            navController = navController,
            onNavigateToDashboard = onNavigateToDashboard,
        )
    }

}

@Composable
private fun SignInScreenContent(
    state: SignInState,
    onEvent: (SignInEvent) -> Unit,
    navController: NavHostController,
    onNavigateToDashboard: () -> Unit,
) {
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var showConfirmPassword by rememberSaveable { mutableStateOf(false) }

    // Animation states
    var logoVisible by remember { mutableStateOf(false) }
    var inputVisible by remember { mutableStateOf(false) }
    val logoAlpha by animateFloatAsState(
        targetValue = if (logoVisible) 1f else 0f,
        animationSpec = tween(800),
        label = "logo_alpha",
    )
    val inputAlpha by animateFloatAsState(
        targetValue = if (inputVisible) 1f else 0f,
        animationSpec = tween(600),
        label = "input_alpha",
    )

    LaunchedEffect(Unit) {
        logoVisible = true
        delay(300)
        inputVisible = true
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        val guidelineOffset = maxHeight * 0.35f

        // Bypass OTP Switch (invisible by default, visible in debug builds)
        AnimatedVisibility(
            visible = state.isVisibleByPassOTP,
            modifier = Modifier.align(Alignment.TopEnd),
        ) {
            Switch(
                checked = state.isBypassOTP,
                onCheckedChange = { enabled -> onEvent(SignInEvent.UpdateBypassOTP(enabled)) },
                modifier = Modifier.padding(16.dp),
            )
        }

        // Logo with animation
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = guidelineOffset * 0.6f)
                .alpha(logoAlpha),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.ic_aristo_logo),
                    contentDescription = "App logo",
                    modifier = Modifier.size(64.dp),
                )
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = "Aristopharma MSFA",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center,
                )
            }
        }

        // Login form
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = guidelineOffset)
                .alpha(inputAlpha),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Header
            Text(
                text = if (state.isVerifyBtnVisible) "Sign Up" else "Login",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Employee ID field
            OutlinedTextField(
                value = state.empId.value,
                onValueChange = { onEvent(SignInEvent.UpdateEmpId(it)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                },
                label = { Text("Employee ID") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = state.empId.errorMessage != null,
                supportingText = state.empId.errorMessage?.let { { Text(it) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = MaterialTheme.shapes.medium,
                colors = appStaticOutlinedTextFieldColors(),
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Password field
            OutlinedTextField(
                value = state.password.value,
                onValueChange = { onEvent(SignInEvent.UpdatePassword(it)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                },
                label = { Text("Password") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (showPassword) "Hide password" else "Show password",
                        )
                    }
                },
                isError = state.password.errorMessage != null,
                supportingText = state.password.errorMessage?.let { { Text(it) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = MaterialTheme.shapes.medium,
                colors = appStaticOutlinedTextFieldColors(),
            )

            // Confirm Password (visible only during sign up)
            AnimatedVisibility(visible = state.isVerifyBtnVisible) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        shape = MaterialTheme.shapes.medium,
                        colors = appStaticOutlinedTextFieldColors(),
                        value = state.confirmPassword.value,
                        onValueChange = { onEvent(SignInEvent.UpdateConfirmPassword(it)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                            )
                        },
                        label = { Text("Confirm Password") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                                Icon(
                                    imageVector = if (showConfirmPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (showConfirmPassword) "Hide password" else "Show password",
                                )
                            }
                        },
                        isError = state.confirmPassword.errorMessage != null,
                        supportingText = state.confirmPassword.errorMessage?.let { { Text(it) } },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                    )
                }
            }

            /* // OTP field (visible only during OTP verification)
             AnimatedVisibility(visible = state.isVerifyBtnVisible && !state.isLoginBtnVisible) {
                 Column {
                     Spacer(modifier = Modifier.height(12.dp))
                     OutlinedTextField(
                         value = state.otp.value,
                         onValueChange = { onEvent(SignInEvent.UpdateOTP(it)) },
                         label = { Text("OTP") },
                         singleLine = true,
                         keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                         modifier = Modifier
                             .fillMaxWidth()
                             .padding(horizontal = 16.dp),
                         colors = TextFieldDefaults.colors(),
                     )
                 }
             }*/

            Spacer(modifier = Modifier.height(32.dp))

            // Login button
            AnimatedVisibility(visible = state.isLoginBtnVisible) {
                Button(
                    onClick = {
                        if (state.isBypassOTP) {
                            onEvent(SignInEvent.LoginBypass(state.empId.value, state.password.value))
                        } else {
                            onEvent(SignInEvent.Login(
                                state.empId.value,
                                state.password.value,
                            ))
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(32.dp),
                ) {
                    Text(text = "Login", style = MaterialTheme.typography.labelLarge)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            // Sign Up button
            AnimatedVisibility(visible = state.isSignUpBtnVisible) {
                OutlinedButton(
                    onClick = {
                        /*onEvent(
                            SignInEvent.ValidateAndSignUp(
                                state.empId.value,
                                state.password.value,
                                state.confirmPassword.value,
                            ),
                        )*/
                        onEvent(SignInEvent.ShowSignUpUi)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(horizontal = 16.dp,),
                    shape = RoundedCornerShape(32.dp),
                ) {
                    Text(text = "Sign Up", style = MaterialTheme.typography.labelLarge)
                }
            }
            // Verify button
            AnimatedVisibility(visible = state.isVerifyBtnVisible) {
                OutlinedButton(
                    onClick = { onEvent(SignInEvent.ValidateAndSignUp(
                        state.empId.value,
                        state.password.value,
                        state.confirmPassword.value,
                    )) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(32.dp),
                ) {
                    Text(text = "Verify", style = MaterialTheme.typography.labelLarge)
                }
            }

            // Forgot password
            if (state.showForgotPassword) {
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "Forgot Password?",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 16.dp)
                        .clickable { onEvent(SignInEvent.ShowSignUpUi) },
                    textAlign = TextAlign.End,
                    textDecoration = TextDecoration.None,
                    fontSize = 12.sp,
                )
            }
        }

        // Version text at bottom
        Text(
            text = "Version 1.0.0",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Black.copy(alpha = 0.6f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(8.dp)
                .clickable { /* Show debug info */ },
        )
    }
}
@Composable
fun appStaticOutlinedTextFieldColors(): TextFieldColors {
    return OutlinedTextFieldDefaults.colors(
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,

        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,

        focusedBorderColor = MaterialTheme.colorScheme.surfaceContainer,
        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceContainer
    )
}