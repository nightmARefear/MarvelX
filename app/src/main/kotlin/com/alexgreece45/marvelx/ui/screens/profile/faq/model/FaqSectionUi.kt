package com.alexgreece45.marvelx.ui.screens.profile.faq.model

import com.alexgreece45.marvelx.domain.model_api.faq.FaqSectionDomain

/**
 * The UI model of [FaqSectionDomain].
 *
 * @param id            The unique ID of this section.
 * @param title         The title of this section.
 * @param description   The description of this section.
 * @param order         The order of this section.
 * @param isExpanded    Whether this section is expanded. It has the default value of `false`.
 */
data class FaqSectionUi(
    val id: String,
    val title: String,
    val description: String,
    val order: Int,
    val isExpanded: Boolean = false
)

fun List<FaqSectionDomain>.toFaqSectionUiList(): List<FaqSectionUi> =
    this.map { it.toFaqSectionUi() }

private fun FaqSectionDomain.toFaqSectionUi(): FaqSectionUi =
    FaqSectionUi(
        id = this.id,
        title = "${this.order}. ${this.title}",
        description = this.description,
        order = this.order
    )