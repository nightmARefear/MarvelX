package com.alexgreece45.marvelx.ui.screens.profile.profileDetails.model

import androidx.annotation.RawRes
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.ProfileDetailsContract

/**
 * The nested error view data for the `Profile Details` screen.
 */
data class ProfileDetailsNestedErrorView(
    val errorLabel: String,
    @RawRes val errorResourceId: Int,
    val buttonLabel: String,
    val onButtonClicked: ProfileDetailsContract.Event
)
