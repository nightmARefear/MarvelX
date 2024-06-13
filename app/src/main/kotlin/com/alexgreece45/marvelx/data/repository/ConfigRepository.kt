package com.alexgreece45.marvelx.data.repository

import com.alexgreece45.marvelx.data.repository.helper.RepositoryPartialState
import com.alexgreece45.marvelx.network_logic.api.ApiClient
import com.alexgreece45.marvelx.network_logic.models_api.faq.ResponseFaq
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface ConfigRepository {
    /**
     * Retrieves the FAQs.
     */
    suspend fun getFaqs(): Flow<RepositoryPartialState<ResponseFaq>>
}

class ConfigRepositoryImpl(
    private val apiClient: ApiClient
) : ConfigRepository {
    override suspend fun getFaqs(): Flow<RepositoryPartialState<ResponseFaq>> = flow {
        apiClient.getFaqs().let { response ->
            if (response.isSuccessful) {
                emit(RepositoryPartialState.Success(response.body()))
            } else {
                emit(RepositoryPartialState.Failed(response.errorBody().toString()))
            }
        }
    }
}