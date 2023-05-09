package com.alexgreece45.marvelx.data.models.character

import com.google.gson.annotations.SerializedName

data class SeriesSummary(
    @SerializedName("resourceURI") val resourceURI: String,
    @SerializedName("name") val name: String,
)
