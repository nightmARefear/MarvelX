package com.alexgreece45.marvelx.domain.model_api.faq

import com.alexgreece45.marvelx.data.model.RepositoryFaq
import com.alexgreece45.marvelx.data.model.RepositoryFaqSection

data class FaqDomain(
    val sections: List<FaqSectionDomain>
)

data class FaqSectionDomain(
    val id: String,
    val title: String,
    val description: String,
    val order: Int
)

internal fun RepositoryFaq.toFaqDomain(): FaqDomain =
    FaqDomain(sections = this.sections.map { it.toFaqSectionDomain() })

private fun RepositoryFaqSection.toFaqSectionDomain(): FaqSectionDomain =
    FaqSectionDomain(
        id = this.id,
        title = this.title,
        description = this.description,
        order = this.order.toInt()
    )
