package com.alexgreece45.marvelx.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.AlertDialogButton
import com.alexgreece45.marvelx.ui.components.AlertDialogError
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.WrapButton
import com.alexgreece45.marvelx.ui.components.effect.ObserveUiEffect
import com.alexgreece45.marvelx.ui.components.effect.OneTimeLaunchedEffect
import com.alexgreece45.marvelx.util.SPACING_SMALL
import kotlinx.coroutines.flow.Flow

@Composable
fun HomeScreen(
    onEventSent: (event: HomeEvent) -> Unit,
    state: HomeState,
    effect: Flow<HomeEffect>,
    navigateToLoginScreen: () -> Unit,
    navigateToSignupScreen: () -> Unit,
    navigateToAllCharactersScreen: () -> Unit,
    navigateToOtpScreen: (Int, String, String, Boolean) -> Unit,
    popBackStack: () -> Boolean
) {
    OneTimeLaunchedEffect {
        onEventSent(HomeEvent.Init)
    }

    ObserveUiEffect(effect) { homeEffect ->
        when (homeEffect) {
            HomeEffect.Navigation.NavigateToLoginScreen -> {
                navigateToLoginScreen()
            }

            HomeEffect.Navigation.NavigateToSignupScreen -> {
                navigateToSignupScreen()
            }

            HomeEffect.Navigation.NavigateToAllCharactersScreen -> {
                navigateToAllCharactersScreen()
            }

            is HomeEffect.Navigation.NavigateToOtpScreen -> {
                navigateToOtpScreen(
                    homeEffect.userId,
                    homeEffect.otpFlowType,
                    homeEffect.verificationStep,
                    homeEffect.shouldRememberUserSession
                )
            }

            HomeEffect.Navigation.PopBackStack -> {
                popBackStack()
            }
        }
    }

    ContentScreen(
        isLoading = state.isLoading,
        navigableAction = ScreenNavigableAction.Backable {
            onEventSent(HomeEvent.OnBack)
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = paddingValues,
        ) {
            item {
                state.alertDialogError?.let { homeAlertDialogErrorView ->
                    AlertDialogError(
                        isVisible = true,
                        title = homeAlertDialogErrorView.title,
                        errorMessage = homeAlertDialogErrorView.errorMessage,
                        confirmButton = AlertDialogButton(
                            value = homeAlertDialogErrorView.buttonLabel,
                            onClick = homeAlertDialogErrorView.alertDialogOnClick
                        )
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.home_screen_toolbar_title),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.h4
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
                ) {
                    // Login button.
                    WrapButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onEventSent(HomeEvent.OnLoginButtonClicked) }
                    ) {
                        Text(
                            text = stringResource(R.string.home_screen_label_log_in_button),
                        )
                    }

                    // Signup button.
                    WrapButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onEventSent(HomeEvent.OnSignupButtonClicked) }
                    ) {
                        Text(
                            text = stringResource(R.string.home_screen_label_sign_up_button),
                        )
                    }
                }
            }
        }
    }
}
