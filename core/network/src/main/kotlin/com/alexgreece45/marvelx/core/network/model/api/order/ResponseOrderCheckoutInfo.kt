package com.alexgreece45.marvelx.core.network.model.api.order

import com.google.gson.annotations.SerializedName

data class ResponseOrderCheckoutInfo(
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("streetAddress") val streetAddress: String,
    @SerializedName("city") val city: String,
    @SerializedName("zipCode") val zipCode: String,
    @SerializedName("maskedCardNumber") val maskedCardNumber: String,
    @SerializedName("cardBrand") val cardBrand: String,
    @SerializedName("paymentDate") val paymentDate: String,
    @SerializedName("transactionId") val transactionId: String,
)
