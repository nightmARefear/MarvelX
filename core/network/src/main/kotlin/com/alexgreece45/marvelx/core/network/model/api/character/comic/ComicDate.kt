package com.alexgreece45.marvelx.core.network.model.api.character.comic

import com.google.gson.annotations.SerializedName
import java.time.OffsetDateTime

data class ComicDate(
    @SerializedName("type") val type: String,
    @SerializedName("date") val date: OffsetDateTime
)
