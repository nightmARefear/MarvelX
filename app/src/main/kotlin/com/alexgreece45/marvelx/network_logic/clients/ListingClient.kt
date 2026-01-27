package com.alexgreece45.marvelx.network_logic.clients

import com.alexgreece45.marvelx.network_logic.models_api.marketplace.ResponseMarketplace
import com.alexgreece45.marvelx.network_logic.models_api.marketplace.listing.RequestCreateListing
import com.alexgreece45.marvelx.network_logic.models_api.marketplace.listing.RequestUpdateListing
import com.alexgreece45.marvelx.network_logic.models_api.marketplace.listing.ResponseListing
import com.alexgreece45.marvelx.network_logic.models_api.marketplace.listing.ResponseListingCategory
import com.alexgreece45.marvelx.network_logic.models_api.marketplace.listing.ResponseListingEnumGeneric
import com.alexgreece45.marvelx.network_logic.models_api.marketplace.listing.ResponseListingId
import com.alexgreece45.marvelx.network_logic.models_api.marketplace.listing.ResponseListingPreview
import com.alexgreece45.marvelx.network_logic.models_generic.ApiResult

interface ListingClient {

    suspend fun getListingCategories(): ApiResult<List<ResponseListingCategory>>

    suspend fun getListingStatuses(): ApiResult<List<ResponseListingEnumGeneric>>

    suspend fun getListingConditions(): ApiResult<List<ResponseListingEnumGeneric>>

    suspend fun getListingApparelSizes(): ApiResult<List<ResponseListingEnumGeneric>>

    suspend fun getListingMaterials(): ApiResult<List<ResponseListingEnumGeneric>>

    suspend fun getListingCollectibleScales(): ApiResult<List<ResponseListingEnumGeneric>>

    suspend fun getListingCollectibleBoxConditions(): ApiResult<List<ResponseListingEnumGeneric>>

    suspend fun getListingGrades(): ApiResult<List<ResponseListingEnumGeneric>>

    suspend fun getListingCardRarities(): ApiResult<List<ResponseListingEnumGeneric>>

    suspend fun getListingCardVariants(): ApiResult<List<ResponseListingEnumGeneric>>

    suspend fun getMarketplaceInfo(): ApiResult<ResponseMarketplace>

    suspend fun getAllListingsByCategory(
        categoryCode: String
    ): ApiResult<List<ResponseListing>>

    suspend fun getUserListings(userId: String, page: Int): ApiResult<List<ResponseListing>>

    suspend fun getListing(listingId: String): ApiResult<ResponseListing>
    suspend fun createListing(request: RequestCreateListing): ApiResult<ResponseListingId>
    suspend fun updateListing(
        listingId: String,
        request: RequestUpdateListing
    ): ApiResult<String>
    suspend fun deleteListing(listingId: String): ApiResult<String>

    suspend fun addToFavorites(listingId: String): ApiResult<String>
    suspend fun removeFromFavorites(listingId: String): ApiResult<String>
    suspend fun getFavoriteListings(): ApiResult<List<ResponseListingPreview>>
}
