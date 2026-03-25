package com.alexgreece45.marvelx.data.repository.cart

import com.alexgreece45.marvelx.data.model.cart.CartInfoData
import com.alexgreece45.marvelx.data.model.cart.CartSummaryData
import com.alexgreece45.marvelx.data.model.cart.toData
import com.alexgreece45.marvelx.data.model.marketplace.listing.ListingPreviewData
import com.alexgreece45.marvelx.data.model.marketplace.listing.toData
import com.alexgreece45.marvelx.data.repository.helper.RepositoryResult
import com.alexgreece45.marvelx.data.repository.helper.handleApiResult
import com.alexgreece45.marvelx.data.repository.helper.handleApiResultNoTransformationWithData
import com.alexgreece45.marvelx.dispatchers.IoDispatcher
import com.alexgreece45.marvelx.network_logic.clients.ListingClient
import kotlinx.coroutines.flow.first
import kotlin.coroutines.CoroutineContext

interface CartRepository {

    suspend fun getCartInfo(): RepositoryResult<CartInfoData>
    suspend fun getCartItems(): RepositoryResult<List<ListingPreviewData>>
    suspend fun getCartSummary(): RepositoryResult<CartSummaryData>
    suspend fun addCartItem(listingId: String): Boolean
    suspend fun deleteCartItem(listingId: String): Boolean
}

class CartRepositoryImpl(
    private val listingClient: ListingClient,
    @IoDispatcher private val ioDispatcher: CoroutineContext
) : CartRepository {

    override suspend fun getCartInfo(): RepositoryResult<CartInfoData> {
        return handleApiResult(
            apiResult = listingClient.getCartInfo(),
            transformation = { this.toData() },
            ioDispatcher = ioDispatcher
        ).first()
    }

    override suspend fun getCartItems(): RepositoryResult<List<ListingPreviewData>> {
        return handleApiResult(
            apiResult = listingClient.getCartItems(),
            transformation = { this.map { it.toData() } },
            ioDispatcher = ioDispatcher
        ).first()
    }

    override suspend fun getCartSummary(): RepositoryResult<CartSummaryData> {
        return handleApiResult(
            apiResult = listingClient.getCartSummary(),
            transformation = { this.toData() },
            ioDispatcher = ioDispatcher
        ).first()
    }

    override suspend fun addCartItem(listingId: String): Boolean {
        return handleApiResultNoTransformationWithData(
            apiResult = listingClient.addCartItem(listingId),
            ioDispatcher = ioDispatcher
        ).first() is RepositoryResult.Success
    }

    override suspend fun deleteCartItem(listingId: String): Boolean {
        return handleApiResultNoTransformationWithData(
            apiResult = listingClient.deleteCartItem(listingId),
            ioDispatcher = ioDispatcher
        ).first() is RepositoryResult.Success
    }
}
