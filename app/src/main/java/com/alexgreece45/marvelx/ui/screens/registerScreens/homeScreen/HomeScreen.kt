package com.alexgreece45.marvelx.ui.screens.registerScreens.homeScreen

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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.AlertDialogButton
import com.alexgreece45.marvelx.ui.components.AlertDialogError
import com.alexgreece45.marvelx.ui.components.ContainedButton
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.navigation.Screen
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.viewModels.HomeEffect
import com.alexgreece45.marvelx.viewModels.HomeEvent
import com.alexgreece45.marvelx.viewModels.HomeViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ContentScreen(
        onBack = { viewModel.setEvent(HomeEvent.OnBack) },
        isLoading = state.isLoading,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = paddingValues,
        ) {
            if (state.isAlertDialogVisible) {
                item {
                    AlertDialogError(
                        title = state.alertDialogTitle,
                        errorMessage = state.alertDialogErrorMessage,
                        confirmButton = AlertDialogButton(
                            value = stringResource(R.string.ok),
                            onClick = { viewModel.setEvent(HomeEvent.AlertDialog.OnOkClick) }
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
                        onClick = { viewModel.setEvent(HomeEvent.OnLogInButtonClick) }
                    ) {
                        Text(
                            text = stringResource(R.string.home_screen_label_log_in_button),
                        )
                    }

                    // Signup button.
                    ContainedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { viewModel.setEvent(HomeEvent.OnSignUpButtonClick) }
                    ) {
                        Text(
                            text = stringResource(R.string.home_screen_label_sign_up_button),
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.setEvent(HomeEvent.InitView)

        viewModel.uiEffect.collect { homeEffect ->
            when (homeEffect) {
                HomeEffect.Navigation.NavigateToLoginScreen -> {
                    navController.navigate(Screen.RegisterScreens.LoginScreen.route) {
                        launchSingleTop = true
                    }
                }

                HomeEffect.Navigation.NavigateToSignupScreen -> {
                    navController.navigate(Screen.RegisterScreens.SignupScreen.route) {
                        launchSingleTop = true
                    }
                }

                is HomeEffect.Navigation.NavigateToAllCharactersScreen -> {
                    navController.navigate(Screen.BottomNavigationScreens.AllCharactersScreen.route) {
                        popUpTo(Screen.RegisterScreens.getNestedGraphRootRoute()) {
                            inclusive = true
                        }
                    }
                }

                is HomeEffect.Navigation.NavigateToOtpScreen -> {
                    navController.navigate(
                        Screen.OtpScreen.createNavRoute(
                            userId = homeEffect.userId,
                            verificationStep = homeEffect.verificationStep,
                            shouldRememberUserSession = homeEffect.shouldRememberUserSession
                        )
                    )
                }

                HomeEffect.Navigation.PopBackStack -> {
                    navController.popBackStack()
                }

                HomeEffect.LoginFlow.ProceedWithLogin -> {
                    viewModel.setEvent(HomeEvent.OnProceedWithLogin)
                }
            }
        }
    }
}