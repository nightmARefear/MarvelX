package com.alexgreece45.marvelx.domain.model_api.user.privacySettings

import com.alexgreece45.marvelx.data.model.RepositoryPrivacySettings

data class PrivacySettingsDomain(
    val isGenderVisible: Boolean,
    val isEmailVisible: Boolean
)

fun RepositoryPrivacySettings.toPrivacySettingsDomain(): PrivacySettingsDomain =
    PrivacySettingsDomain(
        isGenderVisible = isGenderVisible,
        isEmailVisible = isEmailVisible
    )