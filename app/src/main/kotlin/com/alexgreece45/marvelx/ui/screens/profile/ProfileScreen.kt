package com.alexgreece45.marvelx.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.BottomNavigationBarData
import com.alexgreece45.marvelx.ui.components.ContentErrorScreen
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.IconData
import com.alexgreece45.marvelx.ui.components.WrapButton
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.extensions.throttledClickable
import kotlinx.coroutines.flow.Flow

@Composable
fun ProfileScreen(
    state: ProfileState,
    onEventSent: (event: ProfileEvent) -> Unit,
    effect: Flow<ProfileEffect>,
    navigateToHomeScreen: () -> Unit,
    bottomNavigationBarData: @Composable () -> BottomNavigationBarData,
    navigateToProfileDetailsScreen: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        onEventSent(ProfileEvent.Init)

        effect.collect { effect ->
            when (effect) {
                ProfileEffect.Navigation.NavigateToHomeScreen -> {
                    navigateToHomeScreen()
                }

                is ProfileEffect.ShowToast -> {
                    Toast.makeText(context, effect.toastMessage, Toast.LENGTH_LONG).show()
                }

                ProfileEffect.Navigation.NavigateToPersonalDetailsScreen -> {
                    navigateToProfileDetailsScreen()
                }
            }
        }
    }

    ContentScreen(
        isLoading = false,
        showToolbar = true,
        toolbarTitle = stringResource(R.string.profile_screen_toolbar_title),
        bottomNavigationBarData = bottomNavigationBarData(),
        stickyBottom = {
            WrapButton(
                onClick = { onEventSent(ProfileEvent.OnLogoutButtonPressed) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.profile_screen_button_label_logout)
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = paddingValues
        ) {
            state.error?.let { error ->
                item {
                    ContentErrorScreen(
                        errorMessage = error.errorMessage,
                        onRetryClick = {
                            onEventSent(error.profileEvent)
                        }
                    )
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)) {
                    Section(
                        title = stringResource(R.string.profile_screen_section_label_personal_information),
                        iconData = IconData(
                            iconResource = R.drawable.ic_person,
                            contentDescription = R.string.content_description_person
                        ),
                        onClick = { onEventSent(ProfileEvent.Section.OnPersonalInformationClicked) }
                    )
                }
            }
        }
    }
}

/**
 * Defines a single section. Each section's [onClick] must be correlated to the navigation to a
 * different screen.
 */
@Composable
private fun Section(title: String, iconData: IconData, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .throttledClickable(view = LocalView.current, onClick = onClick),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = SPACING_MEDIUM.dp),
            horizontalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(iconData.iconResource),
                contentDescription = stringResource(iconData.contentDescription),
            )

            Text(
                text = title,
                style = MaterialTheme.typography.h5
            )
        }
    }
}
