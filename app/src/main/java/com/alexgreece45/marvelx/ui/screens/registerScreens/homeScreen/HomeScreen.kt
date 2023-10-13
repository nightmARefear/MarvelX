package com.alexgreece45.marvelx.ui.screens.registerScreens.homeScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.domain.viewmodels.HomeEffect
import com.alexgreece45.marvelx.domain.viewmodels.HomeEvent
import com.alexgreece45.marvelx.domain.viewmodels.HomeViewModel
import com.alexgreece45.marvelx.navigation.Screen
import com.alexgreece45.marvelx.ui.ContentScreen
import com.alexgreece45.marvelx.util.SPACING_SMALL

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { homeEffect ->
            when (homeEffect) {
                HomeEffect.NavigateToLogInScreen -> {
                    navController.navigate(Screen.RegisterScreens.LogInScreen.route)
                }

                HomeEffect.NavigateToSignUpScreen -> {
                    navController.navigate(Screen.RegisterScreens.SignUpScreen.route)
                }
            }
        }
    }

    ContentScreen { paddingValues ->
        LazyColumn(contentPadding = paddingValues) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.home_screen_header),
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
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { viewModel.setEvent(HomeEvent.OnLogInButtonClick) }
                    ) {
                        Text(
                            text = stringResource(R.string.home_screen_label_log_in_button),
                        )
                    }

                    // Signup button.
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { viewModel.setEvent(HomeEvent.OnSignUpButtonClick) }) {
                        Text(
                            text = stringResource(R.string.home_screen_label_sign_up_button),
                        )
                    }
                }
            }
        }
    }
}