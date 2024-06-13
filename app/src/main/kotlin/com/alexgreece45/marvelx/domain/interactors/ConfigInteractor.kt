package com.alexgreece45.marvelx.domain.interactors

import com.alexgreece45.marvelx.data.repository.ConfigRepository
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.helper.handleRepositoryResponse
import com.alexgreece45.marvelx.domain.model_api.faq.FaqDomain
import com.alexgreece45.marvelx.domain.model_api.faq.toFaqDomain
import com.alexgreece45.marvelx.network_logic.models_api.faq.ResponseFaq
import com.alexgreece45.marvelx.util.ResourceProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface ConfigInteractor {
    /**
     * Retrieves the FAQs.
     */
    suspend fun getFaqs(): Flow<InteractorPartialState<FaqDomain>>
}

class ConfigInteractorImpl(
    private val configRepository: ConfigRepository,
    private val resourceProvider: ResourceProvider
) : ConfigInteractor {
    override suspend fun getFaqs(): Flow<InteractorPartialState<FaqDomain>> = flow {
        handleRepositoryResponse(
            response = configRepository.getFaqs(),
            transformation = ResponseFaq::toFaqDomain,
            genericErrorMessage = resourceProvider.getGenericErrorMessage()
        ).collect { interactorPartialState -> emit(interactorPartialState) }
    }
}