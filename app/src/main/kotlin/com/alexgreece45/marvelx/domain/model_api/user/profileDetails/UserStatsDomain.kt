package com.alexgreece45.marvelx.domain.model_api.user.profileDetails

import com.alexgreece45.marvelx.core.network.model.api.user.profileDetails.ResponseUserStats
import com.alexgreece45.marvelx.util.extensions.formatNumberWithCommas

/**
 * The statistics of a user such as the friends count, listings count, and favorites count.
 */
data class UserStatsDomain(
    val friendsCount: Int,
    val listingsCount: Int,
    val favoritesCount: Int
) {

    val friendsCountFormatted: String get() = friendsCount.formatNumberWithCommas()
    val listingsCountFormatted: String get() = listingsCount.formatNumberWithCommas()
    val favoritesCountFormatted: String get() = favoritesCount.formatNumberWithCommas()
}

internal fun ResponseUserStats.toUserStatsDomain(): UserStatsDomain =
    UserStatsDomain(
        friendsCount = this.friendsCount,
        listingsCount = this.listingsCount,
        favoritesCount = this.favoritesCount
    )
