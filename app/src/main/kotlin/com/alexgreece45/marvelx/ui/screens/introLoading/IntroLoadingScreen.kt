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
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.effect.ObserveUiEffect
import com.alexgreece45.marvelx.ui.components.effect.OneTimeLaunchedEffect
import com.alexgreece45.marvelx.ui.navigation.NavArgHelper
import com.alexgreece45.marvelx.ui.navigation.Screen
import com.alexgreece45.marvelx.ui.navigation.models.NavArg
import com.alexgreece45.marvelx.util.extensions.popUpToTop
import kotlinx.coroutines.flow.Flow

@Composable
fun IntroLoadingScreen(
    navController: NavController,
    onEventSent: (IntroLoadingContract.Event) -> Unit,
    state: IntroLoadingContract.State,
    effect: Flow<IntroLoadingContract.Effect>,
) {

    OneTimeLaunchedEffect {
        onEventSent(IntroLoadingContract.Event.Init)
    }

    ObserveUiEffect(effect) { uiEffect ->
        when (uiEffect) {
            is IntroLoadingContract.Effect.Navigation.NavigateToHomeScreenWithErrorMessage -> {
                // TODO: Resolve.
//                NavArgHelper.setArgument(
//                    NavArg.HomeAlertDialogErrorType,
//                    HomeAlertDialogErrorType.HasError(uiEffect.errorMessage)
//                )
                navController.navigate(Screen.HomeScreen.route) {
                    popUpToTop(navController)
                }
            }

            IntroLoadingContract.Effect.Navigation.NavigateToAllCharactersScreen -> {
                navController.navigate(Screen.AllCharactersScreen.route) {
                    popUpToTop(navController)
                }
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
