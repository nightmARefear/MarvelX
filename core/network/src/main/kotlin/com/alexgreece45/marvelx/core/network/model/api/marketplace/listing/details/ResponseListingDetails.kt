package com.alexgreece45.marvelx.core.network.model.api.marketplace.listing.details

import com.google.gson.annotations.SerializedName


sealed interface ResponseListingDetails {

    data class ResponseListingApparelDetails(
        @SerializedName("sizeCode") val sizeCode: String,
        @SerializedName("color") val color: String
    ) : ResponseListingDetails

    data class ResponseListingDrinkwareDetails(
        @SerializedName("materialCode") val materialCode: String,
        @SerializedName("capacityMl") val capacityMl: Int,
        @SerializedName("dishwasherSafe") val dishwasherSafe: Boolean
    ) : ResponseListingDetails

    data class ResponseListingCollectibleDetails(
        @SerializedName("scaleCode") val scaleCode: String,
        @SerializedName("boxConditionCode") val boxConditionCode: String
    ) : ResponseListingDetails

    data class ResponseListingArtDetails(
        @SerializedName("width") val width: Int,
        @SerializedName("height") val height: Int,
        @SerializedName("framed") val framed: Boolean
    ) : ResponseListingDetails

    data class ResponseListingAccessoryDetails(
        @SerializedName("materialCode") val materialCode: String,
        @SerializedName("color") val color: String
    ) : ResponseListingDetails

    data class ResponseListingHomeDetails(
        @SerializedName("width") val width: Int,
        @SerializedName("height") val height: Int
    ) : ResponseListingDetails

    data class ResponseListingComicDetails(
        @SerializedName("gradeCode") val gradeCode: String,
        @SerializedName("publisher") val publisher: String,
        @SerializedName("year") val year: Int
    ) : ResponseListingDetails

    data class ResponseListingCardDetails(
        @SerializedName("gradeCode") val gradeCode: String,
        @SerializedName("rarityCode") val rarityCode: String,
        @SerializedName("variantCode") val variantCode: String
    ) : ResponseListingDetails
}