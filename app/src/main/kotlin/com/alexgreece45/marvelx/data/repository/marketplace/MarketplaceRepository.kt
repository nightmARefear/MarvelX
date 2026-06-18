package com.alexgreece45.marvelx.data.repository.marketplace

import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.data.helper.RepositoryResult
import com.alexgreece45.marvelx.core.data.helper.handleApiResultNoTransformationWithData
import com.alexgreece45.marvelx.core.network.clients.ListingClient
import com.alexgreece45.marvelx.core.network.model.api.marketplace.ResponseMarketplace
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Single
import kotlin.coroutines.CoroutineContext

interface MarketplaceRepository {

    suspend fun getMarketplaceInfo(): RepositoryResult<ResponseMarketplace>
}

@Single
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