package com.alexgreece45.marvelx.ui.components.error

/**
 * Shared tags for [ContentErrorScreen]. The screen is displayed on top of the erroneous screen, so
 * the tags are fixed per role rather than per screen.
 */
internal object ContentErrorTestTags {
    // `onClose` toolbar button
    const val CLOSE_BUTTON = "content_error_close_button"

    // ErrorButtonLayout.onPrimaryButtonClicked
    const val PRIMARY_BUTTON = "content_error_primary_button"

    // ErrorButtonLayout.Double.onSecondaryButtonClicked
    const val SECONDARY_BUTTON = "content_error_secondary_button"
}
