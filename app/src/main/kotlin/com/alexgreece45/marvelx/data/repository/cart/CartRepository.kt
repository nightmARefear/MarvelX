package com.alexgreece45.marvelx.data.repository.cart

import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.data.helper.RepositoryResult
import com.alexgreece45.marvelx.core.data.helper.handleApiResultNoFlow
import com.alexgreece45.marvelx.core.data.helper.handleApiResultNoTransformationNoFlow
import com.alexgreece45.marvelx.core.data.model.marketplace.listing.ListingPreviewData
import com.alexgreece45.marvelx.core.data.model.marketplace.listing.toData
import com.alexgreece45.marvelx.core.network.clients.ListingClient
import com.alexgreece45.marvelx.data.model.cart.CartInfoData
import com.alexgreece45.marvelx.data.model.cart.CartSummaryData
import com.alexgreece45.marvelx.data.model.cart.toData
import org.koin.core.annotation.Single
import kotlin.coroutines.CoroutineContext

interface CartRepository {

    suspend fun getCartInfo(): RepositoryResult<CartInfoData>
    suspend fun getCartItems(): RepositoryResult<List<ListingPreviewData>>
    suspend fun getCartSummary(): RepositoryResult<CartSummaryData>
    suspend fun addCartItem(listingId: String): Boolean
    suspend fun deleteCartItem(listingId: String): Boolean
}

@Single
internal class CartRepositoryImpl(
    private val listingClient: ListingClient,
    @IoDispatcher private val ioDispatcher: CoroutineContext
) : CartRepository {

    override suspend fun getCartInfo(): RepositoryResult<CartInfoData> {
        return handleApiResultNoFlow(
            apiCall = listingClient::getCartInfo,
            transformation = { this.toData() },
            ioDispatcher = ioDispatcher
        )
    }

    override suspend fun getCartItems(): RepositoryResult<List<ListingPreviewData>> {
        return handleApiResultNoFlow(
            apiCall = listingClient::getCartItems,
            transformation = { this.map { it.toData() } },
            ioDispatcher = ioDispatcher
        )
    }

    override suspend fun getCartSummary(): RepositoryResult<CartSummaryData> {
        return handleApiResultNoFlow(
            apiCall = listingClient::getCartSummary,
            transformation = { this.toData() },
            ioDispatcher = ioDispatcher
        )
    }

    override suspend fun addCartItem(listingId: String): Boolean {
        return handleApiResultNoTransformationNoFlow(
            apiCall = { listingClient.addCartItem(listingId) },
            ioDispatcher = ioDispatcher
        ) is RepositoryResult.Success
    }

    override suspend fun deleteCartItem(listingId: String): Boolean {
        return handleApiResultNoTransformationNoFlow(
            apiCall = { listingClient.deleteCartItem(listingId) },
            ioDispatcher = ioDispatcher
        ) is RepositoryResult.Success
    }
}