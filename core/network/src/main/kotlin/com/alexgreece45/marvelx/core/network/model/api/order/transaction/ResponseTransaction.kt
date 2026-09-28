package com.alexgreece45.marvelx.core.network.model.api.order.transaction

import com.google.gson.annotations.SerializedName

/**
 * A single order item in the caller's purchase or sale history.
 * Backend contract: `docs/backend-specs/transactions.md`.
 */
data class ResponseTransaction(
    @SerializedName("orderId") val orderId: String,
    @SerializedName("orderItemId") val orderItemId: String,
    @SerializedName("listingId") val listingId: String,
    @SerializedName("title") val title: String,
    @SerializedName("coverImageUrl") val coverImageUrl: String,
    @SerializedName("price") val price: String,
    @SerializedName("date") val date: String,
    @SerializedName("status") val status: String,
    @SerializedName("returnStatus") val returnStatus: String? = null,
    @SerializedName("refundAmount") val refundAmount: String? = null,
    @SerializedName("counterpartyUserId") val counterpartyUserId: String
)
