package com.alexgreece45.marvelx.domain.model_api.user.friend

import com.alexgreece45.marvelx.core.common.extensions.defaultFormat
import com.alexgreece45.marvelx.core.network.model.api.user.friend.ResponseFriend
import com.alexgreece45.marvelx.model.model.user.UserId
import com.alexgreece45.marvelx.util.extensions.toFullName
import java.time.Instant

/**
 * Represents a friend.
 */
data class FriendDomain(
    val userId: UserId,
    val fullName: String,
    val profileImageUrl: String,
    val friendsSince: Instant
) {

    /**
     * When the friendship started.
     */
    val friendsSinceFormatted: String = friendsSince.defaultFormat()
}

fun List<ResponseFriend>.toFriendDomainList(): List<FriendDomain> =
    this.map { friend ->
        FriendDomain(
            userId = UserId(friend.userId),
            fullName = Pair(friend.firstName, friend.lastName).toFullName(),
            profileImageUrl = friend.profileImageUrl,
            friendsSince = Instant.parse(friend.friendsSince),
        )
    }