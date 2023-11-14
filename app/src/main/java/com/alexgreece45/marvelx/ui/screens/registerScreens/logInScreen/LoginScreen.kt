package com.alexgreece45.marvelx.ui.screens.registerScreens.logInScreen

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
import com.alexgreece45.marvelx.viewModels.LoginEffect
import com.alexgreece45.marvelx.viewModels.LoginEvent
import com.alexgreece45.marvelx.viewModels.LoginViewModel
import com.alexgreece45.marvelx.ui.navigation.Screen
import com.alexgreece45.marvelx.ui.components.AlertDialogButton
import com.alexgreece45.marvelx.ui.components.AlertDialogError
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.OutlinedTextFieldValidation
import com.alexgreece45.marvelx.ui.components.ValueType
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import logcat.logcat

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LogInScreen(
    navController: NavController,
    setUserId: (Int) -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    val keyboard = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.setEvent(LoginEvent.InitView)

        viewModel.uiEffect.onEach { effect ->
            when (effect) {
                is LoginEffect.Navigation.NavigateToAllCharactersScreen -> {
                    // Login was successful so store the userId in the activity viewModel's state.
                    setUserId.invoke(effect.userId)

                    logcat { "user id (${effect.userId}) in activityViewModel state was inserted." }
                    navController.navigate(Screen.BottomNavigationScreens.AllCharactersScreen.route) {
                        popUpTo(Screen.RegisterScreens.getNestedGraphRootRoute()) {
                            inclusive = true
                        }
                    }
                }

                is LoginEffect.Navigation.NavigateToOtpScreen -> {
                    logcat { "effect.userId = ${effect.userId}" }
                    navController.navigate(
                        Screen.OtpScreen.createNavRoute(
                            userId = effect.userId
                        )
                    )
                }

                LoginEffect.CloseKeyboardAndClearFocus -> {
                    keyboard?.hide()
                    focusManager.clearFocus(
                        force = true
                    )
                }

                LoginEffect.Navigation.PopBackStack -> {
                    navController.popBackStack().also { logcat { "LoginScreen: popBackStack" } }
                }

                is LoginEffect.ShowErrorDialog -> {
                    viewModel.setEvent(
                        LoginEvent.OnShowErrorDialog(
                            errorMessage = effect.errorMessage
                        )
                    )
                }

                LoginEffect.OnDoneClick -> {
                    viewModel.setEvent(LoginEvent.OnLoginButtonClick)
                }
            }
        }.collect()
    }

    ContentScreen(
        onBack = { viewModel.setEvent(LoginEvent.OnBack) },
        isLoading = state.isLoading,
        showToolbar = true,
        toolbarTitle = stringResource(R.string.login_screen_toolbar_title)
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
                            onClick = { viewModel.setEvent(LoginEvent.AlertDialog.OnOkClick) }
                        )
                    )
                }
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
                        onValueChange = { newEmail ->
                            viewModel.setEvent(LoginEvent.FieldValueChange.Email(email = newEmail))
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
                            onVisibilityIconClick = { viewModel.setEvent(LoginEvent.OnPasswordVisibilityIconClick) }
                        ),
                        label = stringResource(R.string.login_screen_input_field_label_password),
                        placeholder = stringResource(R.string.log_in_screen_input_field_placeholder_password),
                        onValueChange = { newPassword ->
                            viewModel.setEvent(LoginEvent.FieldValueChange.Password(password = newPassword))
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
                                    LoginEvent.OnDoneClick
                                )
                            }
                        ),
                        shape = MaterialTheme.shapes.medium
                    )

                    // Login button.
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = state.loginButton,
                        onClick = { viewModel.setEvent(LoginEvent.OnLoginButtonClick) }
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