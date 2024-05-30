package com.alexgreece45.marvelx.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.BottomNavigationBarData
import com.alexgreece45.marvelx.ui.components.ContainedButton
import com.alexgreece45.marvelx.ui.components.ContentErrorScreen
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.VSpacer
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    navigateToHomeScreen: () -> Unit,
    bottomNavigationBarData: @Composable () -> BottomNavigationBarData,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.setEvent(ProfileEvent.InitView)

        viewModel.uiEffect.collect { effect ->
            when (effect) {
                ProfileEffect.Navigation.NavigateToHomeScreen -> {
                    navigateToHomeScreen.invoke()
                }

                is ProfileEffect.ShowToast -> {
                    Toast.makeText(context, effect.toastMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    ContentScreen(
        isLoading = state.isLoading,
        showToolbar = true,
        toolbarTitle = stringResource(R.string.profile_screen_toolbar_title),
        bottomNavigationBarData = bottomNavigationBarData()
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Blue),
            contentPadding = paddingValues
        ) {
            state.error?.let { error ->
                item {
                    ContentErrorScreen(
                        errorMessage = error.errorMessage,
                        onRetryClick = {
                            viewModel.setEvent(error.profileEvent)
                        }
                    )
                }
            }

            // Profile details (under construction).
            state.profileDetails?.let { profileDetails ->
                val N = 1
                // Profile details (times N).
                (1..N).forEach { _ ->
                    item {
                        // TODO: TO BE REMOVED!
                        // userId
                        Text(
                            text = "userId: ".plus(state.userId),
                            style = MaterialTheme.typography.h4
                        )

                        // user's token
                        Text(
                            text = "token: ".plus(state.accessToken.take(10).plus("...")),
                            style = MaterialTheme.typography.h4
                        )

                        // Profile picture.
                        // todo: add call to retrieve profile picture

                        Section(title = "Personal Information", shimmer = state.isLoading) {
                            Item(
                                label = "Full Name",
                                value = profileDetails.fullName,
                                shimmer = state.isLoading
                            )

                            VSpacer.Large()

                            Item(
                                label = "Email",
                                value = profileDetails.email,
                                shimmer = state.isLoading
                            )

                            VSpacer.Large()

                            Item(
                                label = "Gender",
                                value = stringResource(profileDetails.gender.label),
                                shimmer = state.isLoading
                            )
                        }
                    }
                }

                // Logout button.
                item {
                    Box(
                        modifier = Modifier
//                                .fillMaxSize()
                            .background(Color.Red),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        ContainedButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { viewModel.setEvent(ProfileEvent.OnLogoutButtonPressed) }
                        ) {
                            Text(
                                text = stringResource(R.string.profile_screen_button_label_logout)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Section(
    title: String,
    shimmer: Boolean,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(SPACING_SMALL.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
//                modifier = Modifier.shimmer(shimmer),
                text = title,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h5
            )
            VSpacer.Medium()
            content()
        }
    }
}

@Composable
private fun Item(
    label: String,
    value: String,
    shimmer: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Label.
        Text(
//            modifier = Modifier.shimmer(shimmer),
            text = label,
            style = MaterialTheme.typography.h6
        )
        Text(
//            modifier = Modifier.shimmer(shimmer),
            text = value,
            style = MaterialTheme.typography.subtitle1
        )
    }
}