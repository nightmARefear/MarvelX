package com.alexgreece45.marvelx.ui.screens.profile.profileDetails.stats.model

import androidx.annotation.StringRes
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.core.network.model.api.user.friend.ResponseFriend

data class ProfileDetailsStatsItem(
    val id: String,
    val title: String,
    val imageUrl: String,
    @StringRes val contentDescription: Int
)

fun ResponseFriend.toProfileDetailsStatsItem(): ProfileDetailsStatsItem =
    ProfileDetailsStatsItem(
        id = userId,
        title = "$firstName $lastName",
        imageUrl = profileImageUrl,
        contentDescription = R.string.screen_profile_details_stats_content_description_profile_image
    )
