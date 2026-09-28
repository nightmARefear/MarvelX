package com.alexgreece45.marvelx.data.model.notification

import com.alexgreece45.marvelx.core.network.model.api.notification.ResponseNotification
import com.alexgreece45.marvelx.data.model.deepLink.DeepLinkAction

/**
 * A single notifications-inbox entry. [channelType] stays raw here; the domain layer maps it and
 * drops types this app version doesn't know.
 */
data class NotificationItemData(
    val notificationId: String,
    val channelType: String,
    val title: String,
    val body: String,
    val imageUrl: String?,
    val createdAt: String,
    val isRead: Boolean,
    val deepLinkAction: DeepLinkAction
)

fun ResponseNotification.toData(): NotificationItemData =
    NotificationItemData(
        notificationId = notificationId,
        channelType = channelType,
        title = title,
        body = body,
        imageUrl = imageUrl,
        createdAt = createdAt,
        isRead = isRead,
        deepLinkAction = DeepLinkAction(
            screenRoute = deepLinkAction.screenRoute,
            data = deepLinkAction.data
        )
    )
