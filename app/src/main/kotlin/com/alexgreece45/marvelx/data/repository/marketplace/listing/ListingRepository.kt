package com.alexgreece45.marvelx.data.repository.marketplace.listing

import com.alexgreece45.marvelx.data.mappers.ListingDataMapper
import com.alexgreece45.marvelx.data.model.marketplace.listing.ListingData
import com.alexgreece45.marvelx.data.model.marketplace.listing.ListingPreviewData
import com.alexgreece45.marvelx.data.model.marketplace.listing.toData
import com.alexgreece45.marvelx.data.model.myListing.MyListingData
import com.alexgreece45.marvelx.data.model.myListing.toData
import com.alexgreece45.marvelx.data.repository.helper.RepositoryResult
import com.alexgreece45.marvelx.data.repository.helper.handleApiResult
import com.alexgreece45.marvelx.data.repository.helper.handleApiResultNoTransformationWithData
import com.alexgreece45.marvelx.dispatchers.IoDispatcher
import com.alexgreece45.marvelx.network_logic.clients.ListingClient
import com.alexgreece45.marvelx.network_logic.models_api.marketplace.listing.RequestUpdateListing
import com.alexgreece45.marvelx.network_logic.models_api.marketplace.listing.ResponseListingId
import com.alexgreece45.marvelx.network_logic.models_api.marketplace.listing.ResponseListingPreview
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import kotlin.coroutines.CoroutineContext

interface ListingRepository {

    suspend fun getAllListingsByCategory(categoryCode: String): RepositoryResult<List<ListingData>>

    suspend fun getCurrentUserListings(page: Int): RepositoryResult<List<MyListingData>>

    suspend fun getUserListings(
        userId: String,
        page: Int
    ): RepositoryResult<List<ListingPreviewData>>

    suspend fun getListing(listingId: String): RepositoryResult<ListingData>
    suspend fun createListing(body: MultipartBody): RepositoryResult<String>

    suspend fun updateListing(
        listingId: String,
        request: RequestUpdateListing
    ): Boolean

    suspend fun deleteListing(listingId: String): Boolean

    suspend fun addToFavorites(listingId: String): Boolean
    suspend fun removeFromFavorites(listingId: String): Boolean

    suspend fun getFavoriteListings(): RepositoryResult<List<ResponseListingPreview>>
}

class ListingRepositoryImpl(
    private val listingClient: ListingClient,
    @IoDispatcher private val ioDispatcher: CoroutineContext,
    private val listingDataMapper: ListingDataMapper
) : ListingRepository {

    override suspend fun getAllListingsByCategory(
        categoryCode: String
    ): RepositoryResult<List<ListingData>> {
        return handleApiResult(
            apiResult = listingClient.getAllListingsByCategory(categoryCode),
            transformation = { this.map { listingDataMapper.toData(it) } },
            ioDispatcher = ioDispatcher
        ).first()
    }

    override suspend fun getCurrentUserListings(page: Int): RepositoryResult<List<MyListingData>> {
        return handleApiResult(
            apiResult = listingClient.getCurrentUserListings(page),
            transformation = { this.map { it.toData() } },
            ioDispatcher = ioDispatcher
        ).first()
    }

    override suspend fun getUserListings(
        userId: String,
        page: Int
    ): RepositoryResult<List<ListingPreviewData>> {
        return handleApiResult(
            apiResult = listingClient.getUserListings(userId, page),
            transformation = { this.map { it.toData() } },
            ioDispatcher = ioDispatcher
        ).first()
    }

    override suspend fun getListing(listingId: String): RepositoryResult<ListingData> {
        return handleApiResult(
            apiResult = listingClient.getListing(listingId),
            transformation = listingDataMapper::toData,
            ioDispatcher = ioDispatcher
        ).first()
    }

    override suspend fun createListing(body: MultipartBody): RepositoryResult<String> {
        return handleApiResult(
            apiResult = listingClient.createListing(
                body
            ),
            transformation = ResponseListingId::listingId,
            ioDispatcher = ioDispatcher
        ).first()
    }

    override suspend fun updateListing(
        listingId: String,
        request: RequestUpdateListing
    ): Boolean {
        return handleApiResultNoTransformationWithData(
            apiResult = listingClient.updateListing(listingId, request),
            ioDispatcher = ioDispatcher
        ).first() is RepositoryResult.Success
    }

    override suspend fun deleteListing(listingId: String): Boolean {
        return handleApiResultNoTransformationWithData(
            apiResult = listingClient.deleteListing(listingId),
            ioDispatcher = ioDispatcher
        ).first() is RepositoryResult.Success
    }

    override suspend fun addToFavorites(listingId: String): Boolean {
        return handleApiResultNoTransformationWithData(
            apiResult = listingClient.addToFavorites(listingId),
            ioDispatcher = ioDispatcher
        ).first() is RepositoryResult.Success
    }

    override suspend fun removeFromFavorites(listingId: String): Boolean {
        return handleApiResultNoTransformationWithData(
            apiResult = listingClient.removeFromFavorites(listingId),
            ioDispatcher = ioDispatcher
        ).first() is RepositoryResult.Success
    }

    override suspend fun getFavoriteListings(): RepositoryResult<List<ResponseListingPreview>> {
        return handleApiResultNoTransformationWithData(
            apiResult = listingClient.getFavoriteListings(),
            ioDispatcher = ioDispatcher
        ).first()
    }
}
