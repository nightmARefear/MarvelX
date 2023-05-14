package com.alexgreece45.marvelx.data.models.character

import com.alexgreece45.marvelx.data.domains.CharacterDomain
import com.alexgreece45.marvelx.util.extensions.toSystemLocalDateTime
import com.google.gson.annotations.SerializedName
import java.time.OffsetDateTime

data class CharacterResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String = "",
    @SerializedName("modified") val modified: OffsetDateTime,
    @SerializedName("resourceURI") val resourceURI: String,
    @SerializedName("urls") val urls: Array<Url>,
    @SerializedName("thumbnail") val thumbnail: Image,
    @SerializedName("comics") val comics: ComicList,
    @SerializedName("stories") val stories: StoryList,
    @SerializedName("events") val events: EventList,
    @SerializedName("series") val series: SeriesList
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CharacterResponse

        if (id != other.id) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (resourceURI != other.resourceURI) return false
        if (!urls.contentEquals(other.urls)) return false
        if (thumbnail != other.thumbnail) return false
        if (comics != other.comics) return false
        if (stories != other.stories) return false
        if (events != other.events) return false
        if (series != other.series) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + resourceURI.hashCode()
        result = 31 * result + urls.contentHashCode()
        result = 31 * result + thumbnail.hashCode()
        result = 31 * result + comics.hashCode()
        result = 31 * result + stories.hashCode()
        result = 31 * result + events.hashCode()
        result = 31 * result + series.hashCode()
        return result
    }
}

fun CharacterResponse.toDomain(): CharacterDomain =
    CharacterDomain(
        id = this.id,
        name = this.name,
        description = this.description,
        modified = this.modified.toSystemLocalDateTime(),
        resourceURI = this.resourceURI,
        urls = this.urls.toList(),
        thumbnail = this.thumbnail,
        comics = this.comics,
        stories = this.stories,
        events = this.events,
        series = this.series
    )

fun Array<CharacterResponse>.toDomain(): List<CharacterDomain> =
    this.map {
        CharacterDomain(
            id = it.id,
            name = it.name,
            modified = it.modified.toSystemLocalDateTime(),
            description = it.description,
            resourceURI = it.resourceURI,
            urls = it.urls.toList(),
            thumbnail = it.thumbnail,
            comics = it.comics,
            stories = it.stories,
            events = it.events,
            series = it.series
        )
    }
