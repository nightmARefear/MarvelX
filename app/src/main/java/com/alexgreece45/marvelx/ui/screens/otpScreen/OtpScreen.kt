package com.alexgreece45.marvelx.ui.screens.otpScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.alexgreece45.marvelx.domain.viewmodels.OtpEffect
import com.alexgreece45.marvelx.domain.viewmodels.OtpEvent
import com.alexgreece45.marvelx.domain.viewmodels.OtpViewModel
import com.alexgreece45.marvelx.navigation.Screen
import com.alexgreece45.marvelx.util.ContentScreen
import com.alexgreece45.marvelx.util.OutlinedTextFieldValidation
import com.alexgreece45.marvelx.util.VSpacer


/**
 * The OTP (One Time Password) screen that is used to double check the user's identity either when
 * signing up, or when their token has expired.
 *
 * Navigation **to** and **from** this screen ***must*** be handled very carefully.
 * Navigation to this screen MUST only happen when instructed either by [LogInScreen],
 * or [SignUpScreen].
 * */
@Composable
fun OtpScreen(
    navController: NavController,
    userId: Int,
    viewModel: OtpViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.setEvent(
            OtpEvent.InitView(
                userId = userId
            )
        )

        viewModel.effect.collect { effect ->
            when (effect) {
                is OtpEffect.Navigation.NavigateToNextScreen -> {
                    navController.navigate(effect.screenRoute) {
                        popUpTo(Screen.OtpScreen.route) {
                            inclusive = true
                        }
                    }
                }

                is OtpEffect.Navigation.NavigateToPreviousScreenWithError -> TODO("Navigation to previous screen (LogInScreen/SignUpScreen) with error message, is not supported yet.")
                OtpEffect.Navigation.NavigateToAllCharactersScreen -> {
                    navController.navigate(Screen.BottomNavigationScreens.AllCharactersScreen.route) {
                        popUpTo(Screen.RegisterScreens.getNestedGraphRootRoute()) {
                            inclusive = true
                        }
                    }
                }
            }
        }
    }

    ContentScreen(
        onBack = { navController.popBackStack() },
        showToolbar = true,
        toolbarTitle = "OTP"
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    AnimatedVisibility(
                        visible = state.errorMessage != null && state.errorMessage?.isNotBlank() == true,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = state.errorMessage.toString(),
                            color = MaterialTheme.colors.error,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.body1,
                        )
                    }

                    VSpacer.Small()

                    OutlinedTextFieldValidation(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.userEnteredOtp,
                        onValueChange = { newOtp ->
                            viewModel.setEvent(
                                OtpEvent.OnOtpFieldValueChange(newOtp)
                            )
                        },
                        errorMessage = state.enteredOtpValidationResult.messages.firstOrNull()
                            .orEmpty(),
                        isError = state.enteredOtpValidationResult.isValid.not(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                viewModel.setEvent(
                                    OtpEvent.OnDoneClick
                                )
                            }
                        )
                    )
                }
            }
        }
    }
}