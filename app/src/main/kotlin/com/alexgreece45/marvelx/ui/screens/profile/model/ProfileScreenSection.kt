package com.alexgreece45.marvelx.ui.screens.profile.model

import com.alexgreece45.marvelx.ui.components.IconData

/**
 * Defines a single section in the `Profile` screen.
 *
 * @param title         The title of this section.
 * @param iconData      The icon data of this section.
 * @param isNavigable   Whether this section is navigable -- i.e., the user can navigate to another
 * screen. It has the default value of `true`.
 * @param onClick       Callback to be invoked when this section is clicked.
 */
data class ProfileScreenSection(
    val title: String,
    val iconData: IconData,
    val isNavigable: Boolean = true,
    val onClick: () -> Unit
)
