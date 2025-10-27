package com.alexgreece45.marvelx.domain.model_api.user.profileDetails

/**
 * Defines the relationship status between two users.
 *
 * __Note:__ If the users have blocked each other, this is not captured by the relationship status.
 *
 * @param key The de-serialized value for mapping purposes.
 */
sealed class RelationshipStatusDomain(val key: String) {
    /**
     * There is no relationship between the two users -- i.e., they are not friends.
     */
    data object None : RelationshipStatusDomain(key = "none")

    /**
     * There is a pending friend request from this user.
     */
    data object PendingFriendRequestSender :
        RelationshipStatusDomain(key = "pending_friend_request_sender")

    /**
     * There is an incoming friend request from the other user.
     */
    data object PendingFriendRequestReceiver :
        RelationshipStatusDomain(key = "pending_friend_request_receiver")

    /**
     * The two users are friends.
     */
    data object Friends : RelationshipStatusDomain(key = "friends")

    companion object {
        private fun asList(): List<RelationshipStatusDomain> =
            listOf(
                None,
                PendingFriendRequestSender,
                PendingFriendRequestReceiver,
                Friends
            )

        /**
         * Retrieves the corresponding [RelationshipStatusDomain] based on the given [key]. If no
         * match was found, a [NoSuchElementException] is thrown.
         */
        fun fromKey(key: String): RelationshipStatusDomain = asList().first { it.key == key }
    }
}
