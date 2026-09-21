package com.alexgreece45.marvelx.ui.screens.profile.profileDetails.stats.model

import androidx.annotation.StringRes
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.core.network.model.api.user.friend.ResponseFriend
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingPreviewDomain
import com.alexgreece45.marvelx.domain.model_api.myListing.MyListingDomain

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

fun ListingPreviewDomain.toProfileDetailsStatsItem(): ProfileDetailsStatsItem =
    ProfileDetailsStatsItem(
        id = listingId,
        title = title,
        imageUrl = coverImageUrl,
        contentDescription = R.string.screen_profile_details_stats_content_description_listing_image
    )

fun MyListingDomain.toProfileDetailsStatsItem(): ProfileDetailsStatsItem =
    ProfileDetailsStatsItem(
        id = listingId,
        title = title,
        imageUrl = coverImageUrl,
        contentDescription = R.string.screen_profile_details_stats_content_description_listing_image
    )
