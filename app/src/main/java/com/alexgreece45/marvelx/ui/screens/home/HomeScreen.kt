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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.AlertDialogButton
import com.alexgreece45.marvelx.ui.components.AlertDialogError
import com.alexgreece45.marvelx.ui.components.ContainedButton
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.effect.OneTimeLaunchedEffect
import com.alexgreece45.marvelx.ui.navigation.Screen
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.extensions.popUpToTop
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    OneTimeLaunchedEffect {
        viewModel.setEvent(HomeEvent.Init)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { homeEffect ->
            when (homeEffect) {
                HomeEffect.Navigation.NavigateToLoginScreen -> {
                    navController.navigate(Screen.LoginScreen.route) {
                        launchSingleTop = true
                    }
                }

                HomeEffect.Navigation.NavigateToSignupScreen -> {
                    navController.navigate(Screen.SignupScreen.route) {
                        launchSingleTop = true
                    }
                }

                HomeEffect.Navigation.NavigateToAllCharactersScreen -> {
                    navController.navigate(Screen.AllCharactersScreen.route) {
                        popUpToTop(navController)
                    }
                }

                is HomeEffect.Navigation.NavigateToOtpScreen -> {
                    navController.navigate(
                        Screen.OtpScreen.createNavRoute(
                            userId = homeEffect.userId,
                            otpFlowType = homeEffect.otpFlowType,
                            verificationStep = homeEffect.verificationStep,
                            shouldRememberUserSession = homeEffect.shouldRememberUserSession
                        )
                    )
                }

                HomeEffect.Navigation.PopBackStack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    ContentScreen(
        isLoading = false,
        navigableAction = ScreenNavigableAction.Backable {
            viewModel.setEvent(HomeEvent.OnBack)
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
                    ContainedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { viewModel.setEvent(HomeEvent.OnLoginButtonClicked) }
                    ) {
                        Text(
                            text = stringResource(R.string.home_screen_label_log_in_button),
                        )
                    }

                    // Signup button.
                    ContainedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { viewModel.setEvent(HomeEvent.OnSignupButtonClicked) }
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