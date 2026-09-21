package com.alexgreece45.marvelx.ui.screens.introLoading

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.alexgreece45.marvelx.common_ui.util.ObserveUiEffect
import com.alexgreece45.marvelx.core.common.util.flows.single_observer_flow.SingleObserverFlow
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.effect.OneTimeLaunchedEffect
import com.alexgreece45.marvelx.ui.navigation.Screen
import com.alexgreece45.marvelx.ui.screens.dashboard.navigation.navigateToDashboardScreen
import com.alexgreece45.marvelx.util.extensions.popUpToTop

@Composable
fun IntroLoadingScreen(
    navController: NavController,
    onEventSent: (IntroLoadingContract.Event) -> Unit,
    state: IntroLoadingContract.State,
    effect: SingleObserverFlow<IntroLoadingContract.Effect>,
) {
    OneTimeLaunchedEffect {
        onEventSent(IntroLoadingContract.Event.Init)
    }

    ObserveUiEffect(flow = effect, tag = "IntroLoadingScreen") { uiEffect ->
        when (uiEffect) {
            is IntroLoadingContract.Effect.Navigation.NavigateToHomeScreenWithErrorMessage -> {
                // TODO: Resolve.
//                NavArgHelper.setArgument(
//                    NavArg.HomeAlertDialogErrorType,
//                    HomeAlertDialogErrorType.HasError(uiEffect.errorMessage)
//                )
                navController.navigate(Screen.HomeScreen.createNavRoute()) {
                    popUpToTop(navController)
                }
            }

            IntroLoadingContract.Effect.Navigation.NavigateToDashboardScreen -> {
                navController.navigateToDashboardScreen()
            }

            is IntroLoadingContract.Effect.Navigation.NavigateToOtpScreen -> {
                // TODO: Problems will be caused with the backstack. To be fixed in a future release.
                navController.navigate(
                    Screen.OtpScreen.createNavRoute(
                        userId = uiEffect.userId,
                        otpFlowType = uiEffect.otpFlowType,
                        verificationStep = uiEffect.verificationStep,
                        shouldRememberUserSession = uiEffect.shouldRememberUserSession
                    )
                ) {
                    popUpToTop(navController)
                }
            }
        }
    }

    ContentScreen(isLoading = false) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(state.loadingStage.label)
            )
        }
    }
}
