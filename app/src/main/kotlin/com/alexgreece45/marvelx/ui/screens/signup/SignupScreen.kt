package com.alexgreece45.marvelx.ui.screens.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.util.ObserveUiEffect
import com.alexgreece45.marvelx.core.common.util.flows.single_observer_flow.SingleObserverFlow
import com.alexgreece45.marvelx.ui.components.AlertDialogButton
import com.alexgreece45.marvelx.ui.components.AlertDialogError
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.ExposedDropdownMenu
import com.alexgreece45.marvelx.ui.components.OutlinedTextFieldValidation
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.WrapButton
import com.alexgreece45.marvelx.ui.model.Gender
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.SPACING_TINY

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun SignUpScreen(
    onEvent: (SignupEvent) -> Unit,
    state: SignupState,
    effect: SingleObserverFlow<SignupEffect>,
    onNavigationEffect: (SignupEffect.Navigation) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current

    ObserveUiEffect(flow = effect, tag = "SignupScreen") { uiEffect ->
        when (uiEffect) {
            is SignupEffect.ShowAlertDialogError -> {
                onEvent(
                    SignupEvent.AlertDialogError.OnShow(
                        errorMessage = uiEffect.errorMessage
                    )
                )
            }

            is SignupEffect.Navigation -> onNavigationEffect(uiEffect)

            SignupEffect.CloseKeyboardAndRemoveFocus -> {
                keyboard?.hide()
                focusManager.clearFocus(true)
            }

            SignupEffect.OnDoneClick -> {
                onEvent(SignupEvent.OnSignupButtonClick)
            }
        }
    }

    ContentScreen(
        isLoading = state.isLoading,
        navigableAction = ScreenNavigableAction.Backable(onBack = { onEvent(SignupEvent.OnBack) }),
        onBack = { onEvent(SignupEvent.OnBack) },
        showToolbar = true,
        toolbarTitle = stringResource(R.string.sign_up_screen_toolbar_title)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(SPACING_TINY.dp)
        ) {
            AlertDialogError(
                isVisible = state.isAlertDialogVisible,
                title = state.alertDialogTitle,
                errorMessage = { Text(text = state.alertDialogErrorMessage) },
                confirmButton = AlertDialogButton(
                    label = stringResource(R.string.generic_ok_button),
                    onClick = { onEvent(SignupEvent.AlertDialogError.OnOkClick) }
                )
            )

            // First Name & Last Name
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
            ) {
                // First Name.
                OutlinedTextFieldValidation(
                    value = state.firstName,
                    onValueChange = { newValue ->
                        onEvent(SignupEvent.FieldValueChange.FirstName(firstName = newValue))
                    },
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.generic_label_first_name),
                    errorText = state.firstNameValidationResult.messages.firstOrNull()
                        .orEmpty(),
                    isError = !state.firstNameValidationResult.isValid &&
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

                // Last name
                OutlinedTextFieldValidation(
                    value = state.lastName,
                    onValueChange = { newValue ->
                        onEvent(SignupEvent.FieldValueChange.LastName(lastName = newValue))
                    },
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.generic_label_last_name),
                    errorText = state.lastNameValidationResult.messages.firstOrNull()
                        .orEmpty(),
                    isError = !state.lastNameValidationResult.isValid &&
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

            // Gender
            ExposedDropdownMenu(
                label = stringResource(R.string.generic_label_gender),
                expanded = state.isGenderDropdownMenuVisible,
                modifier = Modifier.fillMaxWidth(),
                value = state.gender?.label?.let { stringResource(it) },
                onDismissRequest = { onEvent(SignupEvent.GenderDropdownMenu.OnDismiss) },
                onExpandedChange = { onEvent(SignupEvent.GenderDropdownMenu.OnClick) }
            ) {
                Gender.asList().forEach { gender ->
                    DropdownMenuItem(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onEvent(
                                SignupEvent.FieldValueChange.GenderDropDown(
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

            // Email
            OutlinedTextFieldValidation(
                value = state.email,
                onValueChange = { newEmail ->
                    onEvent(
                        SignupEvent.FieldValueChange.Email(
                            email = newEmail
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentType = ContentType.EmailAddress },
                label = stringResource(R.string.generic_label_email),
                errorText = state.emailValidationResult.messages.firstOrNull().orEmpty(),
                isError = !state.emailValidationResult.isValid &&
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

            // Password
            OutlinedTextFieldValidation(
                value = state.password,
                onValueChange = { newPassword ->
                    onEvent(
                        SignupEvent.FieldValueChange.Password(
                            password = newPassword
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentType = ContentType.NewPassword },
                label = stringResource(R.string.generic_label_password),
                trailingIcon = {
                    IconButton(onClick = { onEvent(SignupEvent.OnPasswordVisibilityIconClick) }) {
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
                errorText = state.passwordValidationResult.messages.firstOrNull()
                    .orEmpty(),
                isError = !state.passwordValidationResult.isValid &&
                        state.passwordValidationResult.messages.firstOrNull() != null,
                visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onEvent(SignupEvent.OnDoneClick) }
                )
            )

            // Signup button
            WrapButton(
                modifier = Modifier.fillMaxWidth(),
                enabled = state.signupButton,
                onClick = { onEvent(SignupEvent.OnSignupButtonClick) }
            ) {
                Text(
                    text = stringResource(R.string.sign_up_screen_signup_button)
                )
            }
        }
    }
}