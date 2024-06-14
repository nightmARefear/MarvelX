package com.alexgreece45.marvelx.domain.interactors

import com.alexgreece45.marvelx.data.model.RepositoryFaq
import com.alexgreece45.marvelx.data.repository.ConfigRepository
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.helper.handleRepositoryResult
import com.alexgreece45.marvelx.domain.model_api.faq.FaqDomain
import com.alexgreece45.marvelx.domain.model_api.faq.toFaqDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface ConfigInteractor {
    /**
     * Retrieves the FAQs.
     */
    suspend fun getFaqs(): Flow<InteractorPartialState<FaqDomain>>
}

class ConfigInteractorImpl(
    private val configRepository: ConfigRepository
) : ConfigInteractor {
    override suspend fun getFaqs(): Flow<InteractorPartialState<FaqDomain>> = flow {
        configRepository.getFaqs().collect {
            handleRepositoryResult(
                result = it,
                transformation = RepositoryFaq::toFaqDomain
            ).collect { interactorPartialState -> emit(interactorPartialState) }
        }
    }
}