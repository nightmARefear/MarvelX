package com.alexgreece45.marvelx.ui.screens.settings.faq.model

import com.alexgreece45.marvelx.data.model.RepositoryFaq

/**
 * The UI model of [RepositoryFaq].
 *
 * @param id            The unique ID of this section.
 * @param title         The title of this section.
 * @param description   The description of this section.
 * @param order         The order of this section.
 * @param isExpanded    Whether this section is expanded. It has the default value of `false`.
 */
data class FaqUi(
    val id: String,
    val title: String,
    val description: String,
    val order: Int,
    val isExpanded: Boolean = false
)

fun RepositoryFaq.toFaqUi(): FaqUi =
    FaqUi(
        id = this.id,
        title = "${this.order}. ${this.title}",
        description = this.description,
        order = this.order
    )
