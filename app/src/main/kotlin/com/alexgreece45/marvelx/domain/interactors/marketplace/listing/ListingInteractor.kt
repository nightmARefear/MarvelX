package com.alexgreece45.marvelx.domain.interactors.marketplace.listing

import com.alexgreece45.marvelx.configuration.ConfigPriceLogic
import com.alexgreece45.marvelx.core.common.extensions.formatPrice
import com.alexgreece45.marvelx.core.data.helper.multi_part_builder.model.MultipartResult
import com.alexgreece45.marvelx.core.data.model.marketplace.ListingSort
import com.alexgreece45.marvelx.core.data.repositories.marketplace.listing.ListingRepository
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.helper.handleRepositoryResultNoFlow
import com.alexgreece45.marvelx.domain.mappers.marketplace.listing.ListingDomainMapper
import com.alexgreece45.marvelx.domain.mappers.marketplace.listing.details.ListingDetailsResponseMapper
import com.alexgreece45.marvelx.domain.mappers.marketplace.listing.toRequest
import com.alexgreece45.marvelx.domain.model_api.marketplace.MarketplaceSearchCriteriaDomain
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.CreateListingDomain
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingDomain
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingPreviewDomain
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.UpdateListingDomain
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.toData
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.toDomain
import com.alexgreece45.marvelx.domain.model_api.marketplace.toData
import com.alexgreece45.marvelx.domain.model_api.myListing.MyListingDomain
import com.alexgreece45.marvelx.domain.model_api.myListing.toDomain
import org.koin.core.annotation.Factory

interface ListingInteractor {

    suspend fun getAllListingsByCategory(categoryCode: String): InteractorPartialState<List<ListingDomain>>

    suspend fun getCurrentUserListings(page: Int): InteractorPartialState<List<MyListingDomain>>

    suspend fun getUserListings(
        userId: String,
        page: Int
    ): InteractorPartialState<List<ListingPreviewDomain>>

    suspend fun getListing(listingId: String): InteractorPartialState<ListingDomain>

    suspend fun getFilteredListings(
        searchQuery: String?,
        searchCriteria: MarketplaceSearchCriteriaDomain,
        sort: ListingSort
    ): InteractorPartialState<List<ListingDomain>>

    suspend fun createListing(createListingDomain: CreateListingDomain): MultipartResult<String>
    suspend fun updateListing(
        listingId: String,
        updateListingDomain: UpdateListingDomain
    ): Boolean

    suspend fun deleteListing(listingId: String): Boolean

    suspend fun addToFavorites(listingId: String): Boolean
    suspend fun removeFromFavorites(listingId: String): Boolean

    suspend fun getFavoriteListings(): InteractorPartialState<List<ListingPreviewDomain>>
}

@Factory
internal class ListingInteractorImpl(
    private val listingRepository: ListingRepository,
    private val listingDomainMapper: ListingDomainMapper,
    private val configPriceLogic: ConfigPriceLogic,
    private val listingDetailsResponseMapper: ListingDetailsResponseMapper,
) : ListingInteractor {

    override suspend fun getAllListingsByCategory(categoryCode: String): InteractorPartialState<List<ListingDomain>> {
        return handleRepositoryResultNoFlow(
            repositoryResult = listingRepository.getAllListingsByCategory(categoryCode),
            transformation = { this.map { listingDomainMapper.toDomain(it) } }
        )
    }

    override suspend fun getCurrentUserListings(page: Int): InteractorPartialState<List<MyListingDomain>> {
        return handleRepositoryResultNoFlow(
            repositoryResult = listingRepository.getCurrentUserListings(page),
            transformation = { this.map { it.toDomain() } }
        )
    }

    override suspend fun getUserListings(
        userId: String,
        page: Int
    ): InteractorPartialState<List<ListingPreviewDomain>> {
        return handleRepositoryResultNoFlow(
            repositoryResult = listingRepository.getUserListings(userId, page),
            transformation = { this.map { it.toDomain() } }
        )
    }

    override suspend fun getListing(listingId: String): InteractorPartialState<ListingDomain> {
        return handleRepositoryResultNoFlow(
            repositoryResult = listingRepository.getListing(listingId),
            transformation = listingDomainMapper::toDomain
        )
    }

    override suspend fun getFilteredListings(
        searchQuery: String?,
        searchCriteria: MarketplaceSearchCriteriaDomain,
        sort: ListingSort
    ): InteractorPartialState<List<ListingDomain>> {
        return handleRepositoryResultNoFlow(
            repositoryResult = listingRepository.getFilteredListings(
                searchQuery = searchQuery,
                searchCriteria = searchCriteria.toData(
                    formatPrice = {
                        it?.formatPrice(
                            scale = configPriceLogic.priceScale,
                            roundingMode = configPriceLogic.roundingMode
                        )
                    }
                ),
                sort = sort
            ),
            transformation = { this.map { listingDomainMapper.toDomain(it) } }
        )
    }

    override suspend fun createListing(createListingDomain: CreateListingDomain): MultipartResult<String> {
        return listingRepository.createListing(
            createListingData = createListingDomain.toData(
                mapListingDetails = listingDetailsResponseMapper::toResponse
            )
        )
    }

    override suspend fun updateListing(
        listingId: String,
        updateListingDomain: UpdateListingDomain
    ): Boolean {
        return listingRepository.updateListing(listingId, updateListingDomain.toRequest())
    }

    override suspend fun deleteListing(listingId: String): Boolean {
        return listingRepository.deleteListing(listingId)
    }

    override suspend fun addToFavorites(listingId: String): Boolean {
        return listingRepository.addToFavorites(listingId)
    }

    override suspend fun removeFromFavorites(listingId: String): Boolean {
        return listingRepository.removeFromFavorites(listingId)
    }

    override suspend fun getFavoriteListings(): InteractorPartialState<List<ListingPreviewDomain>> {
        return handleRepositoryResultNoFlow(
            repositoryResult = listingRepository.getFavoriteListings(),
            transformation = { this.map { it.toDomain() } }
        )
    }
}