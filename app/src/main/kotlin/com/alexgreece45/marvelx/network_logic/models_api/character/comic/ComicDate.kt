package com.alexgreece45.marvelx.network_logic.models_api.character.comic

import com.google.gson.annotations.SerializedName
import java.time.OffsetDateTime

data class ComicDate(
    @SerializedName("type") val type: String,
    @SerializedName("date") val date: OffsetDateTime
)
