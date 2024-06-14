package com.alexgreece45.marvelx.data.repository

import com.alexgreece45.marvelx.data.model.RepositoryFaq
import com.alexgreece45.marvelx.data.model.toRepositoryFaq
import com.alexgreece45.marvelx.data.repository.helper.RepositoryResult
import com.alexgreece45.marvelx.data.repository.helper.handleApiResult
import com.alexgreece45.marvelx.network_logic.api.ApiClient
import com.alexgreece45.marvelx.network_logic.models_api.faq.ResponseFaq
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface ConfigRepository {
    /**
     * Retrieves the FAQs.
     */
    suspend fun getFaqs(): Flow<RepositoryResult<RepositoryFaq>>
}

class ConfigRepositoryImpl(
    private val apiClient: ApiClient
) : ConfigRepository {
    override suspend fun getFaqs(): Flow<RepositoryResult<RepositoryFaq>> = flow {
        handleApiResult(apiClient.getFaqs(), ResponseFaq::toRepositoryFaq).collect {
            emit(it)
        }
    }
}