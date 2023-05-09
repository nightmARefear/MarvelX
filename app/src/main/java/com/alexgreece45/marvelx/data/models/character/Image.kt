package com.alexgreece45.marvelx.data.models.character

import com.google.gson.annotations.SerializedName

data class Image(
    @SerializedName("path") val path : String,
    @SerializedName("extension") val extension : String
)
