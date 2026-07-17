package com.alexgreece45.marvelx.data.model.profileDetails

import com.alexgreece45.marvelx.core.network.model.api.user.profileDetails.ResponseBasicProfileDetails
import com.alexgreece45.marvelx.util.extensions.toFullName

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

    // Reserved for the future if needed.
//    val gender: String,
//    val email: String,
    val profileImageUrl: String
) {

    // TODO: Should be moved to UI layer.
    val fullName: String get() = Pair(firstName, lastName).toFullName()
}

/**
 * Converts this instance to [RepositoryBasicProfileDetails].
 */
internal fun com.alexgreece45.marvelx.core.network.model.api.user.profileDetails.ResponseBasicProfileDetails.toData(): RepositoryBasicProfileDetails =
    RepositoryBasicProfileDetails(
        firstName = firstName,
        lastName = lastName,
        profileImageUrl = profileImageUrl.orEmpty()
    )
