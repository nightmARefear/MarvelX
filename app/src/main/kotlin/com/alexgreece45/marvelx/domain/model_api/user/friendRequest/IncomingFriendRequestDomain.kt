package com.alexgreece45.marvelx.domain.model_api.user.friendRequest

import com.alexgreece45.marvelx.core.network.model.api.user.incomingFriendRequest.ResponseIncomingFriendRequest
import com.alexgreece45.marvelx.util.DateUtil
import com.alexgreece45.marvelx.util.extensions.toFullName
import java.time.Duration
import java.time.Instant

data class IncomingFriendRequestDomain(
    val userId: String,
    val fullName: String,
    val profileImageUrl: String,
    val receivedAt: Instant
) {

    // TODO: Wrong duration is displayed. backend issue.
    val receivedAtFormatted: String = formatDuration(receivedAt, DateUtil.getUTCInstant())
}

fun List<ResponseIncomingFriendRequest>.toIncomingFriendRequestList(): List<IncomingFriendRequestDomain> =
    this.map { request ->
        IncomingFriendRequestDomain(
            userId = request.userId,
            fullName = Pair(request.firstName, request.lastName).toFullName(),
            profileImageUrl = request.profileImageUrl,
            receivedAt = Instant.parse(request.receivedAt)
        )
    }

private fun formatDuration(from: Instant, to: Instant): String {
    val duration = Duration.between(from, to)
    val seconds = duration.seconds

    val minute = 60
    val hour = 60 * minute
    val day = 24 * hour
    val week = 7 * day
    val month = 30 * day
    val year = 365 * day

    return when {
        seconds < minute -> "${seconds}s"
        seconds < hour -> "${seconds / minute}m"
        seconds < day -> "${seconds / hour}h"
        seconds < week -> "${seconds / day}d"
        seconds < month -> "${seconds / week}w"
        seconds < year -> "${seconds / month}mo"
        else -> "${seconds / year}y"
    }
}
