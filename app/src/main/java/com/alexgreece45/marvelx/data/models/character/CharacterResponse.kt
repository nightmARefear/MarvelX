package com.alexgreece45.marvelx.data.models.character

import com.alexgreece45.marvelx.data.domains.character.CharacterDomain
import com.alexgreece45.marvelx.util.extensions.toSystemLocalDateTime
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

fun CharacterResponse.toDomain(): CharacterDomain =
    CharacterDomain(
        id = this.id,
        name = this.name,
        description = this.description,
        modified = this.modified.toSystemLocalDateTime(),
        resourceURI = this.resourceURI,
        urls = this.urls,
        thumbnail = this.thumbnail.toDomain(
            containerClassType = this::class,
            characterName = this.name
        ),
        comics = this.comics,
        stories = this.stories,
        events = this.events,
        series = this.series
    )

fun List<CharacterResponse>.toDomain(): List<CharacterDomain> =
    this.map {
        CharacterDomain(
            id = it.id,
            name = it.name,
            modified = it.modified.toSystemLocalDateTime(),
            description = it.description,
            resourceURI = it.resourceURI,
            urls = it.urls,
            thumbnail = it.thumbnail.toDomain(
                containerClassType = it::class,
                characterName = it.name
            ),
            comics = it.comics,
            stories = it.stories,
            events = it.events,
            series = it.series
        )
    }
