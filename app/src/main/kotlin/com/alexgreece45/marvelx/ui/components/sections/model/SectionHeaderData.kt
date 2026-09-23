package com.alexgreece45.marvelx.ui.components.sections.model

import com.alexgreece45.marvelx.ui.components.IconData

/**
 * Defines the data for a section header.
 *
 * @param title         The title of this section.
 * @param iconData      The icon data of this section.
 * @param isNavigable   Whether this section is navigable -- i.e., the user can navigate to another
 * screen. It has the default value of `true`.
 * @param testTag       The optional test tag of this section.
 * @param onClick       Callback to be invoked when this section is clicked.
 */
data class SectionHeaderData(
    val title: String,
    val iconData: IconData,
    val isNavigable: Boolean = true,
    val testTag: String? = null,
    val onClick: () -> Unit
)
