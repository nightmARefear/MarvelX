package com.alexgreece45.marvelx.domain.model_api.user.profileDetails

import com.alexgreece45.marvelx.domain.model_api.user.privacySettings.PrivacySettingsDomain
import com.alexgreece45.marvelx.domain.model_api.user.privacySettings.toPrivacySettingsDomain
import com.alexgreece45.marvelx.network_logic.models_api.user.Gender
import com.alexgreece45.marvelx.network_logic.models_api.user.profileDetails.ResponseProfileDetails

/**
 * The profile details of a user. If [isDifferentUser] is `true` then that means that the data is
 * for another user than the currently logged in one. It should be noted that [relationshipStatus]
 * and [privacySettings] __must__ be non-null in this case.
 *
 * @param isDifferentUser   Whether the fetched data is for the same user as the currently logged in
 * one. Depending on its value, the UI changes accordingly -- i.e., when the value is `true`, the
 * `Add friend` / `Remove friend` button is displayed, and when the value is `false` this button is
 * not visible, however, the `Edit Profile Details` toolbar button is visible.
 * @param relationshipStatus The relationship status between the currently logged in user and the
 * fetched user. This value __must not__ be `null` if [isDifferentUser] is `true`.
 * @param privacySettings   The privacy settings of a user. If [isDifferentUser] is `true`, then
 * this field __must__ be non-null.
 * @param userStats         The statistics of a user such as the friends count, listings count, and
 * favorites count.
 */
data class ProfileDetailsDomain(
    val basicProfileDetails: BasicProfileDetailsDomain,
    val isDifferentUser: Boolean,
    val relationshipStatus: RelationshipStatusDomain?,
    val privacySettings: PrivacySettingsDomain?,
    val userStats: UserStatsDomain
) {
    companion object {
        /**
         * Mock data representing the same user.
         */
        fun mockSameUser(): ProfileDetailsDomain = ProfileDetailsDomain(
            BasicProfileDetailsDomain(
                firstName = "Alexandros",
                lastName = "Voliotis",
                gender = Gender.Male,
                email = "alexandrosv@domain.com",
                profileImageUrl = "alexandros_voliotis_profile_image.jpeg",
                fullName = "Alexandros Voliotis"
            ),
            isDifferentUser = false,
            relationshipStatus = null,
            privacySettings = PrivacySettingsDomain(
                isFriendsListVisible = true,
                isListingsListVisible = true,
                isFavoritesListVisible = false,
                isGenderVisible = true,
                isEmailVisible = false
            ),
            userStats = UserStatsDomain(
                friendsCount = 3002, listingsCount = 100, favoritesCount = 10215
            )
        )

        /**
         * Mock data representing another user.
         */
        fun mockAnotherUser(): ProfileDetailsDomain = ProfileDetailsDomain(
            BasicProfileDetailsDomain(
                firstName = "Jill",
                lastName = "Doe",
                gender = Gender.Female,
                email = "jill.d@domain.com",
                profileImageUrl = "jill_d_profile_image.jpg",
                fullName = "Jill Doe"
            ),
            isDifferentUser = true,
            relationshipStatus = RelationshipStatusDomain.None,
            privacySettings = PrivacySettingsDomain(
                isFriendsListVisible = true,
                isListingsListVisible = true,
                isFavoritesListVisible = false,
                isGenderVisible = true,
                isEmailVisible = false
            ),
            userStats = UserStatsDomain(
                friendsCount = 20, listingsCount = 1006, favoritesCount = 1092
            )
        )
    }
}

/**
 * Converts this instance to [ProfileDetailsDomain]. If [ResponseProfileDetails.isDifferentUser] is
 * `true` then that means that the response data is for another user than the currently logged in
 * one, and due to privacy settings being in place, the gender field can be hidden in the UI.
 *
 * In the case that `privacySettings.isGenderVisible` is set to `false` then the server will send an
 * empty [String] which will cause [Gender.of] to throw an exception, so special handling is applied;
 * more specifically, the gender is arbitrarily set to [Gender.Other] even if that's not the user's
 * actual gender. The gender is anyways hidden in the UI. The only occasions that the gender
 * matters in the sense of being displayed in the UI, is when [ResponseProfileDetails.isDifferentUser]
 * is set to `false` or when [ResponseProfileDetails.privacySettings] is not `null` and
 * `privacySettings.isGenderVisible` is set to `true`.
 */
fun ResponseProfileDetails.toProfileDetailsDomain(): ProfileDetailsDomain =
    ProfileDetailsDomain(
        basicProfileDetails = this.basicProfileDetails.toBasicProfileDetailsDomain(isGenderVisible = !isDifferentUser || this.privacySettings?.isGenderVisible == true),
        isDifferentUser = this.isDifferentUser,
        relationshipStatus = if (this.isDifferentUser) RelationshipStatusDomain.fromKey(this.relationshipStatus.orEmpty()) else null,
        privacySettings = if (this.isDifferentUser) this.privacySettings?.toPrivacySettingsDomain() else null,
        userStats = this.userStats.toUserStatsDomain()
    )
