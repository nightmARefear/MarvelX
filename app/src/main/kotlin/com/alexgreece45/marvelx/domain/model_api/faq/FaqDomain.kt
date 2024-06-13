package com.alexgreece45.marvelx.domain.model_api.faq

import com.alexgreece45.marvelx.network_logic.models_api.faq.ResponseFaq
import com.alexgreece45.marvelx.network_logic.models_api.faq.ResponseFaqSection

data class FaqDomain(
    val sections: List<FaqSectionDomain>
)

data class FaqSectionDomain(
    val id: String,
    val title: String,
    val description: String,
    val order: Int
)

fun ResponseFaq.toFaqDomain(): FaqDomain =
    FaqDomain(sections = this.sections.map { it.toFaqSectionDomain()})

private fun ResponseFaqSection.toFaqSectionDomain(): FaqSectionDomain =
    FaqSectionDomain(
        id = this.id,
        title = this.title,
        description = this.description,
        order = this.order.toInt()
    )
