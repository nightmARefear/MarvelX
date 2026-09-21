package com.alexgreece45.marvelx.ui.screens.changePassword

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarEvent
import com.alexgreece45.marvelx.common_ui.util.ObserveUiEffect
import com.alexgreece45.marvelx.core.common.util.flows.single_observer_flow.SingleObserverFlow
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.ContentScreenHeader
import com.alexgreece45.marvelx.ui.components.OutlinedTextFieldValidation
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.WrapButton
import com.alexgreece45.marvelx.ui.components.WrapTextButton
import com.alexgreece45.marvelx.ui.components.error.ContentErrorScreen
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.SPACING_TINY
import com.alexgreece45.marvelx.util.VSpacer
import com.alexgreece45.marvelx.util.constants.ConstantsInputField

@Composable
fun ChangePasswordScreen(
    onEvent: (ChangePasswordContract.Event) -> Unit,
    state: ChangePasswordContract.State,
    effect: SingleObserverFlow<ChangePasswordContract.Effect>,
    snackbarEffects: SingleObserverFlow<SnackbarEvent>,
    requestIdEmail: String?,
    onNavigationEffect: (ChangePasswordContract.Effect.Navigation) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        onEvent(ChangePasswordContract.Event.Init(requestIdEmail))
    }

    ObserveUiEffect(flow = effect, tag = "ChangePasswordScreen") { uiEffect ->
        when (uiEffect) {
            is ChangePasswordContract.Effect.Navigation -> onNavigationEffect(uiEffect)
        }
    }

    ContentScreen(
        isLoading = state.isLoading,
        navigableAction = ScreenNavigableAction.Backable(
            onBack = { onEvent(ChangePasswordContract.Event.OnBackClicked) }
        ),
        onBack = { onEvent(ChangePasswordContract.Event.OnBackClicked) },
        showToolbar = true,
        snackbarEvents = snackbarEffects,
        stickyBottom = {
            WrapButton(
                onClick = {
                    keyboardController?.hide()
                    onEvent(ChangePasswordContract.Event.OnSubmitClicked)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.isSubmitEnabled
            ) {
                Text(text = stringResource(R.string.change_password_screen_button_sticky))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
        ) {
            ContentScreenHeader(
                title = stringResource(R.string.change_password_screen_title),
                subtitle = stringResource(R.string.change_password_screen_subtitle)
            )

            VSpacer.Medium()

            PasswordField(
                value = state.password,
                label = stringResource(R.string.screen_change_password_input_label_new_password),
                errorText = state.passwordValidation.message,
                isVisible = state.isPasswordVisible,
                imeAction = ImeAction.Next,
                onValueChange = { onEvent(ChangePasswordContract.Event.OnPasswordChanged(it)) },
                onToggleVisibility = { onEvent(ChangePasswordContract.Event.OnTogglePasswordVisibility) }
            )

            VSpacer.Small()

            StrengthMeter(strength = state.strength)

            VSpacer.Medium()

            PasswordField(
                value = state.confirmPassword,
                label = stringResource(R.string.screen_change_password_input_label_confirm_password),
                errorText = state.confirmPasswordValidation.message,
                isVisible = state.isConfirmPasswordVisible,
                imeAction = ImeAction.Done,
                onValueChange = { onEvent(ChangePasswordContract.Event.OnConfirmPasswordChanged(it)) },
                onToggleVisibility = { onEvent(ChangePasswordContract.Event.OnToggleConfirmPasswordVisibility) },
                onDone = {
                    keyboardController?.hide()
                    onEvent(ChangePasswordContract.Event.OnSubmitClicked)
                }
            )

            VSpacer.Medium()

            PasswordRules(password = state.password)
        }
    }

    state.error?.let { error ->
        ContentErrorScreen(
            errorSubtitle = error.errorSubtitle,
            onClose = error.onCloseClicked,
            errorButtonLayout = error.errorButtonLayout
        )
    }
}

@Composable
private fun PasswordField(
    value: String,
    label: String,
    errorText: String,
    isVisible: Boolean,
    imeAction: ImeAction,
    onValueChange: (String) -> Unit,
    onToggleVisibility: () -> Unit,
    onDone: (() -> Unit)? = null
) {
    OutlinedTextFieldValidation(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = label,
        errorText = errorText,
        isError = value.isNotEmpty() && errorText.isNotBlank(),
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(onDone = { onDone?.invoke() }),
        trailingIcon = {
            WrapTextButton(onClick = onToggleVisibility) {
                Text(
                    text = stringResource(
                        if (isVisible) {
                            R.string.screen_change_password_input_action_hide
                        } else {
                            R.string.screen_change_password_input_action_show
                        }
                    )
                )
            }
        }
    )
}

@Composable
private fun StrengthMeter(strength: PasswordStrength) {
    if (strength == PasswordStrength.NONE) return

    val (progress, color, labelResId) = when (strength) {
        PasswordStrength.NONE -> Triple(0f, Color.Transparent, R.string.screen_change_password_strength_weak)
        PasswordStrength.WEAK -> Triple(0.25f, MaterialTheme.customColors.genericRed, R.string.screen_change_password_strength_weak)
        PasswordStrength.FAIR -> Triple(0.5f, MaterialTheme.customColors.genericOrangeColor, R.string.screen_change_password_strength_fair)
        PasswordStrength.GOOD -> Triple(0.75f, MaterialTheme.customColors.info, R.string.screen_change_password_strength_good)
        PasswordStrength.STRONG -> Triple(1f, MaterialTheme.customColors.greenCheckmark, R.string.screen_change_password_strength_strong)
    }

    Column(verticalArrangement = Arrangement.spacedBy(SPACING_TINY.dp)) {
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxWidth(),
            color = color,
            backgroundColor = color.copy(alpha = 0.2f)
        )

        Text(
            text = stringResource(R.string.screen_change_password_strength_label, stringResource(labelResId)),
            color = color,
            style = MaterialTheme.typography.caption
        )
    }
}

@Composable
private fun PasswordRules(password: String) {
    Column(verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)) {
        Text(
            text = stringResource(R.string.screen_change_password_rules_title),
            style = MaterialTheme.typography.subtitle1
        )

        PasswordRule(
            label = stringResource(
                R.string.screen_change_password_rule_length,
                ConstantsInputField.Password.minLength
            ),
            isMet = password.length >= ConstantsInputField.Password.minLength
        )
        PasswordRule(
            label = stringResource(R.string.screen_change_password_rule_uppercase),
            isMet = password.any { it.isUpperCase() }
        )
        PasswordRule(
            label = stringResource(R.string.screen_change_password_rule_lowercase),
            isMet = password.any { it.isLowerCase() }
        )
        PasswordRule(
            label = stringResource(R.string.screen_change_password_rule_digit),
            isMet = password.any { it.isDigit() }
        )
    }
}

@Composable
private fun PasswordRule(label: String, isMet: Boolean) {
    val color = if (isMet) {
        MaterialTheme.customColors.greenCheckmark
    } else {
        MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_check),
            contentDescription = null,
            modifier = Modifier.size(SPACING_MEDIUM.dp),
            tint = color
        )

        Text(
            text = label,
            color = color,
            style = MaterialTheme.typography.body2
        )
    }
}
