package com.alexgreece45.marvelx.ui.screens.settings.faq

internal object FaqTestTags {
    // FaqContract.Event.OnBackClicked
    const val BACK_BUTTON = "faq_back_button"

    /**
     * FaqContract.Event.OnSectionClicked; one tag per section, keyed by `FaqUi.id`
     * (e.g. `faq_section_42_header`).
     */
    fun sectionHeader(sectionId: String) = "faq_section_${sectionId}_header"
}
