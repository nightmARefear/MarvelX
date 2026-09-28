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

    data object NewMessage : NotificationChannelType(
        type = "NewMessage",
        channelData = NotificationChannelData.Chat
    )

    /**
     * The buyer's order status changed as a whole (e.g. partially shipped -> shipped).
     */
    data object OrderStatusChanged : NotificationChannelType(
        type = "OrderStatusChanged",
        channelData = NotificationChannelData.Order
    )

    companion object {
        private fun getAllTypes(): List<NotificationChannelType> = listOf(
            IncomingFriendRequest,
            AcceptFriendRequest,
            ListingPurchase,
            NewMessage,
            OrderStatusChanged
        )

        fun getChannelByType(channelType: String): NotificationChannelType =
            getAllTypes().first { it.type == channelType }

        /**
         * Like [getChannelByType], but returns `null` for a type this app version doesn't know.
         */
        fun findChannelByType(channelType: String): NotificationChannelType? =
            getAllTypes().firstOrNull { it.type == channelType }
    }
}
