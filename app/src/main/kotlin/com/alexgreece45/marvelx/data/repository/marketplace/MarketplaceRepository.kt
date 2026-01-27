package com.alexgreece45.marvelx.data.repository.marketplace

import com.alexgreece45.marvelx.data.repository.helper.RepositoryResult
import com.alexgreece45.marvelx.data.repository.helper.handleApiResultNoTransformationWithData
import com.alexgreece45.marvelx.dispatchers.IoDispatcher
import com.alexgreece45.marvelx.network_logic.clients.ListingClient
import com.alexgreece45.marvelx.network_logic.models_api.marketplace.ResponseMarketplace
import kotlinx.coroutines.flow.first
import kotlin.coroutines.CoroutineContext

interface MarketplaceRepository {

    suspend fun getMarketplaceInfo(): RepositoryResult<ResponseMarketplace>
}

class MarketplaceRepositoryImpl(
    private val listingClient: ListingClient,
    @IoDispatcher private val ioDispatcher: CoroutineContext
) : MarketplaceRepository {

    override suspend fun getMarketplaceInfo(): RepositoryResult<ResponseMarketplace> {
        return handleApiResultNoTransformationWithData(
            apiResult = listingClient.getMarketplaceInfo(),
            ioDispatcher = ioDispatcher
        ).first()
    }
}
