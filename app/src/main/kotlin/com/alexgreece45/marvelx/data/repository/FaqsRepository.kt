package com.alexgreece45.marvelx.data.repository

import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.data.helper.RepositoryResult
import com.alexgreece45.marvelx.core.data.helper.handleApiResultNoFlow
import com.alexgreece45.marvelx.core.network.clients.ApiClient
import com.alexgreece45.marvelx.core.network.model.api.faq.ResponseFaqs
import com.alexgreece45.marvelx.data.model.RepositoryFaq
import com.alexgreece45.marvelx.data.model.toRepositoryFaqs
import org.koin.core.annotation.Single
import kotlin.coroutines.CoroutineContext

interface FaqsRepository {

    /**
     * Retrieves the FAQs.
     */
    suspend fun getFaqs(): RepositoryResult<List<RepositoryFaq>>
}

@Single
internal class FaqsRepositoryImpl(
    private val apiClient: ApiClient,
    @IoDispatcher private val ioDispatcher: CoroutineContext
) : FaqsRepository {

    override suspend fun getFaqs(): RepositoryResult<List<RepositoryFaq>> {
        return handleApiResultNoFlow(
            apiCall = apiClient::getFaqs,
            transformation = ResponseFaqs::toRepositoryFaqs,
            ioDispatcher = ioDispatcher
        )
    }
}