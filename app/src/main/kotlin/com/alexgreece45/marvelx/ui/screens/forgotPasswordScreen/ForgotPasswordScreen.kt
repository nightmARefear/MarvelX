package com.alexgreece45.marvelx.ui.screens.forgotPasswordScreen

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.util.ObserveUiEffect
import com.alexgreece45.marvelx.domain.model_api.otp.OtpFlowType
import com.alexgreece45.marvelx.domain.model_api.user.VerificationStep
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.ContentScreenHeader
import com.alexgreece45.marvelx.ui.components.OutlinedTextFieldValidation
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.WrapButton
import com.alexgreece45.marvelx.ui.navigation.Screen
import com.alexgreece45.marvelx.util.VSpacer
import org.koin.androidx.compose.koinViewModel

@Composable
fun ForgotPasswordScreen(
    popUpToLoginScreen: () -> Unit,
    navController: NavController,
    viewModel: ForgotPasswordViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    ObserveUiEffect(flow = viewModel.uiEffect, tag = "ForgotPasswordScreen") { effect ->
        when (effect) {
            ForgotPasswordEffect.Navigation.PopToLoginScreen -> popUpToLoginScreen()

            is ForgotPasswordEffect.Navigation.NavigateToLoginScreenWithError -> {
                Toast.makeText(context, effect.errorMessage, Toast.LENGTH_SHORT).show()
                popUpToLoginScreen()
            }

            is ForgotPasswordEffect.Navigation.NavigateToOtpScreen -> {
                navController.navigate(
                    Screen.OtpScreen.createNavRoute(
                        otpFlowType = OtpFlowType.ForgotPassword.Email.key,
                        verificationStep = VerificationStep.OtpEmail.key,
                        shouldRememberUserSession = false,
                        requestIdEmail = effect.requestIdEmail
                    )
                )
            }
        }
    }

    ContentScreen(
        isLoading = state.isLoading,
        navigableAction = ScreenNavigableAction.Backable {
            viewModel.setEvent(ForgotPasswordEvent.OnBackClick)
        },
        showToolbar = true,
        stickyBottom = {
            WrapButton(
                onClick = { viewModel.setEvent(ForgotPasswordEvent.OnContinueButtonClicked) },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.isContinueButtonEnabled
            ) {
                Text(text = stringResource(R.string.forgot_password_enter_email_screen_sticky_button))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ContentScreenHeader(
                title = stringResource(R.string.forgot_password_enter_email_screen_title),
                subtitle = stringResource(R.string.forgot_password_enter_email_screen_subtitle)
            )

            ForgotPasswordScreenContent(
                onEventSent = viewModel::setEvent,
                state = state
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun ForgotPasswordScreenContent(
    onEventSent: (ForgotPasswordEvent) -> Unit,
    state: ForgotPasswordState
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    VSpacer.Medium()

    OutlinedTextFieldValidation(
        value = state.email,
        onValueChange = { newEmail ->
            onEventSent(ForgotPasswordEvent.OnEmailValueChange(email = newEmail))
        },
        modifier = Modifier
            .semantics { contentType = ContentType.EmailAddress }
            .fillMaxWidth(),
        label = stringResource(R.string.forgot_password_screen_input_field_label_email),
        errorText = state.emailValidationResult.message,
        isError = state.emailValidationResult.isValid.not() && state.emailValidationResult.message.isNotBlank(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
                onEventSent(ForgotPasswordEvent.OnEmailDoneClick)
            }
        )
    )
}
