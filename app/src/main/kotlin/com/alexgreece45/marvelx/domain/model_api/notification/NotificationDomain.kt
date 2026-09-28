package com.alexgreece45.marvelx.domain.model_api.notification

import com.alexgreece45.marvelx.data.model.deepLink.DeepLinkAction
import com.alexgreece45.marvelx.data.model.notification.NotificationItemData
import com.alexgreece45.marvelx.notifications.model.NotificationChannelType
import java.time.Instant

data class NotificationDomain(
    val notificationId: String,
    val channelType: NotificationChannelType,
    val title: String,
    val body: String,
    val imageUrl: String?,
    val createdAt: Instant,
    val isRead: Boolean,
    val deepLinkAction: DeepLinkAction
)

/**
 * One page of the inbox.
 *
 * @param isLastPage Based on the raw page, before unknown types are dropped, so a page holding only
 * unknown types doesn't end the list early.
 */
data class NotificationsPageDomain(
    val items: List<NotificationDomain>,
    val isLastPage: Boolean
)

fun List<NotificationItemData>.toNotificationsPageDomain(): NotificationsPageDomain =
    NotificationsPageDomain(
        items = mapNotNull { it.toDomainOrNull() },
        isLastPage = isEmpty()
    )

/**
 * Returns `null` for a channel type this app version doesn't know, so a newer backend type is
 * skipped instead of failing the whole page.
 */
fun NotificationItemData.toDomainOrNull(): NotificationDomain? {
    val channelType = NotificationChannelType.findChannelByType(channelType) ?: return null
    return NotificationDomain(
        notificationId = notificationId,
        channelType = channelType,
        title = title,
        body = body,
        imageUrl = imageUrl,
        createdAt = Instant.parse(createdAt),
        isRead = isRead,
        deepLinkAction = deepLinkAction
    )
}
