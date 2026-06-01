package com.alexgreece45.marvelx.core.network.model.api.character

import com.google.gson.annotations.SerializedName
import java.time.OffsetDateTime

data class CharacterResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String = "",
    @SerializedName("modified") val modified: OffsetDateTime,
    @SerializedName("resourceURI") val resourceURI: String,
    @SerializedName("urls") val urls: List<Url>,
    @SerializedName("thumbnail") val thumbnail: ImageResponse,
    @SerializedName("comics") val comics: ComicList,
    @SerializedName("stories") val stories: StoryList,
    @SerializedName("events") val events: EventList,
    @SerializedName("series") val series: SeriesList
)