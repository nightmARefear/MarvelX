package com.alexgreece45.marvelx.ui.screens.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.network_logic.models_api.user.Gender
import com.alexgreece45.marvelx.ui.components.AlertDialogButton
import com.alexgreece45.marvelx.ui.components.AlertDialogError
import com.alexgreece45.marvelx.ui.components.ContainedButton
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.ExposedDropdownMenu
import com.alexgreece45.marvelx.ui.components.OutlinedTextFieldValidation
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.models.ErrorButtonType
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.VSpacer
import com.alexgreece45.marvelx.util.extensions.autofill
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SignUpScreen(
    state: SignupState,
    onEventSent: (SignupEvent) -> Unit,
    effect: Flow<SignupEffect>,
    navigateToAllCharactersScreen: () -> Unit,
    navigateToOtpScreen: (Int, String, String, Boolean) -> Unit,
    popBackStack: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        effect.collect { effect ->
            when (effect) {
                is SignupEffect.ShowAlertDialogError -> {
                    onEventSent(
                        SignupEvent.AlertDialogError.OnShow(
                            errorMessage = effect.errorMessage
                        )
                    )
                }

                SignupEffect.Navigation.NavigateToAllCharactersScreen -> {
                    navigateToAllCharactersScreen()
                }

                is SignupEffect.Navigation.NavigateToOtpScreen -> {
                    navigateToOtpScreen(
                        effect.userId,
                        effect.otpFlowType,
                        effect.verificationStep,
                        effect.shouldRememberUserSession
                    )
                }

                SignupEffect.CloseKeyboardAndRemoveFocus -> {
                    keyboard?.hide()
                    focusManager.clearFocus(true)
                }

                SignupEffect.OnDoneClick -> {
                    onEventSent(SignupEvent.OnSignupButtonClick)
                }
            }
        }
    }

    ContentScreen(
        isLoading = state.isLoading,
        navigableAction = ScreenNavigableAction.Backable(popBackStack),
        showToolbar = true,
        toolbarTitle = stringResource(R.string.sign_up_screen_toolbar_title)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
        ) {
            // Alert Dialog Error
            item {
                AlertDialogError(
                    isVisible = state.isAlertDialogVisible,
                    title = state.alertDialogTitle,
                    errorMessage = state.alertDialogErrorMessage,
                    confirmButton = AlertDialogButton(
                        value = stringResource(R.string.generic_ok_button),
                        buttonType = ErrorButtonType.Text,
                        onClick = { onEventSent(SignupEvent.AlertDialogError.OnOkClick) }
                    )
                )
            }

            // First name & Last name
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
                ) {
                    // First Name.
                    OutlinedTextFieldValidation(
                        modifier = Modifier.weight(1f),
                        value = state.firstName,
                        label = stringResource(R.string.sign_up_screen_input_field_label_first_name),
                        onValueChange = { newValue ->
                            onEventSent(SignupEvent.FieldValueChange.FirstName(firstName = newValue))
                        },
                        errorMessage = state.firstNameValidationResult.messages.firstOrNull()
                            .orEmpty(),
                        isError = state.firstNameValidationResult.isValid.not() &&
                                state.firstNameValidationResult.messages.firstOrNull() != null,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                focusManager.moveFocus(FocusDirection.Next)
                            }
                        )
                    )

                    // Last Name.
                    OutlinedTextFieldValidation(
                        modifier = Modifier.weight(1f),
                        value = state.lastName,
                        label = stringResource(R.string.sign_up_screen_input_field_label_last_name),
                        onValueChange = { newValue ->
                            onEventSent(SignupEvent.FieldValueChange.LastName(lastName = newValue))
                        },
                        errorMessage = state.lastNameValidationResult.messages.firstOrNull()
                            .orEmpty(),
                        isError = state.lastNameValidationResult.isValid.not() &&
                                state.lastNameValidationResult.messages.firstOrNull() != null,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                focusManager.moveFocus(FocusDirection.Next)
                            }
                        )
                    )
                }
            }

            // Gender
            item {
                ExposedDropdownMenu(
                    modifier = Modifier.fillMaxWidth(),
                    expanded = state.isGenderDropdownMenuVisible,
                    value = state.gender?.label?.let { stringResource(it) },
                    label = stringResource(R.string.sign_up_screen_input_field_label_gender),
                    onDismissRequest = { onEventSent(SignupEvent.GenderDropdownMenu.OnDismiss) },
                    onExpandedChange = { onEventSent(SignupEvent.GenderDropdownMenu.OnClick) }
                ) {
                    Gender.asList().forEach { gender ->
                        DropdownMenuItem(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                onEventSent(
                                    SignupEvent.FieldValueChange.Gender(
                                        gender = gender
                                    )
                                )
                            }
                        ) {
                            Text(
                                text = stringResource(gender.label),
                                modifier = Modifier.wrapContentWidth()
                            )
                        }
                    }
                }
            }

            // Email
            item {
                OutlinedTextFieldValidation(
                    modifier = Modifier
                        .fillMaxWidth()
                        .autofill(
                            autofillTypes = listOf(AutofillType.EmailAddress),
                            onFill = { suggestionValue ->
                                onEventSent(SignupEvent.FieldValueChange.Email(email = suggestionValue))
                            }
                        ),
                    value = state.email,
                    label = stringResource(R.string.sign_up_screen_input_field_label_email),
                    onValueChange = { newEmail ->
                        onEventSent(
                            SignupEvent.FieldValueChange.Email(
                                email = newEmail
                            )
                        )
                    },
                    errorMessage = state.emailValidationResult.messages.firstOrNull().orEmpty(),
                    isError = state.emailValidationResult.isValid.not() &&
                            state.emailValidationResult.messages.firstOrNull() != null,
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
            }

            // Password
            item {
                OutlinedTextFieldValidation(
                    modifier = Modifier
                        .fillMaxWidth()
                        .autofill(
                            autofillTypes = listOf(AutofillType.Password),
                            onFill = { suggestionValue ->
                                onEventSent(SignupEvent.FieldValueChange.Password(password = suggestionValue))
                            }
                        ),
                    value = state.password,
                    label = stringResource(R.string.sign_up_screen_input_field_label_password),
                    onValueChange = { newPassword ->
                        onEventSent(
                            SignupEvent.FieldValueChange.Password(
                                password = newPassword
                            )
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { onEventSent(SignupEvent.OnPasswordVisibilityIconClick) }) {
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
                    isError = state.passwordValidationResult.isValid.not() &&
                            state.passwordValidationResult.messages.firstOrNull() != null,
                    visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { onEventSent(SignupEvent.OnDoneClick) }
                    )
                )
            }

            item {
                VSpacer.Small()
            }

            // Signup button
            item {
                ContainedButton(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.signupButton,
                    onClick = { onEventSent(SignupEvent.OnSignupButtonClick) }
                ) {
                    Text(
                        text = stringResource(R.string.sign_up_screen_signup_button)
                    )
                }
            }
        }
    }
}