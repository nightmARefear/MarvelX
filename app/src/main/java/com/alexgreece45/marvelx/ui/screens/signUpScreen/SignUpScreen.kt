package com.alexgreece45.marvelx.ui.screens.signUpScreen

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.domain.viewmodels.SignUpEffect
import com.alexgreece45.marvelx.domain.viewmodels.SignUpEvent
import com.alexgreece45.marvelx.domain.viewmodels.SignUpViewModel
import com.alexgreece45.marvelx.navigation.Screen
import com.alexgreece45.marvelx.util.ContentScreen
import com.alexgreece45.marvelx.util.OutlinedTextFieldValidation
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.VSpacer
import com.alexgreece45.marvelx.util.ValueType

@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SignUpEffect.DisplayErrorToastMessage -> {
                    Toast.makeText(context, effect.errorMessage, Toast.LENGTH_LONG).show()
                }

                SignUpEffect.SignUpSuccess -> {
                    navController.navigate(Screen.AllCharactersScreen.route)
                }
            }
        }
    }

    ContentScreen(
        showToolbar = true,
        toolbarTitle = stringResource(R.string.sign_up_screen_toolbar_title)
    ) { paddingValues ->
        LazyColumn(contentPadding = paddingValues) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
                    ) {
                        // First Name.
                        OutlinedTextFieldValidation(
                            modifier = Modifier.weight(1f),
                            value = state.firstName,
                            label = stringResource(R.string.sign_up_screen_input_field_label_first_name),
                            placeholder = stringResource(R.string.sign_up_screen_input_field_placeholder_first_name),
                            onValueChange = { newValue ->
                                viewModel.setEvent(SignUpEvent.FieldValueChange.FirstName(firstName = newValue))
                            },
                            errorMessage = state.firstNameValidationResult.messages.firstOrNull()
                                .orEmpty(),
                            isError = state.firstNameValidationResult.isValid.not(),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    focusManager.moveFocus(FocusDirection.Next)
                                }
                            ),
                            shape = MaterialTheme.shapes.medium
                        )

                        // Last Name.
                        OutlinedTextFieldValidation(
                            modifier = Modifier.weight(1f),
                            value = state.lastName,
                            label = stringResource(R.string.sign_up_screen_input_field_label_last_name),
                            placeholder = stringResource(R.string.sign_up_screen_input_field_placeholder_last_name),
                            onValueChange = { newValue ->
                                viewModel.setEvent(SignUpEvent.FieldValueChange.LastName(lastName = newValue))
                            },
                            errorMessage = state.lastNameValidationResult.messages.firstOrNull()
                                .orEmpty(),
                            isError = state.lastNameValidationResult.isValid.not(),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    focusManager.moveFocus(FocusDirection.Next)
                                }
                            ),
                            shape = MaterialTheme.shapes.medium
                        )
                    }
                    VSpacer.Medium()

                    // Gender
                    /* OutlinedTextFieldValidation(
                         modifier = Modifier.fillMaxWidth(),
                         value = state.gender,
                         label = stringResource(R.string.sign_up_screen_input_field_label_gender),
                         onValueChange = { newValue ->
                             viewModel.setEvent(SignUpEvent.FieldValueChange.Gender(gender = newValue))
                         },
                         error = state.genderValidationResult.messages.firstOrNull().toString(),
                         isError = state.genderValidationResult.isValid.not(),
                         shape = MaterialTheme.shapes.medium
                     )
                     VSpacer.Medium()*/

                    // Email
                    OutlinedTextFieldValidation(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.email,
                        label = stringResource(R.string.sign_up_screen_input_field_label_email),
                        placeholder = stringResource(R.string.sign_up_screen_input_field_placeholder_email),
                        onValueChange = { newValue ->
                            viewModel.setEvent(SignUpEvent.FieldValueChange.Email(email = newValue))
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
                            onVisibilityIconClick = { viewModel.setEvent(SignUpEvent.OnPasswordVisibilityIconClick) }
                        ),
                        label = stringResource(R.string.sign_up_screen_input_field_label_password),
                        placeholder = stringResource(R.string.sign_up_screen_input_field_placeholder_password),
                        onValueChange = { newValue ->
                            viewModel.setEvent(SignUpEvent.FieldValueChange.Password(password = newValue))
                        },
                        errorMessage = state.passwordValidationResult.messages.firstOrNull().orEmpty(),
                        isError = state.passwordValidationResult.isValid.not(),
                        visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {}
                        ),
                        shape = MaterialTheme.shapes.medium
                    )
                    VSpacer.Large()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            enabled = state.signUpButton,
                            onClick = { viewModel.setEvent(SignUpEvent.OnSignUpButtonClick) }
                        ) {
                            Text(
                                text = stringResource(R.string.sign_up_screen_sign_up_button_text)
                            )
                        }
                    }
                }
            }
        }
    }
}