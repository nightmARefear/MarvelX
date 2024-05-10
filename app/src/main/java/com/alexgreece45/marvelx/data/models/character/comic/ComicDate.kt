package com.alexgreece45.marvelx.data.models.character.comic

import com.google.gson.annotations.SerializedName
import java.time.OffsetDateTime
import java.util.Date

data class ComicDate(
    @SerializedName("type") val type: String,
    @SerializedName("date") val date: OffsetDateTime
)
