package com.alexgreece45.marvelx.data.domains

import android.os.Parcelable
import com.alexgreece45.marvelx.data.models.character.*
import com.alexgreece45.marvelx.util.extensions.toOffsetDateTime
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class CharacterDomain(
    val id: Int,
    val name: String,
    val description: String = "",
    val modified: LocalDateTime,
    val resourceURI: String,
    val urls: List<Url>,
    val thumbnail: Image,
    val comics: ComicList,
    val stories: StoryList,
    val events: EventList,
    val series: SeriesList
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

fun LocalDateTime.format(): String {
    return this.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy HH:mm")).toString()
}
