package com.alexgreece45.marvelx.ui.screens.registerScreens.loginScreen.forgotPasswordScreen

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.ContainedButton
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.OutlinedTextFieldValidation
import com.alexgreece45.marvelx.util.VSpacer
import com.alexgreece45.marvelx.util.extensions.autofill
import com.alexgreece45.marvelx.viewModels.ForgotPasswordEffect
import com.alexgreece45.marvelx.viewModels.ForgotPasswordEvent
import com.alexgreece45.marvelx.viewModels.ForgotPasswordState
import com.alexgreece45.marvelx.viewModels.ForgotPasswordSubScreens
import com.alexgreece45.marvelx.viewModels.ForgotPasswordViewModel
import kotlinx.coroutines.flow.Flow

@Composable
fun ForgotPasswordScreen(
    navigateToLoginScreen: () -> Unit,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                ForgotPasswordEffect.Navigation.PopBackStack -> {
                    navigateToLoginScreen()
                }

                is ForgotPasswordEffect.Navigation.NavigateToPreviousScreenWithError -> {
                    Toast.makeText(context, effect.errorMessage, Toast.LENGTH_SHORT).show()
                    navigateToLoginScreen()
                }

                else -> {
                    // TODO: Replace else with every case of [ForgotPasswordEffect].
                }
            }
        }
    }

    ContentScreen(
        onBack = {
            if (state.currentSubScreen == ForgotPasswordSubScreens.firstStep) {
                viewModel.setEvent(ForgotPasswordEvent.OnBackClick)
            }
        },
        isLoading = false,
        showToolbar = true,
        toolbarBackgroundColor = Color.Transparent,
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
            VSpacer.Large()

            HeaderSection(
                titleResId = state.currentSubScreen.titleResId,
                subtitleResId = state.currentSubScreen.subtitleResId
            )

            when (state.currentSubScreen) {
                ForgotPasswordSubScreens.EnterEmail -> {
                    ForgotPasswordScreenContent(
                        onEventSent = { forgotPasswordEvent ->
                            viewModel.setEvent(forgotPasswordEvent)
                        },
                        state = state,
                        effectFlow = viewModel.uiEffect
                    )
                }

                ForgotPasswordSubScreens.ValidateEmail -> {}
                ForgotPasswordSubScreens.EnterPhoneNumber -> {}
                ForgotPasswordSubScreens.ValidatePhoneNumber -> {}
                ForgotPasswordSubScreens.EnterNewPassword -> {}
                ForgotPasswordSubScreens.PasswordChanged -> {}
            }
        }
    }
}

/**
 * Defines the header section of a sub-screen.
 *
 * @param titleResId        The resource ID of the title of this sub-screen.
 * @param subtitleResId     The resource ID of the subtitle of this sub-screen.
 */
@Composable
private fun HeaderSection(
    titleResId: Int,
    subtitleResId: Int,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Title.
        Text(
            text = stringResource(titleResId),
            style = MaterialTheme.typography.h4
        )
        VSpacer.Large()

        // Subtitle.
        Text(
            text = stringResource(subtitleResId),
            style = MaterialTheme.typography.h6.copy(fontSize = 18.sp)
        )
    }
}

/**
 * The initial content of [ForgotPasswordScreen].
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ForgotPasswordScreenContent(
    onEventSent: (ForgotPasswordEvent) -> Unit,
    state: ForgotPasswordState,
    effectFlow: Flow<ForgotPasswordEffect>
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