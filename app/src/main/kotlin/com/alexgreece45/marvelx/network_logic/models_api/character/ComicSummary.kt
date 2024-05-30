package com.alexgreece45.marvelx.network_logic.models_api.character

import com.google.gson.annotations.SerializedName

data class ComicSummary(
    @SerializedName("resourceURI") val resourceURI: String,
    @SerializedName("name") val name: String,
)
