package com.alexgreece45.marvelx.core.network.model.api.myListing

import com.google.gson.annotations.SerializedName

data class ResponseMyListing(
    @SerializedName("listingId") val listingId: String,
    @SerializedName("title") val title: String,
    @SerializedName("coverImageUrl") val coverImageUrl: String,
    @SerializedName("statusCode") val statusCode: String,
)
