package com.alexgreece45.marvelx.di

import com.alexgreece45.marvelx.network_logic.clients.ListingClient
import com.alexgreece45.marvelx.network_logic.models_api.marketplace.ResponseMarketplace
import com.alexgreece45.marvelx.network_logic.models_api.marketplace.listing.RequestCreateListing
import com.alexgreece45.marvelx.network_logic.models_api.marketplace.listing.RequestUpdateListing
import com.alexgreece45.marvelx.network_logic.models_api.marketplace.listing.ResponseListing
import com.alexgreece45.marvelx.network_logic.models_api.marketplace.listing.ResponseListingCategory
import com.alexgreece45.marvelx.network_logic.models_api.marketplace.listing.ResponseListingEnumGeneric
import com.alexgreece45.marvelx.network_logic.models_api.marketplace.listing.ResponseListingId
import com.alexgreece45.marvelx.network_logic.models_api.marketplace.listing.ResponseListingPreview
import com.alexgreece45.marvelx.network_logic.models_generic.ApiResult
import com.alexgreece45.marvelx.network_logic.services.ListingService
import com.alexgreece45.marvelx.network_logic.util.executeApiServiceCall
import com.alexgreece45.marvelx.network_logic.util.executeApiServiceCallNoTransformation

class ListingClientImpl(
    private val apiService: ListingService
) : ListingClient {

    override suspend fun getListingCategories(): ApiResult<List<ResponseListingCategory>> {
        return executeApiServiceCall { apiService.getListingCategories() }
    }

    override suspend fun getListingStatuses(): ApiResult<List<ResponseListingEnumGeneric>> {
        return executeApiServiceCall { apiService.getListingStatuses() }
    }

    override suspend fun getListingConditions(): ApiResult<List<ResponseListingEnumGeneric>> {
        return executeApiServiceCall { apiService.getListingConditions() }
    }

    override suspend fun getListingApparelSizes(): ApiResult<List<ResponseListingEnumGeneric>> {
        return executeApiServiceCall { apiService.getListingApparelSizes() }
    }

    override suspend fun getListingMaterials(): ApiResult<List<ResponseListingEnumGeneric>> {
        return executeApiServiceCall { apiService.getListingMaterials() }
    }

    override suspend fun getListingCollectibleScales(): ApiResult<List<ResponseListingEnumGeneric>> {
        return executeApiServiceCall { apiService.getListingCollectibleScales() }
    }

    override suspend fun getListingCollectibleBoxConditions(): ApiResult<List<ResponseListingEnumGeneric>> {
        return executeApiServiceCall { apiService.getListingCollectibleScales() }
    }

    override suspend fun getListingGrades(): ApiResult<List<ResponseListingEnumGeneric>> {
        return executeApiServiceCall { apiService.getListingGrades() }
    }

    override suspend fun getListingCardRarities(): ApiResult<List<ResponseListingEnumGeneric>> {
        return executeApiServiceCall { apiService.getListingCardRarities() }
    }

    override suspend fun getListingCardVariants(): ApiResult<List<ResponseListingEnumGeneric>> {
        return executeApiServiceCall { apiService.getListingCardVariants() }
    }

    override suspend fun getMarketplaceInfo(): ApiResult<ResponseMarketplace> {
        return executeApiServiceCall {
            apiService.getMarketplaceScreen()
        }
    }

    override suspend fun getAllListingsByCategory(
        categoryCode: String
    ): ApiResult<List<ResponseListing>> {
        return executeApiServiceCall { apiService.getAllListingsByCategory(categoryCode) }
    }

    override suspend fun getUserListings(
        userId: String,
        page: Int
    ): ApiResult<List<ResponseListing>> {
        return executeApiServiceCall {
            apiService.getUserListings(userId, page)
        }
    }

    override suspend fun getListing(listingId: String): ApiResult<ResponseListing> {
        return executeApiServiceCall {
            apiService.getListing(listingId)
        }
    }

    override suspend fun createListing(
        request: RequestCreateListing
    ): ApiResult<ResponseListingId> {
        return executeApiServiceCall {
            apiService.createListing(request)
        }
    }

    override suspend fun updateListing(
        listingId: String,
        request: RequestUpdateListing
    ): ApiResult<String> {
        return executeApiServiceCallNoTransformation {
            apiService.updateListing(listingId, request)
        }
    }

    override suspend fun deleteListing(listingId: String): ApiResult<String> {
        return executeApiServiceCallNoTransformation {
            apiService.deleteListing(listingId)
        }
    }

    override suspend fun addToFavorites(listingId: String): ApiResult<String> {
        return executeApiServiceCallNoTransformation {
            apiService.addToFavorites(listingId)
        }
    }

    override suspend fun removeFromFavorites(listingId: String): ApiResult<String> {
        return executeApiServiceCallNoTransformation {
            apiService.removeFromFavorites(listingId)
        }
    }

    override suspend fun getFavoriteListings(): ApiResult<List<ResponseListingPreview>> {
        return executeApiServiceCall {
            apiService.getFavoriteListings()
        }
    }
}
