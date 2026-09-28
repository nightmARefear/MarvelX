package com.alexgreece45.marvelx.ui.screens.notifications

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.extensions.clickableNoRipple
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarEvent
import com.alexgreece45.marvelx.common_ui.util.ObserveUiEffect
import com.alexgreece45.marvelx.core.common.util.flows.single_observer_flow.SingleObserverFlow
import com.alexgreece45.marvelx.data.model.deepLink.DeepLinkAction
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.IconData
import com.alexgreece45.marvelx.ui.components.LazyItemContentErrorScreen
import com.alexgreece45.marvelx.ui.components.PullRefreshable
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.ToolbarAction
import com.alexgreece45.marvelx.ui.components.WrapAsyncImage
import com.alexgreece45.marvelx.ui.components.isNearEnd
import com.alexgreece45.marvelx.ui.model_generic.InstantUi
import com.alexgreece45.marvelx.ui.screens.marketplace.components.ListingImage
import com.alexgreece45.marvelx.ui.screens.notifications.model.NotificationUi
import com.alexgreece45.marvelx.ui.screens.notifications.model.NotificationsContract
import com.alexgreece45.marvelx.ui_theme.theme.MarvelXTheme
import com.alexgreece45.marvelx.ui_theme.theme.import_aliases.UiThemeDrawable
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.SPACING_TINY
import com.alexgreece45.marvelx.util.constants.ConstantsUi
import com.alexgreece45.marvelx.util.extensions.circularImageWithBorder
import java.time.Instant
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter

/**
 * How many rows from the end of the list the next page is requested at.
 */
private const val LOAD_MORE_PREFETCH_DISTANCE = 3

@Composable
internal fun NotificationsScreen(
    onEvent: (NotificationsContract.Event) -> Unit,
    state: NotificationsContract.State,
    effect: SingleObserverFlow<NotificationsContract.Effect>,
    onNavigationEffect: (NotificationsContract.Effect.Navigation) -> Unit,
    snackbarEffects: SingleObserverFlow<SnackbarEvent>,
) {
    val lazyListState = rememberLazyListState()

    ObserveUiEffect(flow = effect, tag = "NotificationsScreen") { uiEffect ->
        when (uiEffect) {
            is NotificationsContract.Effect.Navigation -> onNavigationEffect(uiEffect)
        }
    }

    LaunchedEffect(Unit) {
        onEvent(NotificationsContract.Event.Init)
    }

    LaunchedEffect(lazyListState) {
        lazyListState.isNearEnd(prefetchDistance = LOAD_MORE_PREFETCH_DISTANCE)
            .filter { isNearEnd -> isNearEnd }
            .collect {
                onEvent(NotificationsContract.Event.OnEndOfListScrolled)
            }
    }

    ContentScreen(
        isLoading = state.isLoading && !state.isRefreshing,
        navigableAction = ScreenNavigableAction.Backable(
            testTag = NotificationsTestTags.BACK_BUTTON,
            onBack = { onEvent(NotificationsContract.Event.OnBackClicked) }
        ),
        onBack = { onEvent(NotificationsContract.Event.OnBackClicked) },
        showToolbar = true,
        toolbarTitle = stringResource(R.string.screen_notifications_toolbar_title),
        toolbarActions = listOf(
            ToolbarAction(
                label = stringResource(R.string.screen_notifications_toolbar_action_mark_all_as_read),
                order = 1,
                iconData = IconData(
                    iconResource = R.drawable.ic_check,
                    contentDescription = R.string.screen_notifications_toolbar_action_mark_all_as_read,
                    enabled = state.hasUnread
                ),
                testTag = NotificationsTestTags.MARK_ALL_AS_READ_BUTTON,
                onClick = { onEvent(NotificationsContract.Event.OnMarkAllAsReadClicked) }
            )
        ),
        snackbarEvents = snackbarEffects
    ) { paddingValues ->
        if (state.isLoading && !state.isRefreshing) return@ContentScreen

        // Same list setup as the Inbox: the screen padding is the list's content padding, so rows
        // line up with the other screens and scroll edge to edge.
        PullRefreshable(
            onRefresh = { onEvent(NotificationsContract.Event.OnPullToRefresh) },
            isRefreshing = state.isRefreshing
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = lazyListState,
                contentPadding = paddingValues,
                verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
            ) {
                if (state.hasError) {
                    item(key = "errorView") {
                        LazyItemContentErrorScreen(
                            errorMessage = stringResource(R.string.screen_notifications_error_loading),
                            retryTestTag = NotificationsTestTags.ERROR_RETRY_BUTTON,
                            onRetryClick = { onEvent(NotificationsContract.Event.OnErrorRetryClicked) }
                        )
                    }
                    return@LazyColumn
                }

                if (state.notifications.isEmpty()) {
                    item(key = "emptyView") {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.screen_notifications_empty),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
                            )
                        }
                    }
                    return@LazyColumn
                }

                items(
                    items = state.notifications,
                    key = { it.notificationId }
                ) { notification ->
                    NotificationRow(
                        notification = notification,
                        onClick = { onEvent(NotificationsContract.Event.OnNotificationClicked(notification)) }
                    )
                }

                item(key = "additionalItemsLoadingView") {
                    AnimatedVisibility(
                        visible = state.isFetchingNewItems,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(ConstantsUi.MIN_ICON_SIZE),
                                color = MaterialTheme.customColors.genericRed
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Spaced like the Inbox rows ([com.alexgreece45.marvelx.ui.components.UserTile]): a card with 12dp
 * insets and a [ConstantsUi.GENERIC_IMAGE_TILE_SIZE] image.
 */
@Composable
private fun NotificationRow(
    notification: NotificationUi,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(NotificationsTestTags.notificationRow(notification.notificationId))
            .clickableNoRipple(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        // Unread rows keep their light red highlight.
        backgroundColor = if (notification.isRead) {
            MaterialTheme.colors.surface
        } else {
            MaterialTheme.customColors.genericRed.copy(alpha = 0.08f).compositeOver(MaterialTheme.colors.surface)
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val imageUrl = notification.imageUrl
            if (imageUrl != null && notification.isPersonImage) {
                // Same as the seller image on Listing Details.
                WrapAsyncImage(
                    imageUrl = imageUrl,
                    contentDescription = notification.title,
                    modifier = Modifier
                        .size(ConstantsUi.GENERIC_IMAGE_TILE_SIZE)
                        .circularImageWithBorder(
                            borderWidth = ConstantsUi.IMAGE_BORDER_WIDTH,
                            borderColor = MaterialTheme.customColors.genericRed
                        ),
                    contentScale = ContentScale.Crop
                )
            } else if (imageUrl != null) {
                ListingImage(
                    imageUrl = imageUrl,
                    title = notification.title,
                    modifier = Modifier.size(ConstantsUi.GENERIC_IMAGE_TILE_SIZE)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(ConstantsUi.GENERIC_IMAGE_TILE_SIZE)
                        .clip(CircleShape)
                        .background(MaterialTheme.colors.background),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(notification.iconResId),
                        contentDescription = null,
                        modifier = Modifier.size(ConstantsUi.MIN_ICON_SIZE)
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(SPACING_TINY.dp)
            ) {
                Text(
                    text = notification.title,
                    fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = notification.body,
                    style = MaterialTheme.typography.body2,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = notification.createdAt.label,
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
                )
            }

            if (!notification.isRead) {
                Box(
                    modifier = Modifier
                        .size(SPACING_SMALL.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.customColors.genericRed)
                )
            }
        }
    }
}

// Previews.

private val previewNotifications = persistentListOf(
    NotificationUi(
        notificationId = "2",
        iconResId = UiThemeDrawable.ic_money_bag,
        title = "Your listing sold!",
        body = "alex voliotis bought \"Canas 50x20\".",
        imageUrl = null,
        isPersonImage = false,
        createdAt = InstantUi(value = Instant.parse("2026-09-28T10:15:00Z"), label = "Sep 28, 10:15"),
        isRead = false,
        deepLinkAction = DeepLinkAction(
            screenRoute = "OrderItemManagementRoute",
            data = mapOf("orderId" to "3", "orderItemId" to "5")
        )
    ),
    NotificationUi(
        notificationId = "1",
        iconResId = R.drawable.ic_orders,
        title = "Order #3 updated",
        body = "Some of your items have shipped.",
        imageUrl = null,
        isPersonImage = false,
        createdAt = InstantUi(value = Instant.parse("2026-09-27T08:00:00Z"), label = "Sep 27, 08:00"),
        isRead = true,
        deepLinkAction = DeepLinkAction(
            screenRoute = "OrderDetailsRoute",
            data = mapOf("orderId" to "3")
        )
    ),
)

@Preview
@Composable
private fun NotificationsScreenPreview() {
    MarvelXTheme {
        NotificationsScreen(
            onEvent = {},
            state = NotificationsContract.State(
                isLoading = false,
                currentPage = 1,
                notifications = previewNotifications
            ),
            effect = SingleObserverFlow(emptyFlow()),
            onNavigationEffect = {},
            snackbarEffects = SingleObserverFlow(emptyFlow())
        )
    }
}

@Preview
@Composable
private fun NotificationsScreenEmptyPreview() {
    MarvelXTheme {
        NotificationsScreen(
            onEvent = {},
            state = NotificationsContract.State(isLoading = false, currentPage = 1, hasReachedEnd = true),
            effect = SingleObserverFlow(emptyFlow()),
            onNavigationEffect = {},
            snackbarEffects = SingleObserverFlow(emptyFlow())
        )
    }
}
