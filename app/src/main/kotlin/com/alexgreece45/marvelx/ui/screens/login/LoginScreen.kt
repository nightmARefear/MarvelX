package com.alexgreece45.marvelx.ui.screens.login

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalAutofillManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.coroutineScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.OutlinedTextFieldValidation
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.WrapButton
import com.alexgreece45.marvelx.ui.components.WrapClickableText
import com.alexgreece45.marvelx.ui.components.WrapOutlinedButton
import com.alexgreece45.marvelx.ui.components.effect.ObserveUiEffect
import com.alexgreece45.marvelx.ui.components.error.ContentErrorScreen
import com.alexgreece45.marvelx.ui.components.line.HorizontalLine
import com.alexgreece45.marvelx.ui.components.model.ClickableString
import com.alexgreece45.marvelx.ui.screens.login.model.LoginWithGoogleResult
import com.alexgreece45.marvelx.ui.screens.login.model.LoginWithIntentWithGoogleResult
import com.alexgreece45.marvelx.util.HSpacer
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.VSpacer
import com.alexgreece45.marvelx.util.constants.ConstantsUi
import com.alexgreece45.marvelx.util.extensions.clickableNoRipple
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(
    onEventSent: (event: LoginEvent) -> Unit,
    state: LoginState,
    effect: Flow<LoginEffect>,
    onNavigationEffect: (LoginEffect.Navigation) -> Unit
) {

    val context = LocalContext.current
    val keyboard = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val autofillManager = LocalAutofillManager.current

    LaunchedEffect(Unit) {
        onEventSent(LoginEvent.Init)
    }

    ObserveUiEffect(effect) { uiEffect ->
        when (uiEffect) {
            is LoginEffect.Navigation -> onNavigationEffect(uiEffect)

            LoginEffect.CloseKeyboardAndClearFocus -> {
                keyboard?.hide()
                focusManager.clearFocus(
                    force = true
                )
            }

            is LoginEffect.ShowToast -> {
                Toast.makeText(context, uiEffect.text, Toast.LENGTH_SHORT).show()
            }

            LoginEffect.SaveAutoFilledData -> {
                autofillManager?.commit()
            }
        }
    }

    ContentScreen(
        isLoading = state.isLoading,
        navigableAction = ScreenNavigableAction.Backable {
            onEventSent(LoginEvent.OnBack)
        },
        onBack = {
            onEventSent(LoginEvent.OnBack)
        },
        showToolbar = true,
        toolbarTitle = stringResource(R.string.login_screen_toolbar_title),
        stickyBottom = {
            // "Don't have an account? Signup" clickable label
            Box(
                modifier = Modifier.padding(bottom = ConstantsUi.LoginScreen.STICKY_BOTTOM_BOTTOM_PADDING.dp),
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
                                onEventSent(LoginEvent.OnSignupTextClicked)
                            }
                        )
                    )
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            // Email
            OutlinedTextFieldValidation(
                value = state.email,
                onValueChange = { newEmail ->
                    onEventSent(LoginEvent.FieldValueChange.Email(email = newEmail))
                },
                modifier = Modifier
                    .semantics { contentType = ContentType.EmailAddress }
                    .fillMaxWidth(),
                label = stringResource(R.string.generic_label_email),
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_person),
                        contentDescription = stringResource(R.string.content_description_person)
                    )
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
            VSpacer.Tiny()

            // Password
            OutlinedTextFieldValidation(
                value = state.password,
                onValueChange = { newPassword ->
                    onEventSent(LoginEvent.FieldValueChange.Password(password = newPassword))
                },
                modifier = Modifier
                    .semantics { contentType = ContentType.Password }
                    .fillMaxWidth(),
                label = stringResource(R.string.generic_label_password),
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_lock),
                        contentDescription = stringResource(R.string.content_description_lock)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { onEventSent(LoginEvent.OnPasswordVisibilityIconClick) }) {
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
                errorMessage = state.passwordValidationResult.messages.firstOrNull().orEmpty(),
                isError = !state.passwordValidationResult.isValid && state.passwordValidationResult.messages.firstOrNull() != null,
                visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        onEventSent(LoginEvent.OnDoneClick)
                    }
                )
            )

            // Remember Me Checkbox & Forgot Password Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.clickableNoRipple { onEventSent(LoginEvent.OnRememberMeCheckboxClick) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = state.isRememberMeCheckboxChecked,
                        onCheckedChange = { onEventSent(LoginEvent.OnRememberMeCheckboxClick) }
                    )

                    Text(
                        text = stringResource(R.string.login_screen_remember_me),
                        style = MaterialTheme.typography.body2
                    )
                }

                TextButton(
                    onClick = { onEventSent(LoginEvent.OnForgotPasswordButtonClick) }
                ) {
                    Text(
                        text = stringResource(R.string.login_screen_forgot_password_button),
                        style = MaterialTheme.typography.body2
                    )
                }
            }
            VSpacer.Small()

            // Login button.
            WrapButton(
                onClick = { onEventSent(LoginEvent.OnLoginButtonClick) },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.isLoginButtonEnabled
            ) {
                Text(
                    text = stringResource(R.string.login_screen_login_button)
                )
            }

            VSpacer.Medium()

            // Login with google.
            LoginWithGoogle {
                onEventSent(LoginEvent.OnLoginWithGoogleResult(it))
            }
        }
    }

    state.error?.let { error ->
        ContentErrorScreen(
            errorTitle = error.errorTitle,
            errorSubtitle = error.errorSubtitle,
            onClose = error.onCloseClicked,
            errorButtonLayout = error.errorButtonLayout
        )
    }
}

@Composable
private fun LoginWithGoogle(onLoginWithGoogleResult: (LoginWithIntentWithGoogleResult) -> Unit) {
    val context = LocalContext.current
    val googleAuthUiClient = remember { GoogleAuthUiClient(Identity.getSignInClient(context)) }

    val linePaddingSpace by remember { mutableStateOf(16.dp) }
    val lineWidth by remember { mutableStateOf(1.25.dp) }

    val lifecycleScope = LocalLifecycleOwner.current.lifecycle.coroutineScope

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            when (result.resultCode) {
                RESULT_OK -> {
                    lifecycleScope.launch {
                        result.data?.let { intent ->
                            val loginWithIntentResult = googleAuthUiClient.loginWithIntent(intent)
                            onLoginWithGoogleResult(loginWithIntentResult)
                        } ?: run {
                            onLoginWithGoogleResult(LoginWithIntentWithGoogleResult.UnknownError)
                        }
                    }
                }

                RESULT_CANCELED -> {
                    onLoginWithGoogleResult(LoginWithIntentWithGoogleResult.Cancelled)
                }

                else -> {
                    onLoginWithGoogleResult(LoginWithIntentWithGoogleResult.UnknownError)
                }
            }
        }
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalLine(
                color = MaterialTheme.colors.primary,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = linePaddingSpace),
                strokeWidth = lineWidth
            )
            Text(text = stringResource(id = R.string.login_screen_or_login_with))
            HorizontalLine(
                color = MaterialTheme.colors.primary,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = linePaddingSpace),
                strokeWidth = lineWidth
            )
        }

        WrapOutlinedButton(
            onClick = {
                lifecycleScope.launch {
                    when (val loginWithGoogleResult = googleAuthUiClient.login()) {
                        is LoginWithGoogleResult.Success -> {
                            launcher.launch(
                                input = IntentSenderRequest.Builder(
                                    loginWithGoogleResult.intentSender
                                ).build()
                            )
                        }

                        is LoginWithGoogleResult.Failed -> {
                            onLoginWithGoogleResult(
                                LoginWithIntentWithGoogleResult.Failed(loginWithGoogleResult.errorMessage)
                            )
                        }
                    }
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            shape = RoundedCornerShape(25.dp),
            border = BorderStroke(lineWidth, MaterialTheme.colors.primary)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_google_logo),
                contentDescription = stringResource(
                    id = R.string.content_description_ic_google_logo
                )
            )

            HSpacer.Small()

            Text(text = stringResource(R.string.login_screen_google_button))
        }
    }
}
