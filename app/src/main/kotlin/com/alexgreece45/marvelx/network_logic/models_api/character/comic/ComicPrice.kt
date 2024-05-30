package com.alexgreece45.marvelx.network_logic.models_api.character.comic

import com.google.gson.annotations.SerializedName

data class ComicPrice(
    @SerializedName("type") val type: String,
    @SerializedName("price") val price: Float
)
