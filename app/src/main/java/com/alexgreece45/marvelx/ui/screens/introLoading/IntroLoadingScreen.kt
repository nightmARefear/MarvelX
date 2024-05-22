package com.alexgreece45.marvelx.ui.screens.introLoading

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.effect.OneTimeLaunchedEffect
import com.alexgreece45.marvelx.ui.navigation.NavArgHelper
import com.alexgreece45.marvelx.ui.navigation.Screen
import com.alexgreece45.marvelx.ui.navigation.models.NavArg
import com.alexgreece45.marvelx.util.extensions.popUpToTop
import com.alexgreece45.marvelx.viewModels.IntroLoadingContract
import com.alexgreece45.marvelx.viewModels.IntroLoadingViewModel
import com.alexgreece45.marvelx.viewModels.home.models.HomeAlertDialogErrorType
import org.koin.androidx.compose.koinViewModel

@Composable
fun IntroLoadingScreen(
    navController: NavController,
    viewModel: IntroLoadingViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    OneTimeLaunchedEffect {
        viewModel.setEvent(IntroLoadingContract.Event.Init)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is IntroLoadingContract.Effect.Navigation.NavigateToHomeScreenWithErrorMessage -> {
                    NavArgHelper.setArgument(
                        NavArg.HomeAlertDialogErrorType,
                        HomeAlertDialogErrorType.HasError(effect.errorMessage)
                    )
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
                            userId = effect.userId,
                            otpFlowType = effect.otpFlowType,
                            verificationStep = effect.verificationStep,
                            shouldRememberUserSession = effect.shouldRememberUserSession
                        )
                    ) {
                        popUpToTop(navController)
                    }
                }
            }
        }
    }

    ContentScreen(isLoading = false) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(state.loadingStage.label)
                    )
                }
            }
        }
    }
}