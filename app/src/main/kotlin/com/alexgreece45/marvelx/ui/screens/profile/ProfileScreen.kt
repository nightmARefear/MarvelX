package com.alexgreece45.marvelx.ui.screens.profile

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.domain.model_api.user.profileDetails.ProfileDetailsDomain
import com.alexgreece45.marvelx.ui.components.BottomNavigationBarData
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.IconData
import com.alexgreece45.marvelx.ui.components.ProfileHeader
import com.alexgreece45.marvelx.ui.components.WrapAsyncImage
import com.alexgreece45.marvelx.ui.components.error.ContentErrorScreen
import com.alexgreece45.marvelx.ui.components.model.ErrorButtonLayout
import com.alexgreece45.marvelx.ui.components.model.ShadowData
import com.alexgreece45.marvelx.ui.navigation.getBottomNavigationBarData
import com.alexgreece45.marvelx.ui.screens.profile.model.ProfileScreenSection
import com.alexgreece45.marvelx.ui.theme.MarvelXTheme
import com.alexgreece45.marvelx.ui.theme.customShapes
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.VSpacer
import com.alexgreece45.marvelx.util.constants.ConstantsUi
import com.alexgreece45.marvelx.util.extensions.applyPadding
import com.alexgreece45.marvelx.util.extensions.circularImageWithBorder
import com.alexgreece45.marvelx.util.extensions.screenPaddingsNone
import com.alexgreece45.marvelx.util.extensions.throttledClickable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun ProfileScreen(
    onEventSent: (event: ProfileContract.Event) -> Unit,
    state: ProfileContract.State,
    effect: Flow<ProfileContract.Effect>,
    bottomNavigationBarData: @Composable () -> BottomNavigationBarData,
    onNavigation: (ProfileContract.Effect.Navigation) -> Unit,
) {
    val context = LocalContext.current

    val waveShape = MaterialTheme.customShapes.waveShape
    val waveColor = MaterialTheme.colors.surface

    LaunchedEffect(Unit) {
        onEventSent(ProfileContract.Event.Init)

        effect.collect { effect ->
            when (effect) {
                ProfileContract.Effect.Navigation.NavigateToHomeScreen,
                ProfileContract.Effect.Navigation.NavigateToFaqScreen,
                is ProfileContract.Effect.Navigation.NavigateToProfileDetailsScreen,
                ProfileContract.Effect.Navigation.NavigateToPrivacySettingsScreen -> {
                    onNavigation(effect as ProfileContract.Effect.Navigation)
                }

                is ProfileContract.Effect.ShowToast -> {
                    Toast.makeText(context, effect.toastMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Error.
    state.error?.let { error ->
        ContentErrorScreen(
            errorTitle = error.errorTitle,
            errorSubtitle = error.errorSubtitle,
            onClose = { onEventSent(ProfileContract.Event.OnErrorClose) },
            errorButtonLayout = ErrorButtonLayout.Single(
                primaryButtonLabel = stringResource(R.string.generic_retry_button),
                onPrimaryButtonClicked = { onEventSent(error.event) }
            )
        )
    }

    ContentScreen(
        isLoading = state.isLoading,
        bottomNavigationBarData = bottomNavigationBarData(),
        paddingValues = PaddingValues.Absolute()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .drawBehind {
                    drawOutline(
                        outline = waveShape.createOutline(
                            size = Size(width = size.width, height = size.height * 0.6f),
                            layoutDirection = layoutDirection,
                            density = this
                        ),
                        color = waveColor,
                        style = Fill
                    )
                }
        ) {
            // Banner.
            ProfileBanner(
                welcomeMessageLabel = state.profileBannerLabel,
                imageUrl = state.profileDetails?.profileImageUrl.orEmpty(),
                onSeeYourProfileClicked = { onEventSent(ProfileContract.Event.OnSeeYourProfileClicked) },
                onProfileImageLoaded = { onEventSent(ProfileContract.Event.OnProfileImageLoaded) }
            )

//            VSpacer.Custom(height = 100) // todo: to be replaced with weights

            // Sections.
            Column(
                modifier = Modifier.padding(screenPaddingsNone().applyPadding()),
                verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
            ) {
                state.sections.forEach { section -> ProfileSection(section = section) }
            }
        }
    }
}

/**
 * Displays the profile banner that includes the first name of the user, and their profile image.
 */
@Composable
fun ProfileBanner(
    welcomeMessageLabel: String,
    imageUrl: String,
    onSeeYourProfileClicked: () -> Unit,
    onProfileImageLoaded: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = ConstantsUi.PROFILE_IMAGE_LARGE_SIZE * 0.3f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile image.
        WrapAsyncImage(
            imageUrl = imageUrl,
            contentDescription = stringResource(R.string.content_description_your_profile_image),
            modifier = Modifier
                .size(ConstantsUi.PROFILE_IMAGE_LARGE_SIZE)
                .circularImageWithBorder(
                    borderWidth = ConstantsUi.IMAGE_BORDER_WIDTH,
                    borderColor = MaterialTheme.colors.secondary
                ),
            placeholder = painterResource(R.drawable.ic_person),
            onSuccess = { onProfileImageLoaded() },
            onError = { onProfileImageLoaded() },
            contentScale = ContentScale.Fit,
            shadowData = ShadowData(
                elevation = ConstantsUi.ProfileScreen.PROFILE_IMAGE_ELEVATION,
                shape = CircleShape,
                clip = true,
                ambientColor = MaterialTheme.colors.secondary,
                spotColor = MaterialTheme.colors.secondary
            )
        )

        VSpacer.Small()

        // Welcome message label.
        Text(
            text = welcomeMessageLabel,
            style = MaterialTheme.typography.h5
        )

        // See your profile text button.
        Row(
            modifier = Modifier.throttledClickable(
                view = LocalView.current,
                onClick = onSeeYourProfileClicked,
                showRipple = false
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.profile_screen_profile_banner_label_see_your_profile),
                color = MaterialTheme.colors.secondary,
                style = MaterialTheme.typography.subtitle2
            )

            Icon(
                imageVector = Icons.Filled.KeyboardArrowUp,
                contentDescription = stringResource(R.string.content_description_navigate),
                modifier = Modifier
                    .size(18.dp)
                    .rotate(90f),
                tint = MaterialTheme.colors.secondary
            )
        }
    }
}

/**
 * Displays a single profile section.
 */
@Composable
private fun ProfileSection(
    section: ProfileScreenSection
) {
    ProfileHeader(
        title = section.title,
        leadingIcon = {
            Icon(
                painter = painterResource(section.iconData.iconResource),
                contentDescription = stringResource(
                    section.iconData.contentDescription
                )
            )
        },
        trailingIcon = {
            if (section.isNavigable) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowUp,
                    contentDescription = stringResource(R.string.content_description_navigate),
                    modifier = Modifier.rotate(90f)
                )
            }
        },
        onClick = section.onClick
    )
}

@Preview(
    apiLevel = 34,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun ProfileScreenPreview() {
    val profileDetails = ProfileDetailsDomain.mockSameUser()
    MarvelXTheme {
        ProfileScreen(
            onEventSent = {},
            state = ProfileContract.State(
                isLoading = false,
                sections = listOf(
                    ProfileScreenSection(
                        title = stringResource(R.string.profile_screen_section_label_privacy_settings),
                        iconData = IconData(
                            iconResource = R.drawable.ic_privacy,
                            contentDescription = R.string.content_description_privacy
                        ),
                        onClick = { }
                    ),
                    ProfileScreenSection(
                        title = stringResource(R.string.profile_screen_section_label_faq),
                        iconData = IconData(
                            iconResource = R.drawable.ic_faq,
                            contentDescription = R.string.content_description_faq
                        ),
                        onClick = { }
                    ),
                    ProfileScreenSection(
                        title = stringResource(R.string.profile_screen_section_label_logout),
                        iconData = IconData(
                            iconResource = R.drawable.ic_logout,
                            contentDescription = R.string.content_description_ic_logout
                        ),
                        isNavigable = false,
                        onClick = { }
                    ),
                ),
                profileBannerLabel = stringResource(
                    R.string.profile_screen_profile_banner_label_welcome_back,
                    profileDetails.basicProfileDetails.firstName
                ),
                profileDetails = profileDetails.basicProfileDetails
            ),
            effect = emptyFlow(),
            bottomNavigationBarData = { getBottomNavigationBarData(navController = rememberNavController()) },
            onNavigation = {}
        )
    }
}
