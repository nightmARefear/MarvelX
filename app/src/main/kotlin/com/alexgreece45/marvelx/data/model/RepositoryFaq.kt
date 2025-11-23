package com.alexgreece45.marvelx.data.model

import com.alexgreece45.marvelx.network_logic.models_api.faq.ResponseFaq
import com.alexgreece45.marvelx.network_logic.models_api.faq.ResponseFaqs

data class RepositoryFaq(
    val id: String,
    val title: String,
    val description: String,
    val order: Int
)

fun ResponseFaqs.toRepositoryFaqs(): List<RepositoryFaq> = this.faqs.map { it.toRepositoryFaq() }

private fun ResponseFaq.toRepositoryFaq(): RepositoryFaq =
    RepositoryFaq(
        id = id,
        title = title,
        description = description,
        order = order
    )
