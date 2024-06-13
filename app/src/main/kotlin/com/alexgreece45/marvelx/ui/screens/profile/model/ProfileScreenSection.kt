package com.alexgreece45.marvelx.ui.screens.profile.model

import com.alexgreece45.marvelx.ui.components.IconData

/**
 * Defines a single section in the `Profile` screen.
 */
data class ProfileScreenSection(
    val title: String,
    val iconData: IconData,
    val onClick: () -> Unit
)