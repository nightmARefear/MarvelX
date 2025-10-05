package com.alexgreece45.marvelx.ui.screens.friends.model

import com.alexgreece45.marvelx.network_logic.models_api.user.incomingFriendRequest.ResponseIncomingFriendRequest

data class IncomingFriendRequestDomain(
    val userId: String,
    val firstName: String,
    val lastName: String,
    val profileImageUrl: String,
    val receivedAt: Long
)

fun List<ResponseIncomingFriendRequest>.toIncomingFriendRequestList(): List<IncomingFriendRequestDomain> =
    this.map { request ->
        IncomingFriendRequestDomain(
            userId = request.userId,
            firstName = request.firstName,
            lastName = request.lastName,
            profileImageUrl = request.profileImageUrl,
            receivedAt = request.receivedAt
        )
    }
