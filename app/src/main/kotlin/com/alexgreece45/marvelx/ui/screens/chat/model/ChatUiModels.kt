package com.alexgreece45.marvelx.ui.screens.chat.model

import androidx.compose.runtime.Immutable
import com.alexgreece45.marvelx.core.common.extensions.formatPrice
import com.alexgreece45.marvelx.core.common.extensions.toCustomFormat
import com.alexgreece45.marvelx.core.common.extensions.toLocalDate
import com.alexgreece45.marvelx.core.common.util.RelativeTime
import com.alexgreece45.marvelx.core.common.util.toRelativeTime
import com.alexgreece45.marvelx.data.model.chat.ChatMessageData
import com.alexgreece45.marvelx.data.model.chat.ChatMessageStatusData
import com.alexgreece45.marvelx.data.model.chat.ConversationData
import com.alexgreece45.marvelx.data.model.chat.ConversationTypeData
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import java.time.Instant
import java.time.LocalDate

enum class ConversationTypeUi { DIRECT, ORDER_ITEM }

@Immutable
data class ChatParticipantUi(
    val userId: String,
    val fullName: String,
    val profileImageUrl: String
)

@Immutable
data class ChatOrderItemUi(
    val orderId: String,
    val orderItemId: String,
    val sellerUserId: String,
    val title: String,
    val coverImageUrl: String,
    val priceLabel: String
)

@Immutable
data class ConversationUi(
    val conversationId: String,
    val type: ConversationTypeUi,
    val participant: ChatParticipantUi,
    val orderItem: ChatOrderItemUi?,
    val lastMessagePreview: String?,
    val lastActivity: RelativeTime,
    val unreadCount: Int
)

enum class ChatMessageStatusUi { SENDING, SENT, FAILED }

@Immutable
data class ChatMessageUi(
    val messageId: String,
    val clientMessageId: String?,
    val body: String,
    val isFromMe: Boolean,
    val timeLabel: String,
    val sentOn: LocalDate,
    val status: ChatMessageStatusUi
) {
    /** Stable key for lazy lists: server id once known, client id while still in flight. */
    val listKey: String get() = messageId.ifBlank { clientMessageId.orEmpty() }
}

/**
 * What the conversation list actually renders: messages in chronological order with a day
 * separator wherever the date changes.
 */
@Immutable
sealed interface ConversationListItemUi {
    val key: String

    @Immutable
    data class DaySeparator(val date: LocalDate) : ConversationListItemUi {
        override val key: String get() = "day-$date"
    }

    @Immutable
    data class Message(val message: ChatMessageUi) : ConversationListItemUi {
        override val key: String get() = "message-${message.listKey}"
    }
}

fun ConversationData.toUi(priceScale: Int, now: Instant = Instant.now()): ConversationUi =
    ConversationUi(
        conversationId = conversationId,
        type = when (type) {
            ConversationTypeData.DIRECT -> ConversationTypeUi.DIRECT
            ConversationTypeData.ORDER_ITEM -> ConversationTypeUi.ORDER_ITEM
        },
        participant = ChatParticipantUi(
            userId = participant.userId,
            fullName = participant.fullName,
            profileImageUrl = participant.profileImageUrl.orEmpty()
        ),
        orderItem = orderItem?.let {
            ChatOrderItemUi(
                orderId = it.orderId,
                orderItemId = it.orderItemId,
                sellerUserId = it.sellerUserId,
                title = it.title,
                coverImageUrl = it.coverImageUrl,
                priceLabel = it.price.formatPrice(priceScale)
            )
        },
        lastMessagePreview = lastMessage?.body,
        lastActivity = updatedAt.toRelativeTime(now),
        unreadCount = unreadCount
    )

fun ChatMessageData.toUi(currentUserId: String): ChatMessageUi =
    ChatMessageUi(
        messageId = messageId,
        clientMessageId = clientMessageId,
        body = body,
        isFromMe = senderUserId == currentUserId,
        timeLabel = sentAt.toCustomFormat("HH:mm"),
        sentOn = sentAt.toLocalDate(),
        status = when (status) {
            ChatMessageStatusData.SENDING -> ChatMessageStatusUi.SENDING
            ChatMessageStatusData.SENT -> ChatMessageStatusUi.SENT
            ChatMessageStatusData.FAILED -> ChatMessageStatusUi.FAILED
        }
    )

/**
 * Interleaves day separators into a chronologically ordered message list.
 */
fun List<ChatMessageUi>.toConversationListItems(): ImmutableList<ConversationListItemUi> {
    val items = ArrayList<ConversationListItemUi>(size + 8)
    var currentDay: LocalDate? = null
    forEach { message ->
        if (message.sentOn != currentDay) {
            currentDay = message.sentOn
            items += ConversationListItemUi.DaySeparator(message.sentOn)
        }
        items += ConversationListItemUi.Message(message)
    }
    return items.toImmutableList()
}
