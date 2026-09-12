package com.alexgreece45.marvelx.core.network.model.api.order

import com.google.gson.annotations.SerializedName

data class RequestPlaceOrder(
    @SerializedName("listingIds") val listingIds: List<String>,
    @SerializedName("checkoutDetails") val checkoutInfo: RequestPlaceOrderCheckoutDetails
)

data class RequestPlaceOrderCheckoutDetails(
    // Shipping details.
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("streetAddress") val streetAddress: String,
    @SerializedName("city") val city: String,
    @SerializedName("zipCode") val zipCode: String,
    @SerializedName("floorNumber") val floorNumber: String,
    @SerializedName("locationType") val locationType: String,

    // Payment details.
    @SerializedName("cardholderName") val cardholderName: String,
    @SerializedName("cardNumber") val cardNumber: String,
    @SerializedName("expirationDate") val expirationDate: String,
    @SerializedName("cvv") val cvv: String
)