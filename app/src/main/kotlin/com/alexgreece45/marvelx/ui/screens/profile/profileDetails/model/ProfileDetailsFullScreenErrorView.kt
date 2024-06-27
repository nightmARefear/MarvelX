package com.alexgreece45.marvelx.ui.screens.profile.profileDetails.model

import com.alexgreece45.marvelx.ui.components.model.ErrorButtonLayout
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.ProfileDetailsContract

/**
 * The full screen error view data for the `ProfileDetails` screen.
 */
data class ProfileDetailsFullScreenErrorView(
    val errorTitle: String,
    val errorSubtitle: String,
    val onCloseClicked: ProfileDetailsContract.Event,
    val errorButtonLayout: ErrorButtonLayout.Single
)
