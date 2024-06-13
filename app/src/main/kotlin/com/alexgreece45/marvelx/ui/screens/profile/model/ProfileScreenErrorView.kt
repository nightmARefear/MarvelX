package com.alexgreece45.marvelx.ui.screens.profile.model

import com.alexgreece45.marvelx.ui.screens.profile.ProfileEvent

/**
 * Defines the error view for the `Profile` screen.
 */
data class ProfileScreenErrorView(
    val errorMessage: String,
    val profileEvent: ProfileEvent
)