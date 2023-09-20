package com.alexgreece45.marvelx.ui.screens.registerScreens.logInScreen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.domain.viewmodels.LogInEffect
import com.alexgreece45.marvelx.domain.viewmodels.LogInEvent
import com.alexgreece45.marvelx.domain.viewmodels.LogInViewModel
import com.alexgreece45.marvelx.navigation.Screen
import com.alexgreece45.marvelx.util.ContentScreen
import com.alexgreece45.marvelx.util.OutlinedTextFieldValidation
import com.alexgreece45.marvelx.util.VSpacer
import com.alexgreece45.marvelx.util.ValueType

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LogInScreen(
    navController: NavController,
    viewModel: LogInViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    val context = LocalContext.current
    val keyboard = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is LogInEffect.DisplayErrorToastMessage -> {
                    Toast.makeText(context, effect.errorMessage, Toast.LENGTH_LONG).show()
                }

                LogInEffect.NavigateToAllCharactersScreen -> {
                    navController.navigate(Screen.BottomNavigationScreens.AllCharactersScreen.route) {
                        popUpTo(Screen.RegisterScreens.getNestedGraphRootRoute()) {
                            inclusive = true
                        }
                    }
                }

                LogInEffect.CloseKeyboard -> {
                    keyboard?.hide()
                }
            }
        }
    }

    ContentScreen(
        onBack = { navController.popBackStack() },
        showToolbar = true,
        toolbarTitle = stringResource(R.string.log_in_screen_toolbar_title)
    ) { paddingValues ->
        LazyColumn(contentPadding = paddingValues) {
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Email
                    OutlinedTextFieldValidation(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.email,
                        label = stringResource(R.string.log_in_screen_input_field_label_email),
                        placeholder = stringResource(R.string.log_in_screen_input_field_placeholder_email),
                        onValueChange = { newValue ->
                            viewModel.setEvent(LogInEvent.FieldValueChange.Email(email = newValue))
                        },
                        errorMessage = state.emailValidationResult.messages.firstOrNull().orEmpty(),
                        isError = state.emailValidationResult.isValid.not(),
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
                    VSpacer.Medium()

                    // Password
                    OutlinedTextFieldValidation(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.password,
                        valueType = ValueType.Password(
                            isPasswordVisible = state.isPasswordVisible,
                            onVisibilityIconClick = { viewModel.setEvent(LogInEvent.OnPasswordVisibilityIconClick) }
                        ),
                        label = stringResource(R.string.log_in_screen_input_field_label_password),
                        placeholder = stringResource(R.string.log_in_screen_input_field_placeholder_password),
                        onValueChange = { newValue ->
                            viewModel.setEvent(LogInEvent.FieldValueChange.Password(password = newValue))
                        },
                        errorMessage = state.passwordValidationResult.messages.firstOrNull()
                            .orEmpty(),
                        isError = state.passwordValidationResult.isValid.not(),
                        visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                viewModel.setEvent(LogInEvent.OnLogInButtonClick)
                            }
                        ),
                        shape = MaterialTheme.shapes.medium
                    )
                    VSpacer.Large()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
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