package com.alexgreece45.marvelx.network_logic.models_api.character

import com.alexgreece45.marvelx.domain.models_api.character.CharacterDomain
import com.alexgreece45.marvelx.util.extensions.toSystemLocalDateTime
import com.google.gson.annotations.SerializedName
import java.time.OffsetDateTime

data class CharacterResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String = "",
    @SerializedName("modified") val modified: OffsetDateTime,
    @SerializedName("resourceURI") val resourceURI: String,
    @SerializedName("urls") val urls: List<com.alexgreece45.marvelx.network_logic.models_api.character.Url>,
    @SerializedName("thumbnail") val thumbnail: com.alexgreece45.marvelx.network_logic.models_api.character.ImageResponse,
    @SerializedName("comics") val comics: com.alexgreece45.marvelx.network_logic.models_api.character.ComicList,
    @SerializedName("stories") val stories: com.alexgreece45.marvelx.network_logic.models_api.character.StoryList,
    @SerializedName("events") val events: com.alexgreece45.marvelx.network_logic.models_api.character.EventList,
    @SerializedName("series") val series: com.alexgreece45.marvelx.network_logic.models_api.character.SeriesList
)

fun com.alexgreece45.marvelx.network_logic.models_api.character.CharacterResponse.toCharacterDomain(): CharacterDomain =
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

fun List<com.alexgreece45.marvelx.network_logic.models_api.character.CharacterResponse>.toCharacterDomainList(): List<CharacterDomain> =
    this.map { characterResponse -> characterResponse.toCharacterDomain() }
