package com.alexgreece45.marvelx.domain.interactors.marketplace

import com.alexgreece45.marvelx.core.network.model.api.marketplace.ResponseMarketplace
import com.alexgreece45.marvelx.data.repository.marketplace.MarketplaceRepository
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.helper.handleRepositoryResultNoFlow
import com.alexgreece45.marvelx.domain.mappers.marketplace.toDomain
import com.alexgreece45.marvelx.domain.model_api.marketplace.MarketplaceDomain
import org.koin.core.annotation.Factory

interface MarketplaceInteractor {

    suspend fun getMarketplaceInfo(): InteractorPartialState<MarketplaceDomain>
}

@Factory
internal class MarketplaceInteractorImpl(
    private val marketplaceRepository: MarketplaceRepository
) : MarketplaceInteractor {

    override suspend fun getMarketplaceInfo(): InteractorPartialState<MarketplaceDomain> {
        return handleRepositoryResultNoFlow(
            repositoryResult = marketplaceRepository.getMarketplaceInfo(),
            transformation = ResponseMarketplace::toDomain
        )
    }
}