package com.alexgreece45.marvelx.ui.screens.profile.profileDetails.model

import com.alexgreece45.marvelx.ui.components.AlertDialogButton

/**
 * Alert dialog error data for the `profile details` screen.
 */
data class ProfileDetailsAlertDialogErrorData(
    val errorTitle: String,
    val errorSubtitle: String,
    val dismissAlertDialogButton: AlertDialogButton? = null,
    val confirmAlertDialogButton: AlertDialogButton
)
