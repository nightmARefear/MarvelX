package com.alexgreece45.marvelx.network_logic.models_api.user.profileDetails

import com.alexgreece45.marvelx.network_logic.models_api.user.privacySettings.ResponsePrivacySettings
import com.google.gson.annotations.SerializedName

/**
 * The profile details of a user. If [isDifferentUser] is `true` then that means that the data is
 * for another user than the currently logged in one. It should be noted that [privacySettings]
 * __must__ be non-null in this case.
 *
 * @param isDifferentUser   Whether the fetched data is for the same user as the currently logged in
 * one. Depending on its value, the UI changes accordingly -- i.e., when the value is `true`, the
 * `Add friend` / `Remove friend` button is displayed, and when the value is `false` this button is
 * not visible, however, the `Edit Profile Details` toolbar button is visible.
 * @param privacySettings   The privacy settings of a user. If [isDifferentUser] is `true`, then
 * this field __must__ be non-null.
 * @param userStats         The statistics of a user such as the friends count, listings count, and
 * favorites count.
 */
data class ResponseProfileDetails(
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("email") val email: String,
    @SerializedName("profileImageUrl") val profileImageUrl: String,
    @SerializedName("isDifferentUser") val isDifferentUser: Boolean,
    @SerializedName("privacySettings") val privacySettings: ResponsePrivacySettings?,
    @SerializedName("userStats") val userStats: ResponseUserStats
)

/**
 * The statistics of a user such as the friends count, listings count, and favorites count.
 */
data class ResponseUserStats(
    @SerializedName("friendsCount") val friendsCount: Int,
    @SerializedName("listingsCount") val listingsCount: Int,
    @SerializedName("favoritesCount") val favoritesCount: Int,
)