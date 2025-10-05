package com.alexgreece45.marvelx.domain.model_api.user.friend

import com.alexgreece45.marvelx.network_logic.models_api.user.friend.ResponseFriend
import com.alexgreece45.marvelx.util.extensions.toFullName
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Represents a friend.
 */
data class FriendDomain(
    val userId: String,
    val fullName: String,
    val profileImageUrl: String,
    val friendsSince: Long
) {

    /**
     * When the friendship started.
     */
    val friendsSinceFormatted: String = Instant.ofEpochMilli(friendsSince)
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
}

fun List<ResponseFriend>.toFriendDomainList(): List<FriendDomain> =
    this.map { friend ->
        FriendDomain(
            userId = friend.userId,
            fullName = Pair(friend.firstName, friend.lastName).toFullName(),
            profileImageUrl = friend.profileImageUrl,
            friendsSince = friend.friendsSince,
        )
    }
