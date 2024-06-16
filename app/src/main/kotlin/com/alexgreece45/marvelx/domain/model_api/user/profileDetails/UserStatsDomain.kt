package com.alexgreece45.marvelx.domain.model_api.user.profileDetails

import com.alexgreece45.marvelx.network_logic.models_api.user.profileDetails.ResponseUserStats
import com.alexgreece45.marvelx.util.extensions.formatNumberWithCommas

/**
 * The statistics of a user such as the friends count, listings count, and favorites count.
 */
data class UserStatsDomain(
    val friendsCount: String,
    val listingsCount: String,
    val favoritesCount: String,
)

internal fun ResponseUserStats.toUserStatsDomain(): UserStatsDomain =
    UserStatsDomain(
        friendsCount = this.friendsCount.formatNumberWithCommas(),
        listingsCount = this.listingsCount.formatNumberWithCommas(),
        favoritesCount = this.favoritesCount.formatNumberWithCommas()
    )