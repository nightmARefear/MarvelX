package com.alexgreece45.marvelx.notifications.model

/**
 * Describes a single notification channel type. Each channel is mapped based on the [type] which is
 * derived from the backend. The Frontend uses [NotificationChannelData.id] to work with notifications.
 * Two channels can have the same [channelData].
 *
 * @param type The specific type of this type.
 * @param channelData The channel data of this type.
 */
sealed class NotificationChannelType(val type: String, val channelData: NotificationChannelData) {

    /**
     * The incoming friend requests channel type.
     */
    data object IncomingFriendRequest : NotificationChannelType(
        type = "IncomingFriendRequest",
        channelData = NotificationChannelData.FriendRequest
    )

    /**
     * The accept friend requests channel type.
     */
    data object AcceptFriendRequest : NotificationChannelType(
        type = "AcceptFriendRequest",
        channelData = NotificationChannelData.FriendRequest
    )

    data object ListingPurchase : NotificationChannelType(
        type = "ListingPurchase",
        channelData = NotificationChannelData.Listing
    )

    companion object {
        private fun getAllTypes(): List<NotificationChannelType> = listOf(
            IncomingFriendRequest,
            AcceptFriendRequest,
            ListingPurchase
        )

        fun getChannelByType(channelType: String): NotificationChannelType =
            getAllTypes().first { it.type == channelType }
    }
}
