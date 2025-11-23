package com.alexgreece45.marvelx.data.repository

import com.alexgreece45.marvelx.data.model.RepositoryFaq
import com.alexgreece45.marvelx.data.model.toRepositoryFaqs
import com.alexgreece45.marvelx.data.repository.helper.RepositoryResult
import com.alexgreece45.marvelx.data.repository.helper.handleApiResult
import com.alexgreece45.marvelx.dispatchers.IoDispatcher
import com.alexgreece45.marvelx.network_logic.clients.ApiClient
import com.alexgreece45.marvelx.network_logic.models_api.faq.ResponseFaqs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.CoroutineContext

interface FaqsRepository {

    /**
     * Retrieves the FAQs.
     */
    suspend fun getFaqs(): Flow<RepositoryResult<List<RepositoryFaq>>>
}

class FaqsRepositoryImpl(
    private val apiClient: ApiClient,
    @IoDispatcher private val ioDispatcher: CoroutineContext
) : FaqsRepository {

    override suspend fun getFaqs(): Flow<RepositoryResult<List<RepositoryFaq>>> = flow {
        emitAll(
            handleApiResult(
                apiResult = apiClient.getFaqs(),
                transformation = ResponseFaqs::toRepositoryFaqs,
                ioDispatcher = ioDispatcher
            )
        )
    }
}
