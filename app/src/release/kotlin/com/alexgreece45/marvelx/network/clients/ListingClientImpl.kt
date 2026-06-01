package com.alexgreece45.marvelx.network.clients

import com.alexgreece45.marvelx.network.models_api.cart.ResponseCartInfo
import com.alexgreece45.marvelx.network.models_api.cart.ResponseCartSummary
import com.alexgreece45.marvelx.network.models_api.marketplace.ResponseMarketplace
import com.alexgreece45.marvelx.network.models_api.marketplace.listing.RequestUpdateListing
import com.alexgreece45.marvelx.network.models_api.marketplace.listing.ResponseListing
import com.alexgreece45.marvelx.network.models_api.marketplace.listing.ResponseListingCategory
import com.alexgreece45.marvelx.network.models_api.marketplace.listing.ResponseListingEnumGeneric
import com.alexgreece45.marvelx.network.models_api.marketplace.listing.ResponseListingId
import com.alexgreece45.marvelx.network.models_api.marketplace.listing.ResponseListingPreview
import com.alexgreece45.marvelx.network.models_api.myListing.ResponseMyListing
import com.alexgreece45.marvelx.network.models_generic.ApiResult
import com.alexgreece45.marvelx.network.services.ListingService
import com.alexgreece45.marvelx.network.util.executeApiServiceCall
import com.alexgreece45.marvelx.network.util.executeApiServiceCallNoTransformation
import okhttp3.MultipartBody
import org.koin.core.annotation.Single

@Single
class ListingClientImpl(
    private val listingService: ListingService
) : ListingClient {

    override suspend fun getListingCategories(): ApiResult<List<ResponseListingCategory>> {
        return executeApiServiceCall { listingService.getListingCategories() }
    }

    override suspend fun getListingStatuses(): ApiResult<List<ResponseListingEnumGeneric>> {
        return executeApiServiceCall { listingService.getListingStatuses() }
    }

    override suspend fun getListingConditions(): ApiResult<List<ResponseListingEnumGeneric>> {
        return executeApiServiceCall { listingService.getListingConditions() }
    }

    override suspend fun getListingApparelSizes(): ApiResult<List<ResponseListingEnumGeneric>> {
        return executeApiServiceCall { listingService.getListingApparelSizes() }
    }

    override suspend fun getListingMaterials(): ApiResult<List<ResponseListingEnumGeneric>> {
        return executeApiServiceCall { listingService.getListingMaterials() }
    }

    override suspend fun getListingCollectibleScales(): ApiResult<List<ResponseListingEnumGeneric>> {
        return executeApiServiceCall { listingService.getListingCollectibleScales() }
    }

    override suspend fun getListingCollectibleBoxConditions(): ApiResult<List<ResponseListingEnumGeneric>> {
        return executeApiServiceCall { listingService.getListingCollectibleScales() }
    }

    override suspend fun getListingGrades(): ApiResult<List<ResponseListingEnumGeneric>> {
        return executeApiServiceCall { listingService.getListingGrades() }
    }

    override suspend fun getListingCardRarities(): ApiResult<List<ResponseListingEnumGeneric>> {
        return executeApiServiceCall { listingService.getListingCardRarities() }
    }

    override suspend fun getListingCardVariants(): ApiResult<List<ResponseListingEnumGeneric>> {
        return executeApiServiceCall { listingService.getListingCardVariants() }
    }

    override suspend fun getMarketplaceInfo(): ApiResult<ResponseMarketplace> {
        return executeApiServiceCall { listingService.getMarketplaceScreen() }
    }

    override suspend fun getAllListingsByCategory(
        categoryCode: String
    ): ApiResult<List<ResponseListing>> {
        return executeApiServiceCall { listingService.getAllListingsByCategory(categoryCode) }
    }

    override suspend fun getCurrentUserListings(page: Int): ApiResult<List<ResponseMyListing>> {
        return executeApiServiceCall {
            listingService.getCurrentUserListings(page)
        }
    }

    override suspend fun getUserListings(
        userId: String,
        page: Int
    ): ApiResult<List<ResponseListingPreview>> {
        return executeApiServiceCall {
            listingService.getUserListings(userId, page)
        }
    }

    override suspend fun getListing(listingId: String): ApiResult<ResponseListing> {
        return executeApiServiceCall {
            listingService.getListing(listingId)
        }
    }

    override suspend fun createListing(body: MultipartBody): ApiResult<ResponseListingId> {
        return executeApiServiceCall {
            listingService.createListing(body)
        }
    }

    override suspend fun updateListing(
        listingId: String,
        request: RequestUpdateListing
    ): ApiResult<String> {
        return executeApiServiceCallNoTransformation {
            listingService.updateListing(listingId, request)
        }
    }

    override suspend fun deleteListing(listingId: String): ApiResult<String> {
        return executeApiServiceCallNoTransformation {
            listingService.deleteListing(listingId)
        }
    }

    override suspend fun addToFavorites(listingId: String): ApiResult<String> {
        return executeApiServiceCallNoTransformation {
            listingService.addToFavorites(listingId)
        }
    }

    override suspend fun removeFromFavorites(listingId: String): ApiResult<String> {
        return executeApiServiceCallNoTransformation {
            listingService.removeFromFavorites(listingId)
        }
    }

    override suspend fun getFavoriteListings(): ApiResult<List<ResponseListingPreview>> {
        return executeApiServiceCall {
            listingService.getFavoriteListings()
        }
    }

    override suspend fun getCartInfo(): ApiResult<ResponseCartInfo> {
        return executeApiServiceCall {
            listingService.getCartInfo()
        }
    }

    override suspend fun getCartItems(): ApiResult<List<ResponseListingPreview>> {
        return executeApiServiceCall {
            listingService.getCartItems()
        }
    }

    override suspend fun getCartSummary(): ApiResult<ResponseCartSummary> {
        return executeApiServiceCall {
            listingService.getCartSummary()
        }
    }

    override suspend fun addCartItem(listingId: String): ApiResult<String> {
        return executeApiServiceCallNoTransformation {
            listingService.addCartItem(listingId)
        }
    }

    override suspend fun deleteCartItem(listingId: String): ApiResult<String> {
        return executeApiServiceCallNoTransformation {
            listingService.deleteCartItem(listingId)
        }
    }
}
