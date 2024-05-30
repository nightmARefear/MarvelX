package com.alexgreece45.marvelx.network_logic.models_api.character

import com.google.gson.annotations.SerializedName

data class StorySummary(
    @SerializedName("resourceURI") val resourceURI : String,
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String // The type of the story (interior or cover).
)
