package com.alexgreece45.marvelx.notifications.model

import com.alexgreece45.marvelx.data.model.deepLink.DeepLinkAction
import com.google.gson.Gson

/**
 * Contains all the data for a notification.
 *
 * @param notificationId The unique identifier for this notification.
 * @param channelType The channel type of this notification.
 * @param title The title of this notification.
 * @param body The description of this notification.
 * @param userId The other user's ID associated with this notification.
 * @param imageBytes The optional image bytes associated with the other user/listing.
 * @param deepLinkAction The deep link action associated with this notification.
 */
data class NotificationData(
    val notificationId: Int,
    val channelType: NotificationChannelType,
    val title: String,
    val body: String,
    val userId: String,
    val imageBytes: ByteArray? = null,
    val deepLinkAction: DeepLinkAction
) {

    fun getStringifiedDeepLinkAction(gson: Gson): String =
        gson.toJson(deepLinkAction)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NotificationData

        if (notificationId != other.notificationId) return false
        if (channelType != other.channelType) return false
        if (title != other.title) return false
        if (body != other.body) return false
        if (userId != other.userId) return false
        if (!imageBytes.contentEquals(other.imageBytes)) return false
        if (deepLinkAction != other.deepLinkAction) return false

        return true
    }

    override fun hashCode(): Int {
        var result = notificationId
        result = 31 * result + channelType.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + body.hashCode()
        result = 31 * result + userId.hashCode()
        result = 31 * result + (imageBytes?.contentHashCode() ?: 0)
        result = 31 * result + deepLinkAction.hashCode()
        return result
    }
}
