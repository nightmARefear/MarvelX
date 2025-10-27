package com.alexgreece45.marvelx.domain.model_api.user.block

import com.alexgreece45.marvelx.network_logic.models_api.user.block.ResponseBlockedUser
import com.alexgreece45.marvelx.util.extensions.toFullName
import java.time.Instant

data class BlockedUserDomain(
    val userId: String,
    val fullName: String,
    val profileImageUrl: String,
    val blockedAt: Instant
)

fun List<ResponseBlockedUser>.toBlockedUserDomainList(): List<BlockedUserDomain> =
    this.map { user ->
        BlockedUserDomain(
            userId = user.userId,
            fullName = Pair(user.firstName, user.lastName).toFullName(),
            profileImageUrl = user.profileImageUrl,
            blockedAt = Instant.parse(user.blockedAt)
        )
    }
