package com.alexgreece45.marvelx.domain.model_api.user.profileDetails

import com.alexgreece45.marvelx.domain.model_api.user.privacySettings.PrivacySettingsDomain
import com.alexgreece45.marvelx.domain.model_api.user.privacySettings.toPrivacySettingsDomain
import com.alexgreece45.marvelx.network_logic.models_api.user.Gender
import com.alexgreece45.marvelx.network_logic.models_api.user.profileDetails.ResponseProfileDetails

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
data class ProfileDetailsDomain(
    val firstName: String,
    val lastName: String,
    val gender: Gender,
    val email: String,
    val profileImageUrl: String,
    val fullName: String = "$firstName $lastName",
    val isDifferentUser: Boolean,
    val privacySettings: PrivacySettingsDomain?,
    val userStats: UserStatsDomain
) {
    companion object {
        /**
         * Returns mock data. Useful for previews and testing.
         */
        fun mock(): ProfileDetailsDomain = ProfileDetailsDomain(
            firstName = "Alexandros",
            lastName = "Voliotis",
            gender = Gender.Male,
            email = "alexandrosv@domain.com",
            profileImageUrl = "alexandros_voliotis_profile_image.jpeg",
            isDifferentUser = false,
            privacySettings = PrivacySettingsDomain(
                isFriendsListVisible = true,
                isListingsListVisible = true,
                isFavoritesListVisible = false,
                isGenderVisible = true,
                isEmailVisible = false
            ),
            userStats = UserStatsDomain(
                friendsCount = "3,002", listingsCount = "100", favoritesCount = "10.2K"
            )
        )
    }
}

/**
 * Converts this instance to [ProfileDetailsDomain]. If [ResponseProfileDetails.privacySettings] is
 * not `null` then that means that the response data is for another user than the currently logged
 * in one, and due to privacy settings being in place, the gender field can be omitted in the UI.
 * In that case, the server will send an empty [String] which will cause [Gender.of] to throw an
 * exception, so special handling is applied; more specifically, the gender is arbitrarily set to
 * [Gender.Other] even if that's not the user's actual gender. The gender is anyways disregarded in
 * the UI. The only time that the gender matters, is when [ResponseProfileDetails.privacySettings]
 * is set to `null`.
 */
fun ResponseProfileDetails.toProfileDetailsDomain(): ProfileDetailsDomain =
    ProfileDetailsDomain(
        firstName = this.firstName,
        lastName = this.lastName,
        gender = if (this.privacySettings == null) Gender.of(this.gender) else Gender.Other,
        email = this.email,
        profileImageUrl = this.profileImageUrl,
        fullName = "${this.firstName} ${this.lastName}",
        isDifferentUser = this.isDifferentUser,
        privacySettings = this.privacySettings?.toPrivacySettingsDomain(),
        userStats = this.userStats.toUserStatsDomain()
    )
