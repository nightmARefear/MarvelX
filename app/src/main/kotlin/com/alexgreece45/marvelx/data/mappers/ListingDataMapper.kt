package com.alexgreece45.marvelx.data.mappers

import com.alexgreece45.marvelx.data.model.marketplace.listing.ListingCategoryCode
import com.alexgreece45.marvelx.data.model.marketplace.listing.ListingData
import com.alexgreece45.marvelx.network_logic.models_api.marketplace.listing.ResponseListing
import com.alexgreece45.marvelx.network_logic.models_api.marketplace.listing.details.ResponseListingDetails
import com.google.gson.Gson

class ListingDataMapper(
    private val gson: Gson
) {

    fun toData(responseListing: ResponseListing): ListingData {
        val responseListingDetails = responseListing.details
        val listingDetails = try {
            when (val responseListingCategoryCode = responseListing.categoryCode) {
                ListingCategoryCode.APPAREL.code -> gson.fromJson(
                    responseListingDetails,
                    ResponseListingDetails.ResponseListingApparelDetails::class.java
                )

                ListingCategoryCode.DRINKWARE.code -> gson.fromJson(
                    responseListingDetails,
                    ResponseListingDetails.ResponseListingDrinkwareDetails::class.java
                )

                ListingCategoryCode.COLLECTIBLES.code -> gson.fromJson(
                    responseListingDetails,
                    ResponseListingDetails.ResponseListingCollectibleDetails::class.java
                )

                ListingCategoryCode.ART.code -> gson.fromJson(
                    responseListingDetails,
                    ResponseListingDetails.ResponseListingArtDetails::class.java
                )

                ListingCategoryCode.ACCESSORIES.code -> gson.fromJson(
                    responseListingDetails,
                    ResponseListingDetails.ResponseListingAccessoryDetails::class.java
                )

                ListingCategoryCode.HOME.code -> gson.fromJson(
                    responseListingDetails,
                    ResponseListingDetails.ResponseListingHomeDetails::class.java
                )

                ListingCategoryCode.COMICS.code -> gson.fromJson(
                    responseListingDetails,
                    ResponseListingDetails.ResponseListingComicDetails::class.java
                )

                ListingCategoryCode.CARDS.code -> gson.fromJson(
                    responseListingDetails,
                    ResponseListingDetails.ResponseListingCardDetails::class.java
                )

                else -> throw IllegalArgumentException("Unknown category: $responseListingCategoryCode")
            }
        } catch (e: Exception) {
            throw IllegalStateException(
                "Failed to parse details for listing ${responseListing.listingId}",
                e
            )
        }

        return with(responseListing) {
            ListingData(
                listingId = listingId,
                sellerUserId = sellerUserId,
                categoryCode = categoryCode,
                subcategoryCode = subcategoryCode,
                conditionCode = conditionCode,
                statusCode = statusCode,
                title = title,
                description = description,
                price = price,
                coverImageUrl = coverImageUrl,
                imageUrls = imageUrls,
                details = listingDetails,
                favorite = favorite,
                postedAt = postedAt,
                updatedAt = updatedAt
            )
        }
    }
}
