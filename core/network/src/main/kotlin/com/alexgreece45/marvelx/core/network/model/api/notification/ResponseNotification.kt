package com.alexgreece45.marvelx.core.network.model.api.notification

import com.google.gson.annotations.SerializedName

/**
 * A single inbox notification. Backend contract: `docs/backend-specs/notifications.md`.
 */
data class ResponseNotification(
    @SerializedName("notificationId") val notificationId: String,
    @SerializedName("channelType") val channelType: String,
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String,
    @SerializedName("imageUrl") val imageUrl: String? = null,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("isRead") val isRead: Boolean,
    @SerializedName("deepLinkAction") val deepLinkAction: ResponseNotificationDeepLinkAction
)

/**
 * @param data The route arguments, in route order (Gson keeps the JSON key order).
 */
data class ResponseNotificationDeepLinkAction(
    @SerializedName("screenRoute") val screenRoute: String,
    @SerializedName("data") val data: Map<String, Any>
)

data class ResponseNotificationsUnreadCount(
    @SerializedName("count") val count: Int
)
