package com.alexgreece45.marvelx.data.domains

import com.alexgreece45.marvelx.data.models.character.CharacterResponse
import com.alexgreece45.marvelx.data.models.character.ComicList
import com.alexgreece45.marvelx.data.models.character.EventList
import com.alexgreece45.marvelx.data.models.character.Image
import com.alexgreece45.marvelx.data.models.character.SeriesList
import com.alexgreece45.marvelx.data.models.character.StoryList
import com.alexgreece45.marvelx.data.models.character.Url
import com.alexgreece45.marvelx.util.extensions.toOffsetDateTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

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
    val series: SeriesList,
    val isFavorite: Boolean = Random.nextInt(1, 3) == 2
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

fun List<CharacterDomain>.toResponse(): List<CharacterResponse> = this.map { character ->
    CharacterResponse(
        id = character.id,
        name = character.name,
        description = character.description,
        modified = character.modified.toOffsetDateTime(),
        resourceURI = character.resourceURI,
        urls = character.urls.toTypedArray(),
        thumbnail = character.thumbnail,
        comics = character.comics,
        stories = character.stories,
        events = character.events,
        series = character.series
    )
}

fun LocalDateTime.format(): String {
    return this.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy HH:mm")).toString()
}
