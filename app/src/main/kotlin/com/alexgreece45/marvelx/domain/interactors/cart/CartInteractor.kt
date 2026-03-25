package com.alexgreece45.marvelx.domain.interactors.cart

import com.alexgreece45.marvelx.data.repository.cart.CartRepository
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.helper.handleRepositoryResultNoFlow
import com.alexgreece45.marvelx.domain.mappers.cart.CartSummaryDomainMapper
import com.alexgreece45.marvelx.domain.model_api.cart.CartInfoDomain
import com.alexgreece45.marvelx.domain.model_api.cart.CartSummaryDomain
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingPreviewDomain
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.toDomain

interface CartInteractor {

    suspend fun getCartInfo(): InteractorPartialState<CartInfoDomain>
    suspend fun getCartItems(): InteractorPartialState<List<ListingPreviewDomain>>
    suspend fun getCartSummary(): InteractorPartialState<CartSummaryDomain>
    suspend fun addCartItem(listingId: String): Boolean
    suspend fun deleteCartItem(listingId: String): Boolean
}

class CartInteractorImpl(
    private val cartRepository: CartRepository,
    private val cartSummaryDomainMapper: CartSummaryDomainMapper
) : CartInteractor {

    override suspend fun getCartInfo(): InteractorPartialState<CartInfoDomain> {
        return handleRepositoryResultNoFlow(
            repositoryResult = cartRepository.getCartInfo(),
            transformation = {
                CartInfoDomain(
                    items = this.items.map { it.toDomain() },
                    summary = cartSummaryDomainMapper.toDomain(this.summary)
                )
            }
        )
    }

    override suspend fun getCartItems(): InteractorPartialState<List<ListingPreviewDomain>> {
        return handleRepositoryResultNoFlow(
            repositoryResult = cartRepository.getCartItems(),
            transformation = { this.map { it.toDomain() } }
        )
    }

    override suspend fun getCartSummary(): InteractorPartialState<CartSummaryDomain> {
        return handleRepositoryResultNoFlow(
            repositoryResult = cartRepository.getCartSummary(),
            transformation = { cartSummaryDomainMapper.toDomain(this) }
        )
    }

    override suspend fun addCartItem(listingId: String): Boolean {
        return cartRepository.addCartItem(listingId)
    }

    override suspend fun deleteCartItem(listingId: String): Boolean {
        return cartRepository.deleteCartItem(listingId)
    }
}
