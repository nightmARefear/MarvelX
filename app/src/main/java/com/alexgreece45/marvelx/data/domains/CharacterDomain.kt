package com.alexgreece45.marvelx.data.domains

import com.alexgreece45.marvelx.data.models.character.*
import com.alexgreece45.marvelx.util.extensions.toOffsetDateTime
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

data class CharacterDomain(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String? = "",
    @SerializedName("modified") val modified: LocalDateTime,
    @SerializedName("resourceURI") val resourceURI: String,
    @SerializedName("urls") val urls: List<Url>,
    @SerializedName("thumbnail") val thumbnail: Image,
    @SerializedName("comics") val comics: ComicList,
    @SerializedName("stories") val stories: StoryList,
    @SerializedName("events") val events: EventList,
    @SerializedName("series") val series: SeriesList
)

fun CharacterDomain.toResponse(): CharacterResponse {
    return CharacterResponse(
        id = this.id,
        name = this.name,
        description = this.description,
        modified = this.modified.toOffsetDateTime(),
        resourceURI = this.resourceURI,
        urls = this.urls.toTypedArray(),
        thumbnail = this.thumbnail,
        comics = this.comics,
        stories = this.stories,
        events = this.events,
        series = this.series
    )
}

fun LocalDateTime.format() : String {
    return this.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy HH:mm")).toString()
}
