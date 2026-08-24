package com.alexgreece45.marvelx.data.repository.marketplace

import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.common.extensions.toImmutableListOrEmpty
import com.alexgreece45.marvelx.core.data.helper.RepositoryResult
import com.alexgreece45.marvelx.core.data.helper.handleApiResultNoFlow
import com.alexgreece45.marvelx.core.data.helper.handleApiResultNoTransformationNoFlow
import com.alexgreece45.marvelx.core.data.model.order.item.ReturnPolicy
import com.alexgreece45.marvelx.core.data.model.order.item.toData
import com.alexgreece45.marvelx.core.network.clients.ApiClient
import com.alexgreece45.marvelx.core.network.clients.ListingClient
import com.alexgreece45.marvelx.core.network.model.api.marketplace.ResponseMarketplace
import kotlinx.collections.immutable.ImmutableList
import org.koin.core.annotation.Single
import kotlin.coroutines.CoroutineContext

interface MarketplaceRepository {

    suspend fun getMarketplaceInfo(): RepositoryResult<ResponseMarketplace>

    suspend fun getReturnPolicies(): RepositoryResult<ImmutableList<ReturnPolicy>>
}

@Single
internal class MarketplaceRepositoryImpl(
    private val apiClient: ApiClient,
    private val listingClient: ListingClient,
    @IoDispatcher private val ioDispatcher: CoroutineContext
) : MarketplaceRepository {

    override suspend fun getMarketplaceInfo(): RepositoryResult<ResponseMarketplace> {
        return handleApiResultNoTransformationNoFlow(
            apiCall = listingClient::getMarketplaceInfo,
            ioDispatcher = ioDispatcher
        )
    }

    // TODO: Add cache support.
    override suspend fun getReturnPolicies(): RepositoryResult<ImmutableList<ReturnPolicy>> {
        return handleApiResultNoFlow(
            apiCall = apiClient::getReturnPolicies,
            transformation = { this.returnPolicies.map { it.toData() }.toImmutableListOrEmpty() },
            ioDispatcher = ioDispatcher
        )
    }
}