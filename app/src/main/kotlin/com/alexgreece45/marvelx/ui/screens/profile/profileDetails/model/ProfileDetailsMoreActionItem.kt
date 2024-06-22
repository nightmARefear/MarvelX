package com.alexgreece45.marvelx.ui.screens.profile.profileDetails.model

import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.ProfileDetailsContract

/**
 * Defines a single item in the more actions bottom sheet of the `Profile Details` screen.
 *
 * @param label                 The label of this item.
 * @param isRestrictiveAction   Whether this item performs a restrictive action such as blocking
 * the user.
 * @param event                 [ProfileDetailsContract.Event] to execute when this item is clicked.
 */
data class ProfileDetailsMoreActionItem(
    val label: String,
    val isRestrictiveAction: Boolean = false,
    val event: ProfileDetailsContract.Event.MoreActions
)