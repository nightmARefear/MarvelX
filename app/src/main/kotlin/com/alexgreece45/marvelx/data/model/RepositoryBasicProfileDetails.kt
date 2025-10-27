package com.alexgreece45.marvelx.data.model

import com.alexgreece45.marvelx.network_logic.models_api.user.profileDetails.ResponseBasicProfileDetails
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
data class RepositoryBasicProfileDetails(
    val firstName: String,
    val lastName: String,
    val gender: String,
    val email: String,
    val profileImageUrl: String?,
)

/**
 * Converts this instance to [RepositoryBasicProfileDetails].
 */
internal fun ResponseBasicProfileDetails.toRepositoryBasicProfileDetails(): RepositoryBasicProfileDetails =
    RepositoryBasicProfileDetails(
        firstName = this.firstName,
        lastName = this.lastName,
        gender = this.gender,
        email = this.email,
        profileImageUrl = this.profileImageUrl
    )