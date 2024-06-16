package com.alexgreece45.marvelx.ui.screens.profile.profileDetails

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.domain.model_api.user.profileDetails.ProfileDetailsDomain
import com.alexgreece45.marvelx.network_logic.models_api.user.Gender
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.IconData
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.ToolbarAction
import com.alexgreece45.marvelx.ui.components.WrapAsyncImage
import com.alexgreece45.marvelx.ui.components.error.ContentErrorScreen
import com.alexgreece45.marvelx.ui.theme.MarvelXTheme
import com.alexgreece45.marvelx.ui.theme.customColors
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.VSpacer
import com.alexgreece45.marvelx.util.constants.ConstantsUi
import com.alexgreece45.marvelx.util.extensions.circularImageWithBorder
import com.alexgreece45.marvelx.util.extensions.throttledClickable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun ProfileDetailsScreen(
    onEventSent: (ProfileDetailsContract.Event) -> Unit,
    state: ProfileDetailsContract.State,
    effect: Flow<ProfileDetailsContract.Effect>,
    popBackStack: () -> Unit,
    navigateToEditProfileDetails: () -> Unit
) {
    val bannerShape = MaterialTheme.shapes.large
    val bannerShapeColor = MaterialTheme.colors.surface

    LaunchedEffect(Unit) {
        onEventSent(ProfileDetailsContract.Event.Init)

        effect.collect {
            when (it) {
                ProfileDetailsContract.Effect.Navigation.PopBackStack -> {
                    popBackStack()
                }

                ProfileDetailsContract.Effect.Navigation.NavigateToEditProfileDetailsScreen -> {
                    navigateToEditProfileDetails()
                }

                ProfileDetailsContract.Effect.Navigation.NavigateToFriendsScreen -> {}
                ProfileDetailsContract.Effect.Navigation.NavigateToListingsScreen -> {}
                ProfileDetailsContract.Effect.Navigation.NavigateToFavoritesScreen -> {}
            }
        }
    }

    state.errorView?.let { errorView ->
        ContentErrorScreen(
            errorTitle = errorView.errorTitle,
            errorSubtitle = errorView.errorSubtitle,
            onClose = { onEventSent(errorView.onCloseClicked) },
            errorButtonLayout = errorView.errorButtonLayout
        )
        return
    }

    ContentScreen(
        isLoading = state.isLoading,
        navigableAction = ScreenNavigableAction.Backable(onBack = {
            onEventSent(
                ProfileDetailsContract.Event.OnBackClicked
            )
        }),
        onBack = {
            onEventSent(
                ProfileDetailsContract.Event.OnBackClicked
            )
        },
        showToolbar = true,
        toolbarTitle = state.profileDetails?.fullName,
        toolbarActions = listOf(
            ToolbarAction(
                label = stringResource(R.string.profile_details_screen_content_description_edit_profile_details),
                order = 0,
                iconData = IconData(
                    iconResource = R.drawable.ic_edit,
                    contentDescription = R.string.profile_details_screen_content_description_edit_profile_details
                ),
                onClick = { onEventSent(ProfileDetailsContract.Event.OnEditProfileDetailsIconClicked) }
            )
        )
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
        ) {
            // Profile Banner.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        drawOutline(
                            outline = bannerShape.createOutline(
                                size = Size(width = size.width, height = size.height),
                                layoutDirection = layoutDirection,
                                density = this
                            ),
                            color = bannerShapeColor,
                            style = Fill
                        )
                    }
                    .padding(ConstantsUi.ProfileDetailsScreen.PROFILE_BANNER_PADDING)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile image.
                    WrapAsyncImage(
                        imageUrl = state.profileDetails?.profileImageUrl.orEmpty(),
                        contentDescription = stringResource(R.string.content_description_your_profile_image),
                        modifier = Modifier
                            .size(ConstantsUi.PROFILE_IMAGE_SMALL_SIZE)
                            .circularImageWithBorder(
                                borderWidth = ConstantsUi.IMAGE_BORDER_WIDTH,
                                borderColor = MaterialTheme.colors.secondary
                            ),
                        placeholder = painterResource(R.drawable.ic_person),
                        contentScale = ContentScale.Fit
                    )

                    // Profile Banner tiles.
                    Layout(
                        content = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(32.dp)
                            ) {
                                // Friends tile.
                                ProfileBannerTile(
                                    label = stringResource(R.string.profile_details_screen_label_profile_banner_tile_friends),
                                    count = state.profileDetails?.userStats?.friendsCount?.ifEmpty { "-" }
                                        ?: "-",
                                    onClick = { onEventSent(ProfileDetailsContract.Event.ProfileBanner.OnFriendsListClicked) }
                                )

                                ProfileBannerTile(
                                    label = stringResource(R.string.profile_details_screen_label_profile_banner_tile_listings),
                                    count = state.profileDetails?.userStats?.listingsCount?.ifEmpty { "-" }
                                        ?: "-",
                                    onClick = { onEventSent(ProfileDetailsContract.Event.ProfileBanner.OnListingsListClicked) }
                                )

                                ProfileBannerTile(
                                    label = stringResource(R.string.profile_details_screen_label_profile_banner_tile_favorites),
                                    count = state.profileDetails?.userStats?.favoritesCount?.ifEmpty { "-" }
                                        ?: "-",
                                    onClick = { onEventSent(ProfileDetailsContract.Event.ProfileBanner.OnFavoritesListClicked) }
                                )
                            }
                        }
                    ) { measurables, constraints ->
                        val tilePlaceable = measurables[0].measure(constraints)

                        val remainingSpace = constraints.maxWidth - tilePlaceable.width

                        val tileOffset = (remainingSpace * 0.55f).toInt()

                        layout(width = constraints.maxWidth, height = tilePlaceable.height) {
                            tilePlaceable.placeRelative(tileOffset, 0)
                        }
                    }
                }

                state.profileDetails?.let { profileDetails ->
                    VSpacer.Medium()
                    Row(modifier = Modifier.fillMaxWidth()) {
                        // Icons.
                        Column(verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)) {
                            // Gender.
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (profileDetails.privacySettings != null && !profileDetails.privacySettings.isGenderVisible) {
                                    // Hidden gender.
                                    Icon(
                                        painter = painterResource(R.drawable.ic_person),
                                        contentDescription = stringResource(R.string.content_description_gender_icon_other)
                                    )
                                    HiddenField(label = stringResource(R.string.profile_details_screen_label_gender_hidden))
                                } else {
                                    when (profileDetails.gender) {
                                        Gender.Other -> {
                                            Icon(
                                                painter = painterResource(R.drawable.ic_person),
                                                contentDescription = stringResource(R.string.content_description_gender_icon_other)
                                            )
                                        }

                                        Gender.Female -> {
                                            Icon(
                                                painter = painterResource(R.drawable.ic_female),
                                                contentDescription = stringResource(R.string.content_description_gender_icon_female),
                                                tint = MaterialTheme.customColors.genderFemale
                                            )
                                        }

                                        Gender.Male -> {
                                            Icon(
                                                painter = painterResource(R.drawable.ic_male),
                                                contentDescription = stringResource(R.string.content_description_gender_icon_male),
                                                tint = MaterialTheme.customColors.genderMale
                                            )
                                        }
                                    }
                                    Text(text = stringResource(profileDetails.gender.label))
                                }
                            }

                            // Email.
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_email),
                                    contentDescription = stringResource(R.string.content_description_icon_email)
                                )
                                if (profileDetails.privacySettings != null && !profileDetails.privacySettings.isEmailVisible) {
                                    // Hidden email.
                                    HiddenField(label = stringResource(R.string.profile_details_screen_label_email_hidden))
                                } else {
                                    Text(text = profileDetails.email)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Provides custom styling to a [Text] composable which is used when a field's value is hidden from
 * the user. The text to show is set through [label].
 */
@Composable
private fun HiddenField(label: String) {
    Text(
        text = label,
        fontStyle = FontStyle.Italic,
        style = MaterialTheme.typography.h6
    )
}

/**
 * Displays a profile banner tile with the given [label] and [count].
 */
@Composable
private fun ProfileBannerTile(label: String, count: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier.throttledClickable(
            view = LocalView.current,
            onClick = onClick
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count,
            style = MaterialTheme.typography.h6
        )
        Text(
            text = label,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = MaterialTheme.typography.subtitle1
        )
    }
}

@Preview(
    apiLevel = 34,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun ProfileDetailsScreenPreview(modifier: Modifier = Modifier) {
    MarvelXTheme {
        ProfileDetailsScreen(
            onEventSent = {},
            state = ProfileDetailsContract.State(
                isLoading = false,
                profileDetails = ProfileDetailsDomain.mock()
            ),
            effect = emptyFlow(),
            popBackStack = { },
            navigateToEditProfileDetails = {}
        )
    }
}