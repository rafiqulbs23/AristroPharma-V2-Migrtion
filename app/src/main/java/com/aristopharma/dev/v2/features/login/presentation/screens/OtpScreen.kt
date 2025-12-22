package com.aristopharma.dev.v2.features.login.presentation.screens

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import androidx.navigation.NavHostController
import com.aristopharma.dev.v2.features.login.domain.model.OtpEvent
import com.aristopharma.dev.v2.features.login.domain.model.OtpUiState
import com.aristopharma.dev.v2.features.login.presentation.viewModel.OtpViewModel
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay


@Composable
fun OtpScreen(
    username: String,
    password: String,
    viewModel: OtpViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
    navController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var hasNavigatedToHome by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val appHash = "YOUR_APP_HASH" // TODO: Implement AppSignatureHelperNew to get actual hash

    DisposableEffect(Unit) {
        val filter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                if (intent?.action != SmsRetriever.SMS_RETRIEVED_ACTION) return
                val extras = intent.extras ?: return
                val status = extras.get(SmsRetriever.EXTRA_STATUS) as? Status ?: return

                when (status.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        val message = extras.getString(SmsRetriever.EXTRA_SMS_MESSAGE)
                        if (message.isNullOrBlank()) return

                        if (!message.contains(appHash)) return

                        // Extract the OTP (4–8 digits for example)
                        val otp = Regex("""\b(\d{4,8})\b""").find(message)?.value ?: return

                        if (!uiState.isOtpButtonEnable) {
                            viewModel.setOtp(otp)
                        }

                        SmsRetriever.getClient(context).startSmsRetriever()
                    }
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
        } else {
            @Suppress("DEPRECATION")
            ContextCompat.registerReceiver(
                context,
                receiver,
                filter,
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
        }

        // 2) Start the 5-min listening window
        SmsRetriever.getClient(context).startSmsRetriever()

        onDispose {
            runCatching { context.unregisterReceiver(receiver) }
        }
    }



    LaunchedEffect(uiState.navigateToHome) {
        if (uiState.navigateToHome && !hasNavigatedToHome) {
            hasNavigatedToHome = true
            onLoginSuccess()
        }
    }

    // Show error message if any
    uiState.error?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            // You can show a snackbar or toast here
            // For now, the error will be displayed in the UI if needed
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OtpContent(
            username = username,
            isLoading = uiState.isLoading,
            otpText = uiState.otp,
            onOtpTextChange = { viewModel.setOtp(it) },
            onVerifyClick = { otp -> viewModel.verifyOtp(username, otp, password) },
            uiState = uiState,
            onEvent = viewModel::onEvent
        )
    }
}

@Composable
private fun OtpContent(
    username: String,
    isLoading: Boolean,
    otpText: String, // Add OTP state
    onOtpTextChange: (String) -> Unit, // Add callback to update OTP
    onVerifyClick: (String) -> Unit,
    uiState: OtpUiState,
    onEvent: (OtpEvent) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        if (uiState.isAutoFilledEnable){
            OutlinedCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Enable Manual Otp Input")
                    Switch(
                        checked = uiState.isOtpButtonEnable,
                        onCheckedChange = {
                            onEvent(OtpEvent.OnStatusChange(it))
                        },
                        thumbContent = if (uiState.isOtpButtonEnable) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize),
                                )
                            }
                        } else {
                            null
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Enter Verification Code",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "We've sent a 6-digit verification code to your registered phone number",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        OtpPinInputField(
            isLoading = isLoading,
            otpText = otpText, // Pass OTP text
            onOtpTextChange = onOtpTextChange, // Pass callback
            onVerifyClick = onVerifyClick,
            isOtpButtonEnable = uiState.isOtpButtonEnable
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun OtpPinInputField(
    isLoading: Boolean,
    otpText: String, // Controlled OTP text
    onOtpTextChange: (String) -> Unit, // Callback to update OTP
    onVerifyClick: (String) -> Unit,
    isOtpButtonEnable: Boolean
) {
    val maxLength = 6
    var otpTextFieldValue by remember { mutableStateOf(TextFieldValue(otpText)) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    // Update text field when otpText changes (e.g., from SMS Retriever)
    LaunchedEffect(otpText) {
        otpTextFieldValue = TextFieldValue(
            text = otpText,
            selection = TextRange(otpText.length)
        )
        if (otpText.length == maxLength) {
            keyboardController?.hide()
            focusManager.clearFocus()
            onVerifyClick(otpText) // Auto-submit when OTP is filled
        }
    }

    // Timer state
    var timeRemaining by remember { mutableStateOf(120) }
    var isTimerRunning by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = isTimerRunning) {
        if (isTimerRunning) {
            while (timeRemaining > 0) {
                delay(1.seconds)
                timeRemaining--
            }
            isTimerRunning = false
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        BasicTextField(
            value = otpTextFieldValue,
            onValueChange = { newValue ->
                if (newValue.text.length <= maxLength && newValue.text.all { it.isDigit() }) {
                    otpTextFieldValue = newValue.copy(
                        selection = TextRange(newValue.text.length)
                    )
                    onOtpTextChange(newValue.text) // Update ViewModel
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            textStyle = TextStyle(fontSize = 0.sp),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            enabled = !isLoading && isOtpButtonEnable,
            decorationBox = { innerTextField ->
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(maxLength) { index ->
                        val char = when {
                            index < otpTextFieldValue.text.length -> otpTextFieldValue.text[index].toString()
                            else -> ""
                        }
                        val isFilled = char.isNotEmpty()
                        val isSelected = otpTextFieldValue.selection.start == index
                        val borderColor = when {
                            isSelected -> MaterialTheme.colorScheme.primary
                            isFilled -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .size(40.dp)
                                .background(
                                    color = if (isFilled) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                                    else MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = borderColor,
                                    shape = RoundedCornerShape(12.dp)
                                )
                        ) {
                            Text(
                                text = char,
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                innerTextField()
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Code expires in: ${formatTime(timeRemaining)}",
            style = MaterialTheme.typography.bodyMedium,
            color = if (timeRemaining > 30) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                MaterialTheme.colorScheme.error
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onVerifyClick(otpTextFieldValue.text) },
            enabled = !isLoading && otpTextFieldValue.text.length == maxLength,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 32.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    "Verify",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Didn't receive the code?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.width(4.dp))

            TextButton(
                onClick = {
                    timeRemaining = 120
                    isTimerRunning = true
                    onOtpTextChange("") // Clear OTP
                    focusRequester.requestFocus()
                    // Call viewModel.resendOtp() when implemented
                },
                enabled = !isTimerRunning && !isLoading
            ) {
                Text(
                    text = "Resend",
                    color = if (!isTimerRunning && !isLoading) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outline
                    }
                )
            }
        }
    }

}

@Composable
fun SystemBroadcastReceiver(
    systemAction: String,
    onSystemEvent: (intent: Intent?) -> Unit
) {
    val context = LocalContext.current

    // If either context or systemAction changes, unregister and register again
    DisposableEffect(context, systemAction) {
        val intentFilter = IntentFilter(systemAction)
        val broadcast = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                onSystemEvent(intent)
            }
        }

        /*
          As discussed at Google I/O 2023, registering receivers
          with intention using the RECEIVER_EXPORTED / RECEIVER_NOT_EXPORTED
          flag was introduced as part of Android 13 and is now a requirement
          for apps running on Android 14 or higher (U+).

          https://stackoverflow.com/a/77276774/13447094
        */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            context.registerReceiver(broadcast, intentFilter, Context.RECEIVER_EXPORTED)
        } else {
            context.registerReceiver(broadcast, intentFilter)
        }

        // When the effect leaves the Composition, remove the callback
        onDispose {
            context.unregisterReceiver(broadcast)
        }
    }
}

private fun formatTime(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "${minutes.toString().padStart(1, '0')}:${seconds.toString().padStart(2, '0')}"
}

@Preview(showBackground = true)
@Composable
fun OtpScreenPreview() {
    OtpContent(
        username = "user@example.com",
        isLoading = false,
        otpText = "123456",
        onOtpTextChange = {},
        onVerifyClick = {},
        uiState = OtpUiState(otp = "123456", isAutoFilledEnable = true, isOtpButtonEnable = true),
        onEvent = {}
    )
}