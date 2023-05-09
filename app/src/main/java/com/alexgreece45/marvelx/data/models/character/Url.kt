package com.alexgreece45.marvelx.data.models.character

import com.google.gson.annotations.SerializedName

data class Url(
    @SerializedName("type") val type: String,
    @SerializedName("url") val url: String
)
