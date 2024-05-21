package com.alexgreece45.marvelx.data.models_api.character

import com.google.gson.annotations.SerializedName

data class CharacterDataContainer(
    @SerializedName("offset") val offset: Int,
    @SerializedName("limit") val limit: Int,
    @SerializedName("total") val total: Int,
    @SerializedName("count") val count: Int,
    @SerializedName("results") val results: List<CharacterResponse>
)