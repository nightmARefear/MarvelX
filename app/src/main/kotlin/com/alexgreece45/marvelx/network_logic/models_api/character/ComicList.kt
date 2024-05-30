package com.alexgreece45.marvelx.network_logic.models_api.character

import com.google.gson.annotations.SerializedName

data class ComicList(
    @SerializedName("available") val available: Int,
    @SerializedName("returned") val returned: Int,
    @SerializedName("collectionURI") val collectionURI: String,
    @SerializedName("items") val items: List<ComicSummary>,
)