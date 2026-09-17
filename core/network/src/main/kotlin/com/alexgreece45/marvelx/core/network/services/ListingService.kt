package com.alexgreece45.marvelx.core.network.services

import com.alexgreece45.marvelx.core.network.model.api.cart.ResponseCartInfo
import com.alexgreece45.marvelx.core.network.model.api.cart.ResponseCartSummary
import com.alexgreece45.marvelx.core.network.model.api.marketplace.ResponseMarketplace
import com.alexgreece45.marvelx.core.network.model.api.marketplace.listing.RequestUpdateListing
import com.alexgreece45.marvelx.core.network.model.api.marketplace.listing.ResponseListing
import com.alexgreece45.marvelx.core.network.model.api.marketplace.listing.ResponseListingCategory
import com.alexgreece45.marvelx.core.network.model.api.marketplace.listing.ResponseListingEnumGeneric
import com.alexgreece45.marvelx.core.network.model.api.marketplace.listing.ResponseListingId
import com.alexgreece45.marvelx.core.network.model.api.marketplace.listing.ResponseListingPreview
import com.alexgreece45.marvelx.core.network.model.api.myListing.ResponseMyListing
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

internal interface ListingService {

    @GET("/api/private/marvelx/listings/categories")
    suspend fun getListingCategories(): Response<List<ResponseListingCategory>>

    @GET("/api/private/marvelx/listing-details/statuses")
    suspend fun getListingStatuses(): Response<List<ResponseListingEnumGeneric>>

    @GET("/api/private/marvelx/listing-details/apparel-sizes")
    suspend fun getListingApparelSizes(): Response<List<ResponseListingEnumGeneric>>

    @GET("/api/private/marvelx/listing-details/collectible-scales")
    suspend fun getListingCollectibleScales(): Response<List<ResponseListingEnumGeneric>>

    @GET("/api/private/marvelx/listing-details/card-rarities")
    suspend fun getListingCardRarities(): Response<List<ResponseListingEnumGeneric>>

    @GET("/api/private/marvelx/listing-details/card-variants")
    suspend fun getListingCardVariants(): Response<List<ResponseListingEnumGeneric>>

    @GET("/api/private/marvelx/listing-details/conditions")
    suspend fun getListingConditions(): Response<List<ResponseListingEnumGeneric>>

    @GET("/api/private/marvelx/listing-details/grades")
    suspend fun getListingGrades(): Response<List<ResponseListingEnumGeneric>>

    @GET("/api/private/marvelx/listing-details/materials")
    suspend fun getListingMaterials(): Response<List<ResponseListingEnumGeneric>>

    @GET("/api/private/marvelx/marketplace")
    suspend fun getMarketplaceScreen(): Response<ResponseMarketplace>

    @GET("/api/private/marvelx/listings/category/{categoryCode}")
    suspend fun getAllListingsByCategory(
        @Path("categoryCode") categoryCode: String
    ): Response<List<ResponseListing>>

    @GET("/api/private/marvelx/user/me/listings")
    suspend fun getCurrentUserListings(
        @Query("page") page: Int
    ): Response<List<ResponseMyListing>>

    @GET("/api/private/marvelx/user/{userId}/listings")
    suspend fun getUserListings(
        @Path("userId") userId: String,
        @Query("page") page: Int
    ): Response<List<ResponseListingPreview>>

    @GET("/api/private/marvelx/listings/{listingId}")
    suspend fun getListing(
        @Path("listingId") listingId: String
    ): Response<ResponseListing>

    @GET("/api/private/marvelx/listings/search")
    suspend fun getFilteredListings(
        @Query("searchQuery") searchQuery: String?,
        @Query("categoryCodes") categoryCodes: List<String>?,
        @Query("conditionCodes") conditionCodes: List<String>?,
        @Query("minPrice") minPrice: String?,
        @Query("maxPrice") maxPrice: String?,
        @Query("datePostedKey") datePostedKey: String,
        @Query("customDateFrom") customDateFrom: String?,
        @Query("customDateTo") customDateTo: String?,
        @Query("finalSale") finalSale: Boolean,
        @Query("sortKey") sortKey: String
    ): Response<List<ResponseListing>>

    /**
     * Offset/limit keyed variant of [getFilteredListings], used by the Paging 3 search feed.
     *
     * Offset/limit (rather than a page index) is required because Paging 3 asks for a different
     * number of rows depending on the load: `PagingConfig.initialLoadSize` on the first load and
     * `pageSize` afterwards. A page-index API cannot express that, so rows get skipped or
     * duplicated at the boundaries.
     */
    @GET("/api/private/marvelx/listings/search")
    suspend fun getFilteredListingsPaged(
        @Query("searchQuery") searchQuery: String?,
        @Query("categoryCodes") categoryCodes: List<String>?,
        @Query("conditionCodes") conditionCodes: List<String>?,
        @Query("minPrice") minPrice: String?,
        @Query("maxPrice") maxPrice: String?,
        @Query("datePostedKey") datePostedKey: String,
        @Query("customDateFrom") customDateFrom: String?,
        @Query("customDateTo") customDateTo: String?,
        @Query("finalSale") finalSale: Boolean,
        @Query("sortKey") sortKey: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Response<List<ResponseListing>>

    @POST("/api/private/marvelx/listings")
    suspend fun createListing(
        @Body body: MultipartBody
    ): Response<ResponseListingId>

    @PATCH("/api/private/marvelx/listings/{listingId}")
    suspend fun updateListing(
        @Path("listingId") listingId: String,
        @Body request: RequestUpdateListing
    ): Response<Void>

    @DELETE("/api/private/marvelx/listings/{listingId}")
    suspend fun deleteListing(
        @Path("listingId") listingId: String
    ): Response<Void>

    @POST("/api/private/marvelx/listings/{listingId}/favorites")
    suspend fun addToFavorites(
        @Path("listingId") listingId: String
    ): Response<Void>

    @DELETE("/api/private/marvelx/listings/{listingId}/favorites")
    suspend fun removeFromFavorites(
        @Path("listingId") listingId: String
    ): Response<Void>

    @GET("/api/private/marvelx/listings/favorites")
    suspend fun getFavoriteListings(
    ): Response<List<ResponseListingPreview>>

    @POST("/api/private/marvelx/listings/{listingId}/cart")
    suspend fun addCartItem(
        @Path("listingId") listingId: String
    ): Response<Void>

    @DELETE("/api/private/marvelx/listings/{listingId}/cart")
    suspend fun deleteCartItem(
        @Path("listingId") listingId: String
    ): Response<Void>

    // Cart calls.

    @GET("/api/private/marvelx/cart/info")
    suspend fun getCartInfo(): Response<ResponseCartInfo>

    @GET("/api/private/marvelx/cart/items")
    suspend fun getCartItems(): Response<List<ResponseListingPreview>>

    @GET("/api/private/marvelx/cart/summary")
    suspend fun getCartSummary(): Response<ResponseCartSummary>
}