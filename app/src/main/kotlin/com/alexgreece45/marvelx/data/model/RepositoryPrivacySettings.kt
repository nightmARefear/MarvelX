package com.alexgreece45.marvelx.data.model

import com.alexgreece45.marvelx.core.network.model.api.user.privacySettings.ResponsePrivacySettings

data class RepositoryPrivacySettings(
    val isFriendsListVisible: Boolean,
    val isListingsListVisible: Boolean,
    val isFavoritesListVisible: Boolean,
    val isGenderVisible: Boolean,
    val isEmailVisible: Boolean
)

fun com.alexgreece45.marvelx.core.network.model.api.user.privacySettings.ResponsePrivacySettings.toRepositoryPrivacySettings(): RepositoryPrivacySettings =
    RepositoryPrivacySettings(
        isFriendsListVisible = this.isFriendsListVisible,
        isListingsListVisible = this.isListingsListVisible,
        isFavoritesListVisible = this.isFavoritesListVisible,
        isEmailVisible = this.isEmailVisible,
        isGenderVisible = this.isGenderVisible,
    )
