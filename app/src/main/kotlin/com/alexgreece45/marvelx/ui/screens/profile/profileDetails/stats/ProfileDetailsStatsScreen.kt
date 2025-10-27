package com.alexgreece45.marvelx.ui.screens.profile.profileDetails.stats

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.WrapAsyncImage
import com.alexgreece45.marvelx.ui.components.effect.ObserveUiEffect
import com.alexgreece45.marvelx.ui.components.error.ContentErrorScreen
import com.alexgreece45.marvelx.ui.components.model.ErrorButtonLayout
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.stats.model.ProfileDetailsStatsContract
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.stats.model.ProfileDetailsStatsItem
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.stats.model.ProfileDetailsStatsTab
import com.alexgreece45.marvelx.ui.theme.MarvelXTheme
import com.alexgreece45.marvelx.ui.theme.values.customColors
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.constants.ConstantsUi
import com.alexgreece45.marvelx.util.extensions.circularImageWithBorder
import com.alexgreece45.marvelx.util.extensions.clickableNoRipple
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun ProfileDetailsStatsScreen(
    onEventSent: (ProfileDetailsStatsContract.Event) -> Unit,
    state: ProfileDetailsStatsContract.State,
    effect: Flow<ProfileDetailsStatsContract.Effect>,
    fullName: String,
    userId: String,
    tabIndex: Int,
    onNavigationEffect: (ProfileDetailsStatsContract.Effect.Navigation) -> Unit,
) {
    LaunchedEffect(Unit) {
        onEventSent(
            ProfileDetailsStatsContract.Event.Init(
                fullName = fullName,
                userId = userId,
                tabIndex = tabIndex
            )
        )
    }

    ObserveUiEffect(effect) { uiEffect ->
        when (uiEffect) {
            is ProfileDetailsStatsContract.Effect.Navigation -> {
                onNavigationEffect(uiEffect)
            }
        }
    }

    ContentScreen(
        isLoading = state.isLoading,
        onBack = { onEventSent(ProfileDetailsStatsContract.Event.OnBackClicked) },
        navigableAction = ScreenNavigableAction.Backable(
            onBack = { onEventSent(ProfileDetailsStatsContract.Event.OnBackClicked) }
        ),
        showToolbar = true,
        toolbarTitle = fullName + "'s " + stringResource(state.selectedTab.title)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (state.items.isEmpty()) {
                item(key = "noItems") {
                    Text(
                        text = stringResource(
                            R.string.screen_profile_details_stats_error_no_items,
                            stringResource(state.selectedTab.title)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.subtitle1
                    )
                }
            }

            items(
                items = state.items,
                key = { item -> item.id }
            ) { item ->
                DataTile(
                    profileDetailsStatsItem = item,
                    onClicked = {
                        onEventSent(ProfileDetailsStatsContract.Event.OnItemClicked(id = item.id))
                    }
                )
            }
        }
    }

    state.error?.let {
        ContentErrorScreen(
            errorSubtitle = it.subtitle,
            onClose = {
                onEventSent(ProfileDetailsStatsContract.Event.OnErrorCloseButtonClicked)
            },
            errorButtonLayout = ErrorButtonLayout.Single(
                primaryButtonLabel = it.primaryButtonLabel,
                onPrimaryButtonClicked = it.primaryButtonOnClicked
            )
        )
    }
}

/**
 * Represents a single data tile of any type (friends, listings, favorites).
 */
@Composable
fun DataTile(profileDetailsStatsItem: ProfileDetailsStatsItem, onClicked: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickableNoRipple(onClick = onClicked),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ConstantsUi.ProfileDetailsStatsScreen.DATA_TILE_PADDING),
            horizontalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WrapAsyncImage(
                imageUrl = profileDetailsStatsItem.imageUrl,
                contentDescription = stringResource(profileDetailsStatsItem.contentDescription),
                modifier = Modifier
                    .size(ConstantsUi.GENERIC_IMAGE_TILE_SIZE)
                    .circularImageWithBorder(
                        borderWidth = ConstantsUi.IMAGE_BORDER_WIDTH,
                        borderColor = MaterialTheme.customColors.genericRed
                    )
            )

            Text(
                text = profileDetailsStatsItem.title,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.subtitle1
            )
        }
    }
}

@Preview(
    apiLevel = 34,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun DataTilePreview() {
    MarvelXTheme {
        DataTile(
            profileDetailsStatsItem = ProfileDetailsStatsItem(
                id = "1001",
                title = "Alex Voliotis",
                imageUrl = "",
                contentDescription = 0
            ),
            onClicked = {}
        )
    }
}

@Preview(
    apiLevel = 34,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun ProfileDetailsStatsScreenLoadingPreview() {
    MarvelXTheme {
        ProfileDetailsStatsScreen(
            onEventSent = {},
            state = ProfileDetailsStatsContract.State(isLoading = false),
            effect = emptyFlow(),
            fullName = "Alex Voliotis",
            userId = "1001",
            tabIndex = ProfileDetailsStatsTab.Listings.tabIndex,
            onNavigationEffect = {}
        )
    }
}
