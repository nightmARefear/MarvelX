package com.alexgreece45.marvelx.network_logic.models_api.marketplace.listing

import com.google.gson.annotations.SerializedName

data class RequestUpdateListing(
    @SerializedName("title") val title: String? = null,
    @SerializedName("description") val description: String? = null
)
