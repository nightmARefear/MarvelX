package com.alexgreece45.marvelx.notifications.model

/**
 * Describes the data of a single notification channel.
 *
 * @param id The unique ID of this channel.
 * @param name The friendly name of this channel -- it will appear in the app's settings.
 */
sealed class NotificationChannelData(val id: String, val name: String) {
    data object FriendRequest :
        NotificationChannelData(id = "FriendRequest", name = "Friend Request Notifications")

    data object Listing : NotificationChannelData(id = "Listing", name = "Listing Notifications")

    companion object {
        fun getAllChannels(): List<NotificationChannelData> = listOf(
            FriendRequest,
            Listing
        )
    }
}
