package com.alexgreece45.marvelx.ui.screens.registerScreens.loginScreen.forgotPasswordScreen

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.ContainedButton
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.ContentScreenHeader
import com.alexgreece45.marvelx.ui.components.OutlinedTextFieldValidation
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.util.VSpacer
import com.alexgreece45.marvelx.util.extensions.autofill
import com.alexgreece45.marvelx.viewModels.ForgotPasswordEffect
import com.alexgreece45.marvelx.viewModels.ForgotPasswordEvent
import com.alexgreece45.marvelx.viewModels.ForgotPasswordState
import com.alexgreece45.marvelx.viewModels.ForgotPasswordSubScreens
import com.alexgreece45.marvelx.viewModels.ForgotPasswordViewModel

@Composable
fun ForgotPasswordScreen(
    popUpToLoginScreen: () -> Unit,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                ForgotPasswordEffect.Navigation.PopBackStack -> {

                }

                ForgotPasswordEffect.Navigation.PopToLoginScreen -> {
                    popUpToLoginScreen()
                }

                is ForgotPasswordEffect.Navigation.NavigateToLoginScreenWithError -> {
                    Toast.makeText(context, effect.errorMessage, Toast.LENGTH_SHORT).show()
                    popUpToLoginScreen()
                }

                is ForgotPasswordEffect.Navigation.NavigateToValidateEmailSubScreen -> {
                    viewModel.setEvent(ForgotPasswordEvent.Navigation.NavigateToValidateEmailSubScreen)
                }

                else -> {
                    // TODO: Replace else with every case of [ForgotPasswordEffect].
                }
            }
        }
    }

    ContentScreen(
        isLoading = state.isLoading,
        navigableAction = ScreenNavigableAction.Backable {
            viewModel.setEvent(
                ForgotPasswordEvent.OnBackClick
            )
        },
        showToolbar = true,
        stickyBottom = {
            ContainedButton(
                onClick = { viewModel.setEvent(ForgotPasswordEvent.OnContinueButtonClicked) },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.isContinueButtonEnabled
            ) {
                Text(
                    text = stringResource(state.currentSubScreen.stickyButtonResId)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ContentScreenHeader(
                title = stringResource(state.currentSubScreen.titleResId),
                subtitle = stringResource(state.currentSubScreen.subtitleResId)
            )

            when (state.currentSubScreen) {
                ForgotPasswordSubScreens.EnterEmail -> {
                    ForgotPasswordScreenContent(
                        onEventSent = { forgotPasswordEvent ->
                            viewModel.setEvent(forgotPasswordEvent)
                        },
                        state = state
                    )
                }

                ForgotPasswordSubScreens.ValidateEmail -> {
//                    ForgotPasswordValidateEmailScreen()
                }

                ForgotPasswordSubScreens.EnterPhoneNumber -> {}
                ForgotPasswordSubScreens.ValidatePhoneNumber -> {}
                ForgotPasswordSubScreens.EnterNewPassword -> {}
                ForgotPasswordSubScreens.PasswordChanged -> {}
            }
        }
    }
}

/**
 * The initial content of [ForgotPasswordScreen].
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ForgotPasswordScreenContent(
    onEventSent: (ForgotPasswordEvent) -> Unit,
    state: ForgotPasswordState
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    VSpacer.Medium()

    // Email.
    OutlinedTextFieldValidation(
        modifier = Modifier
            .fillMaxWidth()
            .autofill(
                autofillTypes = listOf(AutofillType.EmailAddress),
                onFill = { suggestionValue ->
                    onEventSent(ForgotPasswordEvent.OnEmailValueChange(email = suggestionValue))
                }
            ),
        value = state.email,
        label = stringResource(R.string.forgot_password_screen_input_field_label_email),
        onValueChange = { newEmail ->
            onEventSent(ForgotPasswordEvent.OnEmailValueChange(email = newEmail))
        },
        errorMessage = state.emailValidationResult.message,
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