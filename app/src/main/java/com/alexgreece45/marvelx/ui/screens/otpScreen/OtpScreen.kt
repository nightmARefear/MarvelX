package com.alexgreece45.marvelx.ui.screens.otpScreen

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.domain.viewmodels.OtpEffect
import com.alexgreece45.marvelx.domain.viewmodels.OtpEvent
import com.alexgreece45.marvelx.domain.viewmodels.OtpViewModel
import com.alexgreece45.marvelx.navigation.Screen
import com.alexgreece45.marvelx.ui.ContentScreen
import com.alexgreece45.marvelx.ui.IconData
import com.alexgreece45.marvelx.ui.ToolbarAction
import com.alexgreece45.marvelx.util.OTP_LENGTH
import com.alexgreece45.marvelx.util.VSpacer


/**
 * The OTP (One Time Password) screen is used to double check the user's identity when
 * logging in, signing up.
 *
 * Navigation **to** and **from** this screen ***must*** be handled with caution.
 * Navigation to this screen MUST only happen when instructed either by [LogInScreen],
 * or [SignUpScreen].
 * */
@Composable
fun OtpScreen(
    navController: NavController,
    userId: Int,
    viewModel: OtpViewModel = hiltViewModel(),
    setUserId: (Int) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        // Requests focus.
        focusRequester.requestFocus()

        viewModel.setEvent(
            OtpEvent.InitView(
                userId = userId
            )
        )

        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is OtpEffect.Navigation.NavigateToNextScreen -> {
                    navController.navigate(effect.screenRoute) {
                        popUpTo(Screen.OtpScreen.route) {
                            inclusive = true
                        }
                    }
                }

                is OtpEffect.Navigation.NavigateToPreviousScreenWithError -> {
                    Toast.makeText(context, effect.errorMessage, Toast.LENGTH_LONG).show()
                    navController.popBackStack()
                }

                OtpEffect.Navigation.NavigateToAllCharactersScreen -> {
                    setUserId.invoke(userId)
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
        isLoading = state.isLoading,
        showToolbar = true,
        toolbarTitle = stringResource(R.string.otp_screen_toolbar_title),
        toolbarActions = listOf(
            ToolbarAction(
                label = stringResource(R.string.otp_screen_toolbar_action_label_send),
                order = 1,
                iconData = IconData(
                    iconResource = R.drawable.ic_send_32,
                    contentDescription = R.string.otp_screen_content_description_send,
                    enabled = state.sendButton
                ),
                onClick = {
                    viewModel.setEvent(OtpEvent.OnSendClick)
                }
            )
        )
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

                    // OTP
                    OtpTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        otp = state.otp,
                        onValueChange = { value, otpInputFilled ->
                            viewModel.setEvent(
                                OtpEvent.OnOtpFieldValueChange(
                                    otp = value
                                )
                            )
                        },
                        onDoneClick = {
                            viewModel.setEvent(OtpEvent.OnDoneClick)
                        }
                    )

                    VSpacer.Small()

                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.otp_screen_input_field_label),
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.body2
                    )
                }
            }
        }
    }
}

@Composable
fun OtpTextField(
    modifier: Modifier = Modifier,
    otp: String,
    onValueChange: (String, Boolean) -> Unit,
    onDoneClick: () -> Unit
) {
    BasicTextField(
        modifier = modifier,
        value = TextFieldValue(
            otp,
            selection = TextRange(otp.length)
        ),
        onValueChange = {
            if (it.text.length <= OTP_LENGTH) {
                onValueChange.invoke(it.text, it.text.length == OTP_LENGTH)
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onDoneClick.invoke()
            }
        ),
        decorationBox = {
            Row(horizontalArrangement = Arrangement.Center) {
                repeat(OTP_LENGTH) { index ->
                    CharView(
                        index = index,
                        text = otp
                    )
                    VSpacer.Small()
                }
            }
        }
    )
}

@Composable
private fun CharView(
    index: Int,
    text: String
) {
    val isFocused = text.length == index
    val char = when {
        index == text.length -> "0"
        index > text.length -> ""
        else -> text[index].toString()
    }
    Text(
        modifier = Modifier
            .width(40.dp)
            .border(
                width = 1.dp,
                color = when {
                    isFocused -> MaterialTheme.colors.onPrimary
                    else -> MaterialTheme.colors.onBackground
                },
                shape = MaterialTheme.shapes.medium
            )
            .padding(2.dp),
        text = char,
        style = MaterialTheme.typography.h4,
        color = if (isFocused) MaterialTheme.colors.onBackground else MaterialTheme.colors.onPrimary,
        textAlign = TextAlign.Center
    )
}