package com.alexgreece45.marvelx.ui.screens.settings.privacySettings.model

import com.alexgreece45.marvelx.ui.screens.settings.privacySettings.PrivacySettingsEvent

/**
 * The error view data class for the `Privacy Settings` screen.
 */
data class PrivacySettingsErrorView(
    val errorTitle: String,
    val errorSubtitle: String,
    val primaryButtonLabel: String,
    val event: PrivacySettingsEvent
)
