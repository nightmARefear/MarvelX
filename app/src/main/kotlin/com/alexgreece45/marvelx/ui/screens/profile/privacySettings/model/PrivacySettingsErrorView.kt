package com.alexgreece45.marvelx.ui.screens.profile.privacySettings.model

import com.alexgreece45.marvelx.ui.screens.profile.privacySettings.PrivacySettingsEvent

/**
 * The error view data class for the `Privacy Settings` screen.
 */
data class PrivacySettingsErrorView(
    val errorSubtitle: String,
    val primaryButtonLabel: String,
    val event: PrivacySettingsEvent
)
