package com.alexgreece45.marvelx.ui.components

/**
 * Shared tags for [AlertDialogError]. Dialog buttons are built by many ViewModels, so the tags are
 * fixed per role rather than per screen; only one dialog is visible at a time.
 */
internal object AlertDialogTestTags {
    // AlertDialogButton passed as `confirmButton`
    const val CONFIRM_BUTTON = "alert_dialog_confirm_button"

    // AlertDialogButton passed as `dismissButton`
    const val DISMISS_BUTTON = "alert_dialog_dismiss_button"
}
