package com.alexgreece45.marvelx.data.model

import com.alexgreece45.marvelx.core.network.model.api.faq.ResponseFaq
import com.alexgreece45.marvelx.core.network.model.api.faq.ResponseFaqs

data class RepositoryFaq(
    val id: String,
    val title: String,
    val description: String,
    val order: Int
)

fun com.alexgreece45.marvelx.core.network.model.api.faq.ResponseFaqs.toRepositoryFaqs(): List<RepositoryFaq> = this.faqs.map { it.toRepositoryFaq() }

private fun com.alexgreece45.marvelx.core.network.model.api.faq.ResponseFaq.toRepositoryFaq(): RepositoryFaq =
    RepositoryFaq(
        id = id,
        title = title,
        description = description,
        order = order
    )
