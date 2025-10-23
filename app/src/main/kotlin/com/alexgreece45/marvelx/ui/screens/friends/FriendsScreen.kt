package com.alexgreece45.marvelx.ui.screens.friends

import android.text.Html
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.AlertDialogButton
import com.alexgreece45.marvelx.ui.components.AlertDialogError
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.LoadingScreen
import com.alexgreece45.marvelx.ui.components.PullRefreshable
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.WrapAsyncImage
import com.alexgreece45.marvelx.ui.components.WrapButton
import com.alexgreece45.marvelx.ui.components.WrapOutlinedButton
import com.alexgreece45.marvelx.ui.components.effect.ObserveUiEffect
import com.alexgreece45.marvelx.ui.components.error.ContentErrorScreenHeader
import com.alexgreece45.marvelx.ui.components.model.ErrorButtonType
import com.alexgreece45.marvelx.ui.screens.friends.model.FriendsContract
import com.alexgreece45.marvelx.ui.theme.values.customColors
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.VSpacer
import com.alexgreece45.marvelx.util.constants.ConstantsUi
import com.alexgreece45.marvelx.util.extensions.circularImageWithBorder
import com.alexgreece45.marvelx.util.extensions.clickableNoRipple
import com.alexgreece45.marvelx.util.extensions.toAnnotatedString
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
                .padding(paddingValues)
        ) {
            if (state.tabTitles.isEmpty()) return@ContentScreen

            TabRow(
                selectedTabIndex = state.selectedTabIndex,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[state.selectedTabIndex]),
                        color = MaterialTheme.customColors.genericRed
                    )
                }
            ) {
                state.tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = index == state.selectedTabIndex,
                        onClick = { onEventSent(FriendsContract.Event.OnTabClicked(index)) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = title,
                            modifier = Modifier.padding(vertical = SPACING_SMALL.dp)
                        )
                    }
                }
            }

            PullRefreshable(
                onRefresh = { onEventSent(FriendsContract.Event.OnPullToRefresh) },
                isRefreshing = state.isTabRefreshing
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    VSpacer.Medium()

                    if (state.isTabLoading) {
                        LoadingScreen()
                        return@PullRefreshable
                    }

                    state.errorView?.let { error ->
                        if (state.selectedTabIndex != error.tabIndex) return@let

                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ContentErrorScreenHeader(
                                title = stringResource(R.string.generic_error),
                                subtitle = error.subtitle
                            )

                            WrapOutlinedButton(onClick = error.primaryButtonOnClicked) {
                                Text(
                                    text = stringResource(R.string.generic_retry_button)
                                )
                            }
                        }

                        return@PullRefreshable
                    }

                    when (state.selectedTabIndex) {
                        0 -> {
                            GenericListView(
                                noItemsLabel = stringResource(id = R.string.screen_friends_tab_all_no_friends),
                                items = state.allFriends,
                                key = { it.userId }
                            ) { friend ->
                                FriendTile(
                                    fullName = friend.fullName,
                                    profileImageUrl = friend.profileImageUrl,
                                    onClick = {
                                        onEventSent(
                                            FriendsContract.Event.AllTabEvent.OnUserClicked(
                                                friend.userId
                                            )
                                        )
                                    },
                                    onRemoveClicked = {
                                        onEventSent(
                                            FriendsContract.Event.AllTabEvent.OnRemoveClicked(
                                                friend.userId
                                            )
                                        )
                                    }
                                )
                            }
                        }

                        1 -> {
                            GenericListView(
                                noItemsLabel = stringResource(R.string.screen_friends_tab_requests_no_requests),
                                items = state.incomingFriendRequests,
                                key = { it.userId }
                            ) { incomingFriendRequest ->
                                IncomingFriendRequestTile(
                                    fullName = incomingFriendRequest.fullName,
                                    profileImageUrl = incomingFriendRequest.profileImageUrl,
                                    receivedAt = incomingFriendRequest.receivedAtFormatted,
                                    onClick = {
                                        onEventSent(
                                            FriendsContract.Event.RequestsTabEvent.OnUserClicked(
                                                userId = incomingFriendRequest.userId
                                            )
                                        )
                                    },
                                    onDeclineClicked = {
                                        onEventSent(
                                            FriendsContract.Event.RequestsTabEvent.OnRequestDeclined(
                                                userId = incomingFriendRequest.userId
                                            )
                                        )
                                    },
                                    onAcceptClicked = {
                                        onEventSent(
                                            FriendsContract.Event.RequestsTabEvent.OnRequestAccepted(
                                                incomingFriendRequest = incomingFriendRequest
                                            )
                                        )
                                    }
                                )
                            }
                        }

                        2 -> {
                            GenericListView(
                                noItemsLabel = stringResource(id = R.string.screen_friends_tab_blocked_no_blocked_users),
                                items = state.blockedUsers,
                                key = { it.userId }
                            ) { blockedUser ->
                                BlockTile(
                                    fullName = blockedUser.fullName,
                                    profileImageUrl = blockedUser.profileImageUrl,
                                    onUnblockClicked = {
                                        onEventSent(
                                            FriendsContract.Event.BlockedTabEvent.OnUnblockClicked(
                                                blockedUserDomain = blockedUser
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }

            state.dialogView?.let { dialog ->
                AlertDialogError(
                    isVisible = true,
                    title = dialog.title,
                    errorMessage = {
                        if (dialog.doesSubtitleContainBold) {
                            Text(
                                text = Html.fromHtml(
                                    dialog.subtitle,
                                    Html.FROM_HTML_MODE_COMPACT
                                ).toAnnotatedString()
                            )
                        } else {
                            Text(text = dialog.subtitle)
                        }
                    },
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

/**
 * Draws a generic list of items, or displays a message if no items were found.
 */
@Composable
private fun <T> GenericListView(
    noItemsLabel: String,
    items: List<T>,
    key: (item: T) -> Any,
    itemContent: @Composable() (LazyItemScope.(item: T) -> Unit),
) {
    if (items.isEmpty()) {
        NoItemsFoundText(label = noItemsLabel)
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
    ) {
        items(
            items = items,
            key = key,
            itemContent = itemContent
        )
    }
}

/**
 * Displays a text view when no items are found in a tab.
 */
@Composable
private fun NoItemsFoundText(label: String) {
    Text(
        text = label,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}

/**
 * The base tile.
 */
@Composable
private fun GenericTile(
    fullName: String,
    profileImageUrl: String,
    onClick: (() -> Unit)?,
    content: @Composable () -> Unit
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
            horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WrapAsyncImage(
                imageUrl = profileImageUrl,
                contentDescription = stringResource(
                    id = R.string.screen_friends_content_description_profile_image,
                    fullName
                ),
                modifier = Modifier
                    .size(90.dp)
                    .circularImageWithBorder(
                        borderWidth = ConstantsUi.IMAGE_BORDER_WIDTH,
                        borderColor = MaterialTheme.customColors.genericRed
                    ),
                error = painterResource(R.drawable.ic_broken_image),
                contentScale = ContentScale.Fit
            )

            content()
        }
    }
}

/**
 * The full name text.
 */
@Composable
private fun FullName(fullName: String, modifier: Modifier = Modifier) {
    Text(
        text = fullName,
        modifier = modifier,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        style = MaterialTheme.typography.h6
    )
}

/**
 * Draws a single friend.
 */
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FullName(
                fullName = fullName,
                modifier = Modifier.weight(1f)
            )

            WrapButton(
                onClick = onRemoveClicked,
                colors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.customColors.buttonContentWhite,
                    backgroundColor = MaterialTheme.customColors.buttonBackgroundNegative
                )
            ) {
                Text(text = stringResource(id = R.string.screen_friends_tab_all_button_remove_friend))
            }
        }
    }
}

@Composable
private fun IncomingFriendRequestTile(
    fullName: String,
    profileImageUrl: String,
    receivedAt: String,
    onClick: () -> Unit,
    onDeclineClicked: () -> Unit,
    onAcceptClicked: () -> Unit
) {
    GenericTile(
        fullName = fullName,
        profileImageUrl = profileImageUrl,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FullName(
                    fullName = fullName,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = receivedAt,
                    color = MaterialTheme.customColors.friendsDateColor,
                    style = MaterialTheme.typography.body2
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                WrapButton(
                    onClick = onAcceptClicked,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.customColors.buttonContentWhite,
                        backgroundColor = MaterialTheme.customColors.buttonBackgroundPositive
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.screen_friends_tab_requests_button_positive),
                        maxLines = 1
                    )
                }

                WrapButton(
                    onClick = onDeclineClicked,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.customColors.buttonContentWhite,
                        backgroundColor = MaterialTheme.customColors.buttonBackgroundNegative
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.screen_friends_tab_requests_button_negative),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun BlockTile(
    fullName: String,
    profileImageUrl: String,
    onUnblockClicked: () -> Unit
) {
    GenericTile(
        fullName = fullName,
        profileImageUrl = profileImageUrl,
        onClick = null
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FullName(
                fullName = fullName,
                modifier = Modifier.weight(1f)
            )

            WrapButton(
                onClick = onUnblockClicked,
            ) {
                Text(text = stringResource(id = R.string.screen_friends_tab_blocked_button_positive))
            }
        }
    }
}
