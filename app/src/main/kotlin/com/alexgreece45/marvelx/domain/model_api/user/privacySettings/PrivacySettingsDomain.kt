package com.alexgreece45.marvelx.domain.model_api.user.privacySettings

import com.alexgreece45.marvelx.data.model.RepositoryPrivacySettings
import com.alexgreece45.marvelx.network_logic.models_api.user.privacySettings.RequestPrivacySettings
import com.alexgreece45.marvelx.network_logic.models_api.user.privacySettings.ResponsePrivacySettings

/**
 * The privacy settings of a user.
 *
 * @param isFriendsListVisible      Whether the friends list is visible to other users.
 * @param isListingsListVisible     Whether the listings list is visible to other users.
 * @param isFavoritesListVisible    Whether the favorites list is visible to other users.
 * @param isGenderVisible           Whether the gender is visible to other users.
 * @param isEmailVisible            Whether the email is visible to other users.
 */
data class PrivacySettingsDomain(
    val isFriendsListVisible: Boolean,
    val isListingsListVisible: Boolean,
    val isFavoritesListVisible: Boolean,
    val isGenderVisible: Boolean,
    val isEmailVisible: Boolean
)

fun PrivacySettingsDomain.toRequestPrivacySettings(): RequestPrivacySettings =
    RequestPrivacySettings(
        isFriendsListVisible = this.isFriendsListVisible.toString(),
        isListingsListVisible = this.isListingsListVisible.toString(),
        isFavoritesListVisible = this.isFavoritesListVisible.toString(),
        isGenderVisible = this.isGenderVisible.toString(),
        isEmailVisible = this.isEmailVisible.toString(),
    )

fun ResponsePrivacySettings.toPrivacySettingsDomain(): PrivacySettingsDomain =
    PrivacySettingsDomain(
        isFriendsListVisible = this.isFriendsListVisible,
        isListingsListVisible = this.isListingsListVisible,
        isFavoritesListVisible = this.isFavoritesListVisible,
        isGenderVisible = this.isGenderVisible,
        isEmailVisible = this.isEmailVisible,
    )

fun RepositoryPrivacySettings.toPrivacySettingsDomain(): PrivacySettingsDomain =
    PrivacySettingsDomain(
        isFriendsListVisible = this.isFriendsListVisible,
        isListingsListVisible = this.isListingsListVisible,
        isFavoritesListVisible = this.isFavoritesListVisible,
        isGenderVisible = this.isGenderVisible,
        isEmailVisible = this.isEmailVisible,
    )
