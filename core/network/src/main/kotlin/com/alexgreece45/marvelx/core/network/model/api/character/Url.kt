package com.alexgreece45.marvelx.core.network.model.api.character

import com.google.gson.annotations.SerializedName

data class Url(
    @SerializedName("type") val type: String,
    @SerializedName("url") val url: String
)
