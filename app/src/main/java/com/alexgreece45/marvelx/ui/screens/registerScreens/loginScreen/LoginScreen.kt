package com.alexgreece45.marvelx.ui.screens.registerScreens.loginScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.AlertDialogButton
import com.alexgreece45.marvelx.ui.components.AlertDialogError
import com.alexgreece45.marvelx.ui.components.ContainedButton
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.OutlinedTextFieldValidation
import com.alexgreece45.marvelx.ui.components.hightlightedText.WrapClickableText
import com.alexgreece45.marvelx.ui.components.models.ClickableString
import com.alexgreece45.marvelx.ui.navigation.Screen
import com.alexgreece45.marvelx.util.VSpacer
import com.alexgreece45.marvelx.util.extensions.autofill
import com.alexgreece45.marvelx.util.extensions.clickableNoRipple
import com.alexgreece45.marvelx.viewModels.LoginEffect
import com.alexgreece45.marvelx.viewModels.LoginEvent
import com.alexgreece45.marvelx.viewModels.LoginViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val keyboard = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.setEvent(LoginEvent.InitView)

        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is LoginEffect.Navigation.NavigateToAllCharactersScreen -> {
                    navController.navigate(Screen.BottomNavigationScreens.AllCharactersScreen.route) {
                        popUpTo(Screen.RegisterScreens.getNestedGraphRootRoute()) {
                            inclusive = true
                        }
                    }
                }

                is LoginEffect.Navigation.NavigateToOtpScreen -> {
                    navController.navigate(
                        Screen.OtpScreen.createNavRoute(
                            userId = effect.userId,
                            verificationStep = effect.verificationStep,
                            shouldRememberUserSession = effect.shouldRememberUserSession
                        )
                    )
                }

                LoginEffect.Navigation.PopUpToSignupScreen -> {
                    navController.navigate(Screen.RegisterScreens.SignupScreen.route)
                }

                LoginEffect.CloseKeyboardAndClearFocus -> {
                    keyboard?.hide()
                    focusManager.clearFocus(
                        force = true
                    )
                }

                LoginEffect.Navigation.PopBackStack -> {
                    navController.popBackStack()
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
        }
    }

    ContentScreen(
        onBack = { viewModel.setEvent(LoginEvent.OnBack) },
        isLoading = state.isLoading,
        showToolbar = true,
        toolbarTitle = stringResource(R.string.login_screen_toolbar_title)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            if (state.isAlertDialogVisible) {
                AlertDialogError(
                    title = state.alertDialogTitle,
                    errorMessage = state.alertDialogErrorMessage,
                    confirmButton = AlertDialogButton(
                        value = stringResource(R.string.ok),
                        onClick = { viewModel.setEvent(LoginEvent.AlertDialog.OnOkClick) }
                    )
                )
            }

            // Email
            OutlinedTextFieldValidation(
                modifier = Modifier
                    .fillMaxWidth()
                    .autofill(
                        autofillTypes = listOf(AutofillType.EmailAddress),
                        onFill = { suggestionValue ->
                            viewModel.setEvent(LoginEvent.FieldValueChange.Email(email = suggestionValue))
                        }
                    ),
                value = state.email,
                label = stringResource(R.string.login_screen_input_field_label_email),
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_person),
                        contentDescription = stringResource(R.string.content_description_person)
                    )
                },
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
                )
            )
            VSpacer.Custom(12)

            // Password
            OutlinedTextFieldValidation(
                modifier = Modifier
                    .fillMaxWidth()
                    .autofill(
                        autofillTypes = listOf(AutofillType.Password),
                        onFill = { suggestionValue ->
                            viewModel.setEvent(LoginEvent.FieldValueChange.Password(password = suggestionValue))
                        }
                    ),
                value = state.password,
                label = stringResource(R.string.login_screen_input_field_label_password),
                onValueChange = { newPassword ->
                    viewModel.setEvent(LoginEvent.FieldValueChange.Password(password = newPassword))
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_lock),
                        contentDescription = stringResource(R.string.content_description_lock)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { viewModel.setEvent(LoginEvent.OnPasswordVisibilityIconClick) }) {
                        if (state.isPasswordVisible) {
                            Icon(
                                imageVector = Icons.Filled.VisibilityOff,
                                contentDescription = stringResource(R.string.is_password_visible_content_description)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.Visibility,
                                contentDescription = stringResource(R.string.is_password_not_visible_content_description)
                            )
                        }
                    }
                },
                errorMessage = state.passwordValidationResult.messages.firstOrNull()
                    .orEmpty(),
                isError = !state.passwordValidationResult.isValid && state.passwordValidationResult.messages.firstOrNull() != null,
                visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        viewModel.setEvent(LoginEvent.OnDoneClick)
                    }
                )
            )
            VSpacer.Custom(4)

            // Remember Me Checkbox & Forgot Password Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clickableNoRipple { viewModel.setEvent(LoginEvent.OnRememberMeCheckboxClick) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = state.isRememberMeCheckboxChecked,
                        onCheckedChange = { viewModel.setEvent(LoginEvent.OnRememberMeCheckboxClick) }
                    )

                    Text(
                        text = stringResource(R.string.login_screen_remember_me),
                        style = MaterialTheme.typography.body2
                    )
                }

                TextButton(
                    onClick = { viewModel.setEvent(LoginEvent.OnForgotPasswordButtonClick) }
                ) {
                    Text(
                        text = stringResource(R.string.login_screen_forgot_password_button),
                        style = MaterialTheme.typography.body2
                    )
                }
            }
            VSpacer.Custom(12)

            // Login button.
            ContainedButton(
                modifier = Modifier.fillMaxWidth(),
                enabled = state.isLoginButtonEnabled,
                onClick = { viewModel.setEvent(LoginEvent.OnLoginButtonClick) }
            ) {
                Text(
                    text = stringResource(R.string.login_screen_login_button)
                )
            }

            // No account - Signup
            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .weight(0.3f)
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                WrapClickableText(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.login_screen_do_not_have_an_account),
                    textStyle = TextStyle(
                        fontSize = MaterialTheme.typography.subtitle1.fontSize,
                        fontWeight = MaterialTheme.typography.subtitle1.fontWeight,
                        color = MaterialTheme.colors.onBackground,
                        textAlign = TextAlign.Center
                    ),
                    clickableStrings = listOf(
                        ClickableString(
                            text = stringResource(R.string.login_screen_signup_text_button),
                            onClick = {
                                viewModel.setEvent(LoginEvent.OnSignupTextClicked)
                            }
                        )
                    )
                )
            }
        }
    }
}