package com.alexgreece45.marvelx.ui.screens.registerScreens.logInScreen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.domain.viewmodels.LogInEffect
import com.alexgreece45.marvelx.domain.viewmodels.LogInEvent
import com.alexgreece45.marvelx.domain.viewmodels.LogInViewModel
import com.alexgreece45.marvelx.navigation.Screen
import com.alexgreece45.marvelx.ui.AlertDialogError
import com.alexgreece45.marvelx.ui.AlertDialogButton
import com.alexgreece45.marvelx.ui.ContentScreen
import com.alexgreece45.marvelx.ui.LoadingScreen
import com.alexgreece45.marvelx.ui.OutlinedTextFieldValidation
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.ui.ValueType
import logcat.logcat

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LogInScreen(
    navController: NavController,
    setUserId: (Int) -> Unit,
    viewModel: LogInViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    val context = LocalContext.current
    val keyboard = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.setEvent(
            LogInEvent.InitView
        )

        viewModel.effect.collect { effect ->
            when (effect) {
                is LogInEffect.Navigation.NavigateToAllCharactersScreen -> {
                    // Login was successful so store the userId in the activity viewModel's state.
                    setUserId.invoke(effect.userId)

                    Toast.makeText(
                        context,
                        "user id (${effect.userId}) in activityViewModel state was inserted.",
                        Toast.LENGTH_SHORT
                    ).show()

                    navController.navigate(Screen.BottomNavigationScreens.AllCharactersScreen.route) {
                        popUpTo(Screen.RegisterScreens.getNestedGraphRootRoute()) {
                            inclusive = true
                        }
                    }
                }

                is LogInEffect.Navigation.NavigateToOtpScreen -> {
                    logcat { "effect.userId = ${effect.userId}" }
                    navController.navigate(
                        Screen.OtpScreen.createNavRoute(
                            userId = effect.userId
                        )
                    )
                }

                LogInEffect.CloseKeyboardAndClearFocus -> {
                    keyboard?.hide()
                    focusManager.clearFocus(
                        force = true
                    )
                }
            }
        }
    }

    LoadingScreen(isLoading = state.isLoading) {
        ContentScreen(
            onBack = { navController.popBackStack() },
            showToolbar = true,
            toolbarTitle = stringResource(R.string.login_screen_toolbar_title)
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = paddingValues,
            ) {
                item {
                    AlertDialogError(
                        isVisible = state.isAlertDialogVisible,
                        title = state.alertDialogTitle,
                        errorMessage = state.alertDialogErrorMessage,
                        confirmButton = AlertDialogButton(
                            value = stringResource(R.string.ok),
                            onClick = { viewModel.setEvent(LogInEvent.AlertDialog.OnOkClick) }
                        )
                    )
                }

                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
                    ) {
                        // Email
                        OutlinedTextFieldValidation(
                            modifier = Modifier.fillMaxWidth(),
                            value = state.email,
                            label = stringResource(R.string.login_screen_input_field_label_email),
                            placeholder = stringResource(R.string.login_screen_input_field_placeholder_email),
                            onValueChange = { newValue ->
                                viewModel.setEvent(LogInEvent.FieldValueChange.Email(email = newValue))
                            },
                            errorMessage = state.emailValidationResult.messages.firstOrNull()
                                .orEmpty(),
                            isError = state.emailValidationResult.isValid.not() && state.emailValidationResult.messages.firstOrNull() != null,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    focusManager.moveFocus(FocusDirection.Next)
                                }
                            ),
                            shape = MaterialTheme.shapes.medium
                        )

                        // Password
                        OutlinedTextFieldValidation(
                            modifier = Modifier.fillMaxWidth(),
                            value = state.password,
                            valueType = ValueType.Password(
                                isPasswordVisible = state.isPasswordVisible,
                                onVisibilityIconClick = { viewModel.setEvent(LogInEvent.OnPasswordVisibilityIconClick) }
                            ),
                            label = stringResource(R.string.login_screen_input_field_label_password),
                            placeholder = stringResource(R.string.log_in_screen_input_field_placeholder_password),
                            onValueChange = { newValue ->
                                viewModel.setEvent(LogInEvent.FieldValueChange.Password(password = newValue))
                            },
                            errorMessage = state.passwordValidationResult.messages.firstOrNull()
                                .orEmpty(),
                            isError = state.passwordValidationResult.isValid.not() && state.passwordValidationResult.messages.firstOrNull() != null,
                            visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    viewModel.setEvent(
                                        LogInEvent.OnDoneClick
                                    )
                                }
                            ),
                            shape = MaterialTheme.shapes.medium
                        )

                        // Login button.
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            enabled = state.logInButton,
                            onClick = { viewModel.setEvent(LogInEvent.OnLogInButtonClick) }
                        ) {
                            Text(
                                text = stringResource(R.string.log_in_screen_sign_up_button_text)
                            )
                        }
                    }
                }
            }
        }
    }
}