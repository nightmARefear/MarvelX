package com.alexgreece45.marvelx.tools

import com.alexgreece45.marvelx.core.network.model.api.faq.ResponseFaq
import com.alexgreece45.marvelx.core.network.model.api.faq.ResponseFaqs
import com.alexgreece45.marvelx.data.model.RepositoryFaq
import java.util.UUID
import kotlin.random.Random

object TestUtils {
    fun randomDbKey() = Random.nextLong(until = 10000L)

    fun randomId() = UUID.randomUUID().toString()
}

object FaqsTest {
    val responseFaqs = ResponseFaqs(
        faqs = listOf(
            ResponseFaq(
                id = "faq_1",
                title = "How do I sell a comic?",
                description = "Create a listing from the marketplace.",
                order = 1
            ),
            ResponseFaq(
                id = "faq_2",
                title = "How do I return an item?",
                description = "Open the order and request a return.",
                order = 2
            )
        )
    )

    val repositoryFaqs = listOf(
        RepositoryFaq(
            id = "faq_1",
            title = "How do I sell a comic?",
            description = "Create a listing from the marketplace.",
            order = 1
        ),
        RepositoryFaq(
            id = "faq_2",
            title = "How do I return an item?",
            description = "Open the order and request a return.",
            order = 2
        )
    )
}
