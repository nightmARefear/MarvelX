package com.alexgreece45.marvelx.ui.screens.profile.model

import com.alexgreece45.marvelx.ui.components.AlertDialogButton

/**
 * The data class for the error dialog of the `Profile` screen.
 */
data class ProfileErrorAlertDialog(
    val errorTitle: String,
    val errorSubtitle: String,
    val alertDialogButtonNegative: AlertDialogButton,
    val alertDialogButtonPositive: AlertDialogButton
)
