package com.alexgreece45.marvelx.data.model

import com.alexgreece45.marvelx.network_logic.models_api.user.privacySettings.ResponsePrivacySettings

data class RepositoryPrivacySettings(
    val isGenderVisible: Boolean,
    val isEmailVisible: Boolean
)

fun ResponsePrivacySettings.toRepositoryPrivacySettings(): RepositoryPrivacySettings =
    RepositoryPrivacySettings(
        isGenderVisible = isGenderVisible,
        isEmailVisible = isEmailVisible
    )
