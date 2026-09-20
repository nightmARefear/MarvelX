package com.alexgreece45.marvelx.ui.screens.chat.inbox

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.util.ObserveUiEffect
import com.alexgreece45.marvelx.core.common.util.flows.single_observer_flow.SingleObserverFlow
import com.alexgreece45.marvelx.ui.components.BottomNavigationBarData
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.GenericErrorScreen
import com.alexgreece45.marvelx.ui.components.GenericInfoScreen
import com.alexgreece45.marvelx.ui.components.PullRefreshable
import com.alexgreece45.marvelx.ui.components.UserTile
import com.alexgreece45.marvelx.ui.components.WrapAsyncImage
import com.alexgreece45.marvelx.ui.model_generic.UiState
import com.alexgreece45.marvelx.ui.screens.chat.components.label
import com.alexgreece45.marvelx.ui.screens.chat.inbox.model.InboxContract
import com.alexgreece45.marvelx.ui.screens.chat.model.ConversationUi
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.SPACING_TINY

private val ORDER_ITEM_THUMBNAIL_SIZE = 20.dp

@Composable
internal fun InboxScreen(
    onEvent: (InboxContract.Event) -> Unit,
    state: InboxContract.State,
    effect: SingleObserverFlow<InboxContract.Effect>,
    onNavigationEffect: (InboxContract.Effect.Navigation) -> Unit,
    bottomNavigationBarData: @Composable () -> BottomNavigationBarData
) {
    LaunchedEffect(Unit) {
        onEvent(InboxContract.Event.Init)
    }

    ObserveUiEffect(flow = effect, tag = "InboxScreen") { uiEffect ->
        when (uiEffect) {
            is InboxContract.Effect.Navigation -> onNavigationEffect(uiEffect)
        }
    }

    ContentScreen(
        isLoading = state.uiState.isLoading,
        showToolbar = true,
        toolbarTitle = stringResource(R.string.screen_inbox_toolbar_title),
        bottomNavigationBarData = bottomNavigationBarData()
    ) { paddingValues ->
        when (val uiState = state.uiState) {
            UiState.Loading -> Unit

            is UiState.Error -> {
                GenericErrorScreen(
                    errorMessage = stringResource(R.string.screen_inbox_error_loading),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    onErrorButtonClick = { onEvent(InboxContract.Event.OnErrorButtonClicked) }
                )
            }

            is UiState.Success -> {
                PullRefreshable(
                    onRefresh = { onEvent(InboxContract.Event.OnRefresh) },
                    isRefreshing = state.isRefreshing
                ) {
                    if (uiState.data.isEmpty()) {
                        GenericInfoScreen(
                            title = stringResource(R.string.screen_inbox_empty_title),
                            buttonLabel = stringResource(R.string.generic_retry_button),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues),
                            onButtonClick = { onEvent(InboxContract.Event.OnRefresh) }
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = paddingValues,
                            verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
                        ) {
                            items(
                                items = uiState.data,
                                key = { it.conversationId }
                            ) { conversation ->
                                ConversationRow(
                                    conversation = conversation,
                                    onClick = {
                                        onEvent(
                                            InboxContract.Event.OnConversationClicked(conversation.conversationId)
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ConversationRow(
    conversation: ConversationUi,
    onClick: () -> Unit
) {
    val hasUnread = conversation.unreadCount > 0

    UserTile(
        profileImageUrl = conversation.participant.profileImageUrl,
        profileImageContentDescription = stringResource(
            R.string.screen_inbox_content_description_avatar,
            conversation.participant.fullName
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(SPACING_TINY.dp)
        ) {
            Text(
                text = conversation.participant.fullName,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                fontWeight = if (hasUnread) FontWeight.Bold else FontWeight.Normal,
                style = MaterialTheme.typography.h6
            )

            // Order-item chats carry the item they are about, so they read differently at a glance.
            conversation.orderItem?.let { orderItem ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(SPACING_TINY.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WrapAsyncImage(
                        imageUrl = orderItem.coverImageUrl,
                        contentDescription = orderItem.title,
                        modifier = Modifier.size(ORDER_ITEM_THUMBNAIL_SIZE),
                        boxModifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .background(MaterialTheme.customColors.white),
                        contentScale = ContentScale.Crop
                    )

                    Text(
                        text = orderItem.title,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
                        style = MaterialTheme.typography.caption
                    )
                }
            }

            Text(
                text = conversation.lastMessagePreview
                    ?: stringResource(R.string.screen_inbox_no_messages_yet),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                color = if (hasUnread) {
                    MaterialTheme.colors.onSurface
                } else {
                    MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
                },
                fontWeight = if (hasUnread) FontWeight.SemiBold else FontWeight.Normal,
                style = MaterialTheme.typography.body2
            )
        }

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(SPACING_TINY.dp)
        ) {
            Text(
                text = conversation.lastActivity.label(),
                color = if (hasUnread) {
                    MaterialTheme.customColors.genericRed
                } else {
                    MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
                },
                style = MaterialTheme.typography.caption
            )

            if (hasUnread) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .background(color = MaterialTheme.customColors.genericRed, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = conversation.unreadCount.coerceAtMost(99).toString(),
                        color = MaterialTheme.customColors.white,
                        style = MaterialTheme.typography.caption,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
