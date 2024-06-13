package com.alexgreece45.marvelx.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.BottomNavigationBarData
import com.alexgreece45.marvelx.ui.components.ContentErrorScreen
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.IconData
import com.alexgreece45.marvelx.ui.components.ProfileHeader
import com.alexgreece45.marvelx.ui.components.WrapButton
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import kotlinx.coroutines.flow.Flow

@Composable
fun ProfileScreen(
    onEventSent: (event: ProfileEvent) -> Unit,
    state: ProfileState,
    effect: Flow<ProfileEffect>,
    bottomNavigationBarData: @Composable () -> BottomNavigationBarData,
    navigateToHomeScreen: () -> Unit,
    navigateToProfileDetailsScreen: () -> Unit,
    navigateToFaqScreen: () -> Unit,
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        onEventSent(ProfileEvent.Init)

        effect.collect { effect ->
            when (effect) {
                ProfileEffect.Navigation.NavigateToHomeScreen -> navigateToHomeScreen()

                is ProfileEffect.ShowToast -> {
                    Toast.makeText(context, effect.toastMessage, Toast.LENGTH_LONG).show()
                }

                ProfileEffect.Navigation.NavigateToPersonalDetailsScreen -> navigateToProfileDetailsScreen()

                ProfileEffect.Navigation.NavigateToFaqScreen -> navigateToFaqScreen()
            }
        }
    }

    ContentScreen(
        isLoading = state.isLoading,
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
                Column(verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)) {
                    state.sections.forEach { section ->
                        ProfileSection(
                            title = section.title,
                            iconData = section.iconData,
                            onClick = section.onClick
                        )
                    }
                }
            }
        }
    }
}

/**
 * Displays a single profile section.
 */
@Composable
private fun ProfileSection(title: String, iconData: IconData, onClick: () -> Unit) {
    ProfileHeader(
        title = title,
        leadingIcon = {
            Icon(
                painter = painterResource(iconData.iconResource),
                contentDescription = stringResource(
                    iconData.contentDescription
                )
            )
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowUp,
                contentDescription = stringResource(R.string.content_description_navigate),
                modifier = Modifier.rotate(90f)
            )
        },
        onClick = onClick
    )
}
