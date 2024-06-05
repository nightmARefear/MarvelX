package com.alexgreece45.marvelx.ui.screens.otp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.ContainedButton
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.effect.OneTimeLaunchedEffect
import com.alexgreece45.marvelx.ui.components.error.ContentErrorScreen
import com.alexgreece45.marvelx.ui.components.otpTextField.OtpTextField
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.SPACING_TINY
import com.alexgreece45.marvelx.util.VSpacer
import kotlinx.coroutines.flow.Flow

/**
 * The OTP (One Time Password) screen is used to double check the user's identity when signing up or
 * logging in.
 *
 * Navigation **to** and **from** this screen ***must*** be handled with caution.
 * Navigation to this screen MUST only happen when instructed either by `LoginScreen`,
 * or `SignupScreen`.
 */
@Composable
fun OtpScreen(
    state: OtpState,
    onEventSent: (event: OtpEvent) -> Unit,
    effect: Flow<OtpEffect>,
    popBackStack: () -> Unit,
    navigateToAllCharactersScreen: () -> Unit,
    navigateToChangePasswordScreen: (String, String?) -> Unit,
    userId: Int?,
    otpFlowType: String?,
    verificationStep: String?,
    shouldRememberUserSession: Boolean?,
    requestIdEmail: String?,
    requestIdPhoneNumber: String?
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    OneTimeLaunchedEffect {
        onEventSent(
            OtpEvent.Init(
                userId = userId,
                otpFlowType = otpFlowType,
                verificationStep = verificationStep,
                shouldRememberUserSession = shouldRememberUserSession,
                requestIdEmail = requestIdEmail,
                requestIdPhoneNumber = requestIdPhoneNumber
            )
        )
    }

    LaunchedEffect(Unit) {
        effect.collect { effect ->
            when (effect) {
                OtpEffect.Navigation.PopBackStack -> {
                    popBackStack()
                }

                OtpEffect.OnUserSensitiveDataReceived -> {
                    // Requests focus for OTP field.
                    focusRequester.requestFocus()
                }

                OtpEffect.OtpFieldRequestFocus -> {
                    focusRequester.requestFocus()
                }

                OtpEffect.Navigation.NavigateToAllCharactersScreen -> {
                    navigateToAllCharactersScreen()
                }

                is OtpEffect.Navigation.NavigateToChangePasswordScreen -> {
                    navigateToChangePasswordScreen(effect.requestIdEmail, requestIdPhoneNumber)
                }

                OtpEffect.CloseKeyboardAndRemoveFocus -> {
                    keyboardController?.hide()
                    focusRequester.freeFocus()
                }
            }
        }
    }

    ContentScreen(
        isLoading = state.isLoading,
        navigableAction = ScreenNavigableAction.Backable {
            onEventSent(OtpEvent.OnBackClick)
        },
        showToolbar = true
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Title and where the OTP has been sent to.
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Title.
                Text(
                    text = stringResource(R.string.otp_screen_title),
                    style = MaterialTheme.typography.h4
                )

                VSpacer.Large()

                state.sensitiveUserData?.let { sensitiveUserData ->
                    // Where the OTP has been sent to.
                    Text(
                        text = stringResource(
                            R.string.otp_screen_input_field_label,
                            stringResource(sensitiveUserData.otpPreferredCommunicationType.label),
                            sensitiveUserData.maskedData
                        ),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.h6.copy(fontSize = 18.sp)
                    )
                }
            }

            VSpacer.Large()

            // OTP field and error message.
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
            ) {
                OtpTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    enabled = state.otpFieldEnabled,
                    otp = state.otp,
                    onValueChange = { value, otpInputFilled ->
                        onEventSent(
                            OtpEvent.OnOtpFieldValueChange(
                                otp = value
                            )
                        )
                    },
                    onDoneClick = { onEventSent(OtpEvent.OnDoneClick) }
                )

                Column(
                    modifier = Modifier.padding(start = 35.dp, end = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(SPACING_TINY.dp)
                ) {
                    // Error message
                    Row {
                        state.errorMessage?.let { errorMessage ->
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = SPACING_TINY.dp),
                                text = errorMessage,
                                color = MaterialTheme.colors.error,
                                textAlign = TextAlign.Start,
                                style = MaterialTheme.typography.subtitle1,
                            )
                        }
                    }

                    VSpacer.Medium()

                    // "didn't receive an OTP..." string
                    Text(
                        text = stringResource(R.string.otp_screen_not_receive_otp_label)
                    )

                    // Resend OTP button.
                    Button(
                        onClick = { onEventSent(OtpEvent.OnResendOtpButtonClick) },
                        enabled = state.isResendOtpButtonEnabled
                    ) {
                        Text(
                            text = state.otpBlockRemainingTime?.let { remainingTime ->
                                stringResource(
                                    R.string.otp_screen_resend_otp_button_with_delay,
                                    remainingTime
                                )
                            } ?: stringResource(R.string.otp_screen_resend_otp_button)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // "Verify" button.
            ContainedButton(
                onClick = { onEventSent(OtpEvent.OnVerifyButtonClicked) },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.isVerifyButtonEnabled
            ) {
                Text(
                    text = stringResource(R.string.otp_screen_verify_button)
                )
            }
        }
    }

    state.error?.let { error ->
        ContentErrorScreen(
            errorTitle = error.errorTitle,
            errorSubtitle = error.errorSubtitle,
            onClose = error.onCloseClicked,
            errorButtonLayout = error.errorButtonLayout
        )
    }
}