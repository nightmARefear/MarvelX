package com.alexgreece45.marvelx.network_logic.models_api.character.comic

import com.google.gson.annotations.SerializedName

data class TextObject (
    @SerializedName("type") val type: String,
    @SerializedName("language") val language: String,
    @SerializedName("text") val text: String
)
