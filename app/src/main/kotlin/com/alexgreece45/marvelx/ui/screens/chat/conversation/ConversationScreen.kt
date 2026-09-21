package com.alexgreece45.marvelx.ui.screens.chat.conversation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.extensions.clickableNoRipple
import com.alexgreece45.marvelx.common_ui.util.ObserveUiEffect
import com.alexgreece45.marvelx.core.common.util.flows.single_observer_flow.SingleObserverFlow
import com.alexgreece45.marvelx.domain.model_api.order.OrderIdDomain
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.GenericErrorScreen
import com.alexgreece45.marvelx.ui.components.GenericInfoScreen
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.ThrottledIconButton
import com.alexgreece45.marvelx.ui.components.WrapAsyncImage
import com.alexgreece45.marvelx.ui.components.inputFields.TextInputField
import com.alexgreece45.marvelx.ui.components.isNearEnd
import com.alexgreece45.marvelx.ui.components.listing.ListingPriceLabel
import com.alexgreece45.marvelx.ui.model_generic.UiState
import com.alexgreece45.marvelx.ui.screens.chat.components.dayLabel
import com.alexgreece45.marvelx.ui.screens.chat.conversation.model.ConversationContract
import com.alexgreece45.marvelx.ui.screens.chat.conversation.model.ConversationErrorType
import com.alexgreece45.marvelx.ui.screens.chat.model.ChatMessageStatusUi
import com.alexgreece45.marvelx.ui.screens.chat.model.ChatMessageUi
import com.alexgreece45.marvelx.ui.screens.chat.model.ChatOrderItemUi
import com.alexgreece45.marvelx.ui.screens.chat.model.ConversationListItemUi
import com.alexgreece45.marvelx.ui.screens.orders.components.OrderIdsLabel
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.SPACING_TINY
import com.alexgreece45.marvelx.util.constants.ConstantsUi
import com.alexgreece45.marvelx.util.extensions.circularImageWithBorder
import kotlinx.coroutines.launch

private val BUBBLE_MAX_WIDTH = 300.dp
private val BUBBLE_RADIUS = 16.dp
private val BUBBLE_TAIL_RADIUS = 4.dp
private val PINNED_ITEM_IMAGE_SIZE = 56.dp
private val TOOLBAR_AVATAR_SIZE = 32.dp
private const val OLDER_MESSAGES_PREFETCH_DISTANCE = 6

@Composable
internal fun ConversationScreen(
    onEvent: (ConversationContract.Event) -> Unit,
    state: ConversationContract.State,
    effect: SingleObserverFlow<ConversationContract.Effect>,
    onNavigationEffect: (ConversationContract.Effect.Navigation) -> Unit
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    ObserveUiEffect(flow = effect, tag = "ConversationScreen") { uiEffect ->
        when (uiEffect) {
            is ConversationContract.Effect.Navigation -> onNavigationEffect(uiEffect)
            ConversationContract.Effect.ScrollToLatest -> {
                // reverseLayout puts the newest message at index 0.
                coroutineScope.launch { listState.animateScrollToItem(0) }
            }
        }
    }

    LaunchedEffect(listState) {
        listState.isNearEnd(prefetchDistance = OLDER_MESSAGES_PREFETCH_DISTANCE).collect { isNearEnd ->
            if (isNearEnd) onEvent(ConversationContract.Event.OnLoadOlderMessages)
        }
    }

    val conversation = state.conversation

    ContentScreen(
        isLoading = state.uiState.isLoading,
        navigableAction = ScreenNavigableAction.Backable(
            onBack = { onEvent(ConversationContract.Event.OnBack) }
        ),
        onBack = { onEvent(ConversationContract.Event.OnBack) },
        showToolbar = true,
        toolbarTitle = conversation?.participant?.fullName.orEmpty(),
        toolbarActionsContent = {
            conversation?.let {
                ParticipantAvatar(
                    imageUrl = it.participant.profileImageUrl,
                    fullName = it.participant.fullName,
                    onClick = { onEvent(ConversationContract.Event.OnParticipantClicked) }
                )
            }
        },
        stickyBottom = {
            if (conversation != null) {
//                TextInputField()
                Composer(
                    text = state.messageField.value,
                    isSendEnabled = state.isSendEnabled,
                    onTextChange = { onEvent(ConversationContract.Event.OnMessageTextChanged(it)) },
                    onSendClick = { onEvent(ConversationContract.Event.OnSendClicked) }
                )
            }
        }
    ) { paddingValues ->
        when (val uiState = state.uiState) {
            UiState.Loading -> Unit

            is UiState.Error -> {
                ConversationError(
                    errorType = uiState.errorData,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    onRetryClick = { onEvent(ConversationContract.Event.OnErrorButtonClicked) },
                    onBackClick = { onEvent(ConversationContract.Event.OnBack) }
                )
            }

            is UiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    uiState.data.orderItem?.let { orderItem ->
                        PinnedOrderItem(
                            orderItem = orderItem,
                            onClick = { onEvent(ConversationContract.Event.OnPinnedOrderItemClicked) }
                        )
                    }

                    if (state.listItems.isEmpty()) {
                        EmptyConversationHint(participantName = uiState.data.participant.fullName)
                    } else {
                        MessageList(
                            listItems = state.listItems,
                            isLoadingOlderMessages = state.isLoadingOlderMessages,
                            listState = listState,
                            onRetryMessageClick = { clientMessageId ->
                                onEvent(ConversationContract.Event.OnRetryMessageClicked(clientMessageId))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.ParticipantAvatar(
    imageUrl: String,
    fullName: String,
    onClick: () -> Unit
) {
    WrapAsyncImage(
        imageUrl = imageUrl,
        contentDescription = stringResource(
            R.string.screen_conversation_content_description_participant_avatar,
            fullName
        ),
        modifier = Modifier
            .padding(end = SPACING_SMALL.dp)
            .size(TOOLBAR_AVATAR_SIZE)
            .circularImageWithBorder(
                borderWidth = ConstantsUi.IMAGE_BORDER_WIDTH,
                borderColor = MaterialTheme.customColors.genericRed
            )
            .clickableNoRipple(onClick = onClick)
    )
}

/**
 * The order item this conversation is about. Sits above the message list and never scrolls away,
 * so both sides always know what the chat refers to.
 */
@Composable
private fun PinnedOrderItem(
    orderItem: ChatOrderItemUi,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = SPACING_SMALL.dp)
            .clickableNoRipple(onClick = onClick),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SPACING_SMALL.dp),
            verticalArrangement = Arrangement.spacedBy(SPACING_TINY.dp)
        ) {
            Text(
                text = stringResource(R.string.screen_conversation_pinned_item_label),
                color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
                style = MaterialTheme.typography.caption
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                WrapAsyncImage(
                    imageUrl = orderItem.coverImageUrl,
                    contentDescription = orderItem.title,
                    modifier = Modifier.size(PINNED_ITEM_IMAGE_SIZE),
                    boxModifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.customColors.white),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(SPACING_TINY.dp)
                ) {
                    Text(
                        text = orderItem.title,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2,
                        style = MaterialTheme.typography.subtitle1
                    )

                    ListingPriceLabel(orderItem.priceLabel)

                    OrderIdsLabel(
                        orderId = OrderIdDomain(orderItem.orderId),
                        orderItemId = OrderIdDomain(orderItem.orderItemId)
                    )
                }

                Icon(
                    painter = painterResource(R.drawable.ic_arrow_right),
                    contentDescription = null,
                    tint = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.MessageList(
    listItems: List<ConversationListItemUi>,
    isLoadingOlderMessages: Boolean,
    listState: androidx.compose.foundation.lazy.LazyListState,
    onRetryMessageClick: (clientMessageId: String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        state = listState,
        reverseLayout = true,
        verticalArrangement = Arrangement.spacedBy(SPACING_TINY.dp)
    ) {
        items(
            items = listItems.asReversed(),
            key = { it.key }
        ) { item ->
            when (item) {
                is ConversationListItemUi.DaySeparator -> DaySeparator(date = item.date)
                is ConversationListItemUi.Message -> MessageBubble(
                    message = item.message,
                    onRetryClick = { item.message.clientMessageId?.let(onRetryMessageClick) }
                )
            }
        }

        if (isLoadingOlderMessages) {
            item(key = "loading-older-messages") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SPACING_SMALL.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                }
            }
        }
    }
}

@Composable
private fun DaySeparator(date: java.time.LocalDate) {
    Text(
        text = date.dayLabel(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SPACING_SMALL.dp),
        color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.caption
    )
}

@Composable
private fun MessageBubble(
    message: ChatMessageUi,
    onRetryClick: () -> Unit
) {
    val isMine = message.isFromMe
    val bubbleShape = RoundedCornerShape(
        topStart = BUBBLE_RADIUS,
        topEnd = BUBBLE_RADIUS,
        bottomStart = if (isMine) BUBBLE_RADIUS else BUBBLE_TAIL_RADIUS,
        bottomEnd = if (isMine) BUBBLE_TAIL_RADIUS else BUBBLE_RADIUS
    )
    val backgroundColor = if (isMine) MaterialTheme.colors.primary else MaterialTheme.colors.surface
    val contentColor = if (isMine) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier.widthIn(max = BUBBLE_MAX_WIDTH),
            horizontalAlignment = if (isMine) Alignment.End else Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Surface(
                shape = bubbleShape,
                color = backgroundColor,
                contentColor = contentColor,
                elevation = if (isMine) 0.dp else 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = message.body,
                        style = MaterialTheme.typography.body1
                    )

                    Text(
                        text = message.timeLabel,
                        modifier = Modifier.align(Alignment.End),
                        color = contentColor.copy(alpha = ContentAlpha.medium),
                        style = MaterialTheme.typography.caption
                    )
                }
            }

            when (message.status) {
                ChatMessageStatusUi.SENT -> Unit

                ChatMessageStatusUi.SENDING -> Text(
                    text = stringResource(R.string.screen_conversation_message_status_sending),
                    color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
                    style = MaterialTheme.typography.caption
                )

                ChatMessageStatusUi.FAILED -> Text(
                    text = stringResource(R.string.screen_conversation_message_status_failed),
                    modifier = Modifier.clickableNoRipple(onClick = onRetryClick),
                    color = MaterialTheme.customColors.genericRed,
                    style = MaterialTheme.typography.caption
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.EmptyConversationHint(participantName: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(SPACING_MEDIUM.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.screen_conversation_empty_hint, participantName),
            color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.subtitle1
        )
    }
}

@Composable
private fun Composer(
    text: String,
    isSendEnabled: Boolean,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(SPACING_TINY.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        TextInputField(
            inputField = com.alexgreece45.marvelx.ui.model_generic.InputFieldUi.Text(
                value = text,
                isMandatory = false
            ),
            label = "",
            placeholder = stringResource(R.string.screen_conversation_composer_placeholder),
            modifier = Modifier.weight(1f),
            onValueChanged = onTextChange,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Default
            ),
            minLines = 1,
            maxLines = 4
        )

        ThrottledIconButton(
            onClick = onSendClick,
            enabled = isSendEnabled
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_right),
                contentDescription = stringResource(R.string.screen_conversation_content_description_send),
                tint = if (isSendEnabled) {
                    MaterialTheme.customColors.genericRed
                } else {
                    MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled)
                }
            )
        }
    }
}

@Composable
private fun ConversationError(
    errorType: ConversationErrorType,
    modifier: Modifier,
    onRetryClick: () -> Unit,
    onBackClick: () -> Unit
) {
    when (errorType) {
        ConversationErrorType.Forbidden -> GenericInfoScreen(
            title = stringResource(R.string.screen_conversation_error_forbidden),
            buttonLabel = stringResource(R.string.generic_go_back),
            modifier = modifier,
            onButtonClick = onBackClick
        )

        ConversationErrorType.NoInternet -> GenericErrorScreen(
            errorMessage = stringResource(R.string.no_internet_connection_error_message),
            modifier = modifier,
            onErrorButtonClick = onRetryClick
        )

        ConversationErrorType.Generic -> GenericErrorScreen(
            errorMessage = stringResource(R.string.screen_conversation_error_loading),
            modifier = modifier,
            onErrorButtonClick = onRetryClick
        )
    }
}
