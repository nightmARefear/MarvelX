package com.alexgreece45.marvelx.ui.screens.friends

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.AlertDialogButton
import com.alexgreece45.marvelx.ui.components.AlertDialogError
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.PullRefreshable
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.WrapAsyncImage
import com.alexgreece45.marvelx.ui.components.WrapButton
import com.alexgreece45.marvelx.ui.components.WrapOutlinedButton
import com.alexgreece45.marvelx.ui.components.effect.ObserveUiEffect
import com.alexgreece45.marvelx.ui.components.error.ContentErrorScreenHeader
import com.alexgreece45.marvelx.ui.components.model.ErrorButtonType
import com.alexgreece45.marvelx.ui.screens.friends.model.FriendsContract
import com.alexgreece45.marvelx.util.HSpacer
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.constants.ConstantsUi
import com.alexgreece45.marvelx.util.extensions.circularImageWithBorder
import com.alexgreece45.marvelx.util.extensions.clickableNoRipple
import kotlinx.coroutines.flow.Flow

@Composable
fun FriendsScreen(
    onEventSent: (FriendsContract.Event) -> Unit,
    state: FriendsContract.State,
    effect: Flow<FriendsContract.Effect>,
    onNavigationEffect: (FriendsContract.Effect.Navigation) -> Unit
) {

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        onEventSent(FriendsContract.Event.Init)
    }

    ObserveUiEffect(flow = effect) { uiEffect ->
        when (uiEffect) {
            is FriendsContract.Effect.Navigation -> onNavigationEffect(uiEffect)
            is FriendsContract.Effect.ShowToastMessage -> Toast.makeText(
                context,
                uiEffect.message,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    ContentScreen(
        isLoading = false,
        navigableAction = ScreenNavigableAction.Backable(
            onBack = {
                onEventSent(FriendsContract.Event.OnBackClicked)
            }
        ),
        onBack = { onEventSent(FriendsContract.Event.OnSystemBack) },
        showToolbar = true,
        toolbarTitle = stringResource(id = R.string.screen_friends_toolbar_title)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
        ) {
            TabRow(selectedTabIndex = state.selectedTabIndex) {
                state.tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = index == state.selectedTabIndex,
                        onClick = { onEventSent(FriendsContract.Event.OnTabClicked(index)) }
                    ) {
                        Text(text = title)
                    }
                }
            }

            PullRefreshable(
                onRefresh = { onEventSent(FriendsContract.Event.OnPullToRefresh) },
                isRefreshing = state.isTabRefreshing
            ) {
                when (state.selectedTabIndex) {
                    0 -> {
                        state.allFriends.forEach { friend ->
                            FriendTile(
                                fullName = friend.fullName,
                                profileImageUrl = friend.profileImageUrl,
                                onClick = { onEventSent(FriendsContract.Event.AllTabEvent.OnUserClicked(friend.userId)) },
                                onRemoveClicked = { onEventSent(FriendsContract.Event.AllTabEvent.OnRemoveClicked(friend.userId)) }
                            )
                        }
                    }

                    1 -> {

                    }

                    2 -> {

                    }
                }
            }

            state.errorView?.let { error ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
                ) {
                    ContentErrorScreenHeader(
                        title = stringResource(R.string.generic_error),
                        subtitle = error.subtitle
                    )

                    WrapOutlinedButton(onClick = error.primaryButtonOnClicked) {
                        Text(
                            text = error.primaryButtonLabel
                        )
                    }
                }
            }

            state.dialogView?.let { dialog ->
                AlertDialogError(
                    isVisible = true,
                    title = dialog.title,
                    errorMessage = dialog.subtitle,
                    confirmButton = AlertDialogButton(
                        label = dialog.positiveButtonLabel,
                        buttonType = ErrorButtonType.Contained,
                        onClick = dialog.positiveButtonOnClicked
                    ),
                    dismissButton = AlertDialogButton(
                        label = dialog.negativeButtonLabel,
                        onClick = dialog.negativeButtonOnClicked
                    )
                )
            }
        }
    }
}

@Composable
private fun GenericTile(
    fullName: String,
    profileImageUrl: String,
    onClick: (() -> Unit)?,
    actionButtons: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickableNoRipple(onClick = onClick ?: {}, enabled = onClick != null),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WrapAsyncImage(
                imageUrl = profileImageUrl,
                contentDescription = "", // TODO: Add string resource.
                modifier = Modifier
                    .size(ConstantsUi.GENERIC_IMAGE_TILE_SIZE)
                    .circularImageWithBorder(
                        borderWidth = ConstantsUi.IMAGE_BORDER_WIDTH,
                        borderColor = MaterialTheme.colors.error
                    ),
                error = painterResource(R.drawable.ic_broken_image),
                contentScale = ContentScale.Fit
            )

            HSpacer.Custom(width = SPACING_MEDIUM.dp)

            Text(
                text = fullName,
                style = MaterialTheme.typography.subtitle1
            )

            Spacer(modifier = Modifier.weight(1f))

            actionButtons()
        }
    }
}

@Composable
private fun FriendTile(
    fullName: String,
    profileImageUrl: String,
    onClick: () -> Unit,
    onRemoveClicked: () -> Unit
) {
    GenericTile(
        fullName = fullName,
        profileImageUrl = profileImageUrl,
        onClick = onClick
    ) {
        WrapButton(onClick = onRemoveClicked) {
            Text(text = "remove") // TODO: Add string resource.
        }
    }
}
