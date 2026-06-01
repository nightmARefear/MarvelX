package com.alexgreece45.marvelx.core.network.model.api.character

import com.google.gson.annotations.SerializedName

data class ImageResponse(
    @SerializedName("path") val path: String,
    @SerializedName("extension") val extension: String
)