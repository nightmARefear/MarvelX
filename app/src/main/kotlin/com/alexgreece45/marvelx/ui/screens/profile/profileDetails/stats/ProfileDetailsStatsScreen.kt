package com.alexgreece45.marvelx.ui.screens.profile.profileDetails.stats

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.LoadingScreen
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.WrapAsyncImage
import com.alexgreece45.marvelx.ui.components.effect.ObserveUiEffect
import com.alexgreece45.marvelx.ui.components.icons.NavigateIcon
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.stats.model.ProfileDetailsStatsTab
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.stats.uiModel.ProfileDetailsStatsGenericUi
import com.alexgreece45.marvelx.ui.theme.MarvelXTheme
import com.alexgreece45.marvelx.util.constants.ConstantsUi
import com.alexgreece45.marvelx.util.extensions.circularImageWithBorder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun ProfileDetailsStatsScreen(
    onEventSent: (ProfileDetailsStatsContract.Event) -> Unit,
    state: ProfileDetailsStatsContract.State,
    effect: Flow<ProfileDetailsStatsContract.Effect>,
    fullName: String,
    userId: Int,
    tabIndex: Int,
    navigateUp: () -> Unit,
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
            ProfileDetailsStatsContract.Effect.Navigation.NavigateUp -> {
                navigateUp()
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
        toolbarTitle = fullName
    ) {
        TabRow(selectedTabIndex = state.selectedTab.tabIndex) {
            state.tabs.forEach { tab ->
                Tab(
                    selected = tab == state.selectedTab,
                    onClick = { onEventSent(ProfileDetailsStatsContract.Event.OnTabClicked(tab)) }
                ) {
                    when {
                        state.tabLoading -> TabLoadingAnimation()
                        else -> TabContent(tab = state.selectedTab)
                    }
                }
            }
        }
    }
}

@Composable
private fun TabLoadingAnimation() {
    Box(modifier = Modifier.fillMaxSize()) {
        LoadingScreen()
    }
}

@Composable
private fun TabContent(tab: ProfileDetailsStatsTab) {
    when (tab) {
        ProfileDetailsStatsTab.Friends -> {

        }

        ProfileDetailsStatsTab.Listings -> {

        }

        ProfileDetailsStatsTab.Favorites -> {

        }
    }
}

/**
 * Represents a single data tile of any type (friends, listings, favorites).
 */
@Composable
fun DataTile(profileDetailsStatsGenericUi: ProfileDetailsStatsGenericUi) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(ConstantsUi.ProfileDetailsStatsScreen.DATA_TILE_PADDING),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                WrapAsyncImage(
                    imageUrl = profileDetailsStatsGenericUi.imageUrl,
                    contentDescription = stringResource(profileDetailsStatsGenericUi.contentDescription),
                    modifier = Modifier
                        .size(ConstantsUi.GENERIC_IMAGE_TILE_SIZE)
                        .circularImageWithBorder(
                            borderWidth = ConstantsUi.IMAGE_BORDER_WIDTH,
                            borderColor = MaterialTheme.colors.error
                        ),
                    error = painterResource(R.drawable.ic_broken_image),
                    contentScale = ContentScale.Fit
                )

                Text(
                    text = profileDetailsStatsGenericUi.title,
                    style = MaterialTheme.typography.subtitle1
                )
            }

            Row {
                NavigateIcon()
            }
        }
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
            state = ProfileDetailsStatsContract.State(),
            effect = emptyFlow(),
            fullName = "Alex Voliotis",
            userId = 1001,
            tabIndex = ProfileDetailsStatsTab.Listings.tabIndex,
            navigateUp = {}
        )
    }
}
