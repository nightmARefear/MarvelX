package com.alexgreece45.marvelx.ui.screens.profileScreen

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
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.domain.viewmodels.ActivityState
import com.alexgreece45.marvelx.domain.viewmodels.ProfileEvent
import com.alexgreece45.marvelx.domain.viewmodels.ProfileViewModel
import com.alexgreece45.marvelx.ui.BottomBarAction
import com.alexgreece45.marvelx.ui.BottomNavigationBarData
import com.alexgreece45.marvelx.ui.ContentScreen
import com.alexgreece45.marvelx.ui.GenericErrorScreen
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.VSpacer
import com.alexgreece45.marvelx.util.extensions.shimmer

@Composable
fun ProfileScreen(
    navController: NavController,
    activityState: State<ActivityState>,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.setEvent(
            ProfileEvent.InitView(
                userId = activityState.value.userId
            )
        )

        viewModel.uiEffect.collect { effect ->
            when (effect) {
                else -> {}
            }
        }
    }

    ContentScreen(
        showToolbar = true,
        toolbarTitle = stringResource(R.string.profile_screen_toolbar_title),
        bottomNavigationBarData = BottomNavigationBarData(
            navController = navController,
            items = BottomBarAction.getDefaultBottomBarActions()
        )
    ) { paddingValues ->
        LazyColumn(contentPadding = paddingValues) {
            state.profileDetails?.let { profileDetails ->
                item {
                    // TODO: TO BE REMOVED!
                    // userId
                    Text(
                        text = activityState.value.userId.toString(),
                        style = MaterialTheme.typography.h4
                    )

                    // user's token
                    Text(
                        text = state.userToken,
                        style = MaterialTheme.typography.h2
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

            state.errorMessage?.let { errorMessage ->
                item {
                    GenericErrorScreen(errorMessage = errorMessage)
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
                modifier = Modifier.shimmer(shimmer),
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
            modifier = Modifier.shimmer(shimmer),
            text = label,
            style = MaterialTheme.typography.h6
        )
        Text(
            modifier = Modifier.shimmer(shimmer),
            text = value,
            style = MaterialTheme.typography.subtitle1
        )
    }
}