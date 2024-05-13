package com.alexgreece45.marvelx.ui.screens.registerScreens.loginScreen.forgotPasswordScreen.forgotPasswordValidateEmailScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alexgreece45.marvelx.ui.components.ContainedButton
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.ContentScreenHeader
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.error.ContentErrorScreen
import com.alexgreece45.marvelx.viewModels.ForgotPasswordValidateEmailEffect
import com.alexgreece45.marvelx.viewModels.ForgotPasswordValidateEmailEvent
import com.alexgreece45.marvelx.viewModels.ForgotPasswordValidateEmailViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * This sub-screen is used to validate the OTP that the user has received to their email.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ForgotPasswordValidateEmailScreen(
    popBackStack: () -> Unit,
    navigateToForgotPasswordEnterPhoneNumberScreen: (requestIdEmail: String) -> Unit,
    viewModel: ForgotPasswordValidateEmailViewModel = koinViewModel(),
    requestIdEmail: String?
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val keyboard = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        viewModel.setEvent(ForgotPasswordValidateEmailEvent.InitView(requestIdEmail))

        viewModel.uiEffect.collect { effect ->
            when (effect) {
                ForgotPasswordValidateEmailEffect.Navigation.PopBackStack -> {
                    popBackStack()
                }

                is ForgotPasswordValidateEmailEffect.Navigation.NavigateToForgotPasswordEnterPhoneNumberScreen -> {
                    navigateToForgotPasswordEnterPhoneNumberScreen(effect.requestIdEmail)
                }

                is ForgotPasswordValidateEmailEffect.Navigation.NavigateToPreviousScreenWithError -> {

                }
            }
        }
    }

    ContentScreen(
        isLoading = state.isLoading,
        navigableAction = ScreenNavigableAction.Backable {
            viewModel.setEvent(
                ForgotPasswordValidateEmailEvent.OnBackClicked
            )
        },
        showToolbar = true,
        stickyBottom = {
            ContainedButton(
                onClick = { viewModel.setEvent(ForgotPasswordValidateEmailEvent.OnVerifyOtpButtonClicked) },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.isVerifyOtpButtonEnabled
            ) {
                Text(
                    text = stringResource(state.stickyButtonResId)
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
                title = stringResource(state.titleResId),
                subtitle = stringResource(state.subtitleResId)
            )
        }
    }

    state.error?.let { error ->
        ContentErrorScreen(
            errorTitle = error.errorTitle,
            errorSubtitle = error.errorSubtitle,
            onClose = error.onClose,
            errorButtonLayout = error.errorButtonLayout
        )
    }
}