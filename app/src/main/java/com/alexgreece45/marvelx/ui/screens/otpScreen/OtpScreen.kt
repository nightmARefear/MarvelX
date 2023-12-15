package com.alexgreece45.marvelx.ui.screens.otpScreen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.navigation.Screen
import com.alexgreece45.marvelx.util.HSpacer
import com.alexgreece45.marvelx.util.OTP_LENGTH
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.SPACING_TINY
import com.alexgreece45.marvelx.util.VSpacer
import com.alexgreece45.marvelx.viewModels.OtpEffect
import com.alexgreece45.marvelx.viewModels.OtpEvent
import com.alexgreece45.marvelx.viewModels.OtpViewModel

/**
 * The OTP (One Time Password) screen is used to double check the user's identity when signing up
 * or logging in.
 *
 * Navigation **to** and **from** this screen ***must*** be handled with caution.
 * Navigation to this screen MUST only happen when instructed either by `LoginScreen`,
 * or `SignupScreen`.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun OtpScreen(
    navController: NavController,
    viewModel: OtpViewModel = hiltViewModel(),
    userId: Int?,
    verificationStep: String?,
    shouldRememberUserSession: Boolean?
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val keyboard = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        viewModel.setEvent(
            OtpEvent.InitView(
                userId = userId,
                verificationStep = verificationStep,
                shouldRememberUserSession = shouldRememberUserSession
            )
        )

        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is OtpEffect.Navigation.NavigateToPreviousScreenWithError -> {
                    Toast.makeText(context, effect.errorMessage, Toast.LENGTH_LONG).show()
                    navController.popBackStack()
                }

                OtpEffect.Navigation.NavigateToAllCharactersScreen -> {
                    keyboard?.hide()
                    navController.navigate(Screen.BottomNavigationScreens.AllCharactersScreen.route) {
                        popUpTo(Screen.RegisterScreens.getNestedGraphRootRoute()) {
                            inclusive = true
                        }
                    }
                }

                OtpEffect.OnUserSensitiveDataReceived -> {
                    // Requests focus for OTP field.
                    focusRequester.requestFocus()
                }

                OtpEffect.Navigation.PopBackStack -> {
                    navController.popBackStack()
                }

                OtpEffect.OtpFieldRequestFocus -> {
                    focusRequester.requestFocus()
                }
            }
        }
    }

    ContentScreen(
        onBack = { viewModel.setEvent(OtpEvent.OnBackClick) },
        isLoading = state.isLoading,
        showToolbar = true,
        toolbarBackgroundColor = Color.Transparent
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = paddingValues
        ) {
            // Title and where OTP has been sent to.
            item {
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
            }

            // Vertical spacing.
            item { VSpacer.Large() }

            // OTP field and error message.
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
                ) {
                    // OTP field
                    OtpTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        enabled = state.otpFieldEnabled,
                        otp = state.otp,
                        onValueChange = { value, otpInputFilled ->
                            viewModel.setEvent(
                                OtpEvent.OnOtpFieldValueChange(
                                    otp = value
                                )
                            )
                        },
                        onDoneClick = { viewModel.setEvent(OtpEvent.OnDoneClick) }
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
                            onClick = { viewModel.setEvent(OtpEvent.OnResendOtpButtonClick) },
                            enabled = state.resendOtpButtonEnabled
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
            }
        }
    }
}

@Composable
private fun OtpTextField(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    otp: String,
    onValueChange: (String, Boolean) -> Unit,
    onDoneClick: () -> Unit
) {
    BasicTextField(
        modifier = modifier,
        enabled = enabled,
        value = TextFieldValue(
            otp,
            selection = TextRange(otp.length)
        ),
        onValueChange = {
            if (it.text.length <= OTP_LENGTH) {
                onValueChange.invoke(it.text, it.text.length == OTP_LENGTH)
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onDoneClick.invoke()
            }
        ),
        decorationBox = {
            Row(horizontalArrangement = Arrangement.Center) {
                repeat(OTP_LENGTH) { index ->
                    CharView(
                        index = index,
                        text = otp
                    )
                    HSpacer.Small()
                }
            }
        }
    )
}

@Composable
private fun CharView(
    index: Int,
    text: String
) {
    val isFocused = text.length == index
    val char = when {
        index == text.length -> ""
        index > text.length -> ""
        else -> text[index].toString()
    }

    val lineColor = when (isFocused) {
        true -> MaterialTheme.colors.primaryVariant
        false -> MaterialTheme.colors.onBackground
    }

    Box(
        modifier = Modifier
            .size(45.dp)
            .background(MaterialTheme.colors.secondaryVariant)
            /*  .border(
                  width = 2.dp,
                  color = when {
                      isFocused -> MaterialTheme.colors.onPrimary
                      else -> MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium)
                  },
                  shape = MaterialTheme.shapes.medium
              )*/
            .drawWithContent {
                // Draws this field.
                drawContent()

                // Draws a horizontal line at the bottom of this field.
                drawLine(
                    color = lineColor,
                    start = Offset(
                        x = 0f,
                        y = size.height
                    ),
                    end = Offset(
                        x = size.width,
                        y = size.height
                    ),
                    strokeWidth = 1.5.dp.toPx(),
                    cap = StrokeCap.Round
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = char,
            style = MaterialTheme.typography.h4,
        )
    }
}

@Preview
@Composable
private fun OtpTextFieldPreview() {
    var otp by remember { mutableStateOf("") }
    OtpTextField(
        modifier = Modifier
            .fillMaxWidth(),
        enabled = true,
        otp = otp,
        onValueChange = { value, _ ->
            otp = value
        },
        onDoneClick = {
        }
    )
}