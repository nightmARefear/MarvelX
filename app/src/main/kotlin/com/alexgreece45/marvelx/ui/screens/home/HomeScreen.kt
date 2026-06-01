package com.alexgreece45.marvelx.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.AlertDialogButton
import com.alexgreece45.marvelx.ui.components.AlertDialogError
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.WrapButton
import com.alexgreece45.marvelx.ui.components.effect.ObserveUiEffect
import com.alexgreece45.marvelx.ui.components.effect.OneTimeLaunchedEffect
import com.alexgreece45.marvelx.util.SPACING_SMALL
import kotlinx.coroutines.flow.Flow

@Composable
fun HomeScreen(
    onEventSent: (event: HomeEvent) -> Unit,
    state: HomeState,
    effect: Flow<HomeEffect>,
    logoutReason: String?,
    onNavigationEffect: (HomeEffect.Navigation) -> Unit,
) {
    OneTimeLaunchedEffect {
        onEventSent(HomeEvent.Init(logoutReason))
    }

    ObserveUiEffect(effect) { uiEffect ->
        when (uiEffect) {
            is HomeEffect.Navigation -> onNavigationEffect(uiEffect)
        }
    }

    ContentScreen(
        isLoading = state.isLoading,
        navigableAction = ScreenNavigableAction.Backable {
            onEventSent(HomeEvent.OnBack)
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = paddingValues,
        ) {
            item {
                state.alertDialogError?.let { homeAlertDialogErrorView ->
                    AlertDialogError(
                        isVisible = true,
                        title = homeAlertDialogErrorView.title,
                        errorMessage = { Text(text = homeAlertDialogErrorView.description) },
                        confirmButton = AlertDialogButton(
                            label = homeAlertDialogErrorView.buttonLabel,
                            onClick = homeAlertDialogErrorView.alertDialogOnClick
                        )
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.screen_home_toolbar_title),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.h4
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
                ) {
                    // Login button.
                    WrapButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onEventSent(HomeEvent.OnLoginButtonClicked) }
                    ) {
                        Text(
                            text = stringResource(R.string.screen_home_login_button),
                        )
                    }

                    // Signup button.
                    WrapButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onEventSent(HomeEvent.OnSignupButtonClicked) }
                    ) {
                        Text(
                            text = stringResource(R.string.screen_home_signup_button),
                        )
                    }
                }
            }
        }
    }
}
