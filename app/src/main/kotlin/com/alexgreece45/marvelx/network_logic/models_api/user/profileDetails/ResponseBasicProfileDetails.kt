package com.alexgreece45.marvelx.network_logic.models_api.user.profileDetails

import com.google.gson.annotations.SerializedName

/**
 * The basic profile details of a user.
 *
 * @param firstName         The first name.
 * @param lastName          The last name.
 * @param gender            The gender.
 * @param email             The email.
 * @param profileImageUrl   The URL of the profile image.
 */
data class ResponseBasicProfileDetails(
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("email") val email: String,
    @SerializedName("profileImageUrl") val profileImageUrl: String?,
)