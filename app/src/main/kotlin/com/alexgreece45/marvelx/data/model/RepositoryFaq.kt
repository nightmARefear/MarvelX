package com.alexgreece45.marvelx.data.model

import com.alexgreece45.marvelx.network_logic.models_api.faq.ResponseFaq
import com.alexgreece45.marvelx.network_logic.models_api.faq.ResponseFaqSection

data class RepositoryFaq(
    val sections: List<RepositoryFaqSection>
)

data class RepositoryFaqSection(
    val id: String,
    val title: String,
    val description: String,
    val order: String
)

internal fun ResponseFaq.toRepositoryFaq(): RepositoryFaq =
    RepositoryFaq(
        sections = sections.map { it.toRepositoryFaqSection() }
    )

private fun ResponseFaqSection.toRepositoryFaqSection(): RepositoryFaqSection =
    RepositoryFaqSection(
        id = id,
        title = title,
        description = description,
        order = order
    )
