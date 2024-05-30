package com.alexgreece45.marvelx.network_logic.models_api.character.comic

import com.google.gson.annotations.SerializedName

data class ComicDataContainer(
    @SerializedName("offset") val offset: Int,
    @SerializedName("limit") val limit: Int,
    @SerializedName("total") val total: Int,
    @SerializedName("count") val count: Int,
    @SerializedName("results") val results: List<ComicResponse>,
)