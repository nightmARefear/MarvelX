package com.alexgreece45.marvelx.data.domains.character

import com.alexgreece45.marvelx.data.domains.image.ImageDomain
import com.alexgreece45.marvelx.data.domains.image.toResponse
import com.alexgreece45.marvelx.data.models.character.CharacterResponse
import com.alexgreece45.marvelx.data.models.character.ComicList
import com.alexgreece45.marvelx.data.models.character.EventList
import com.alexgreece45.marvelx.data.models.character.SeriesList
import com.alexgreece45.marvelx.data.models.character.StoryList
import com.alexgreece45.marvelx.data.models.character.Url
import com.alexgreece45.marvelx.data.uiModels.CharacterCardUi
import com.alexgreece45.marvelx.domain.database.FavoriteCharacterEntity
import com.alexgreece45.marvelx.util.extensions.toOffsetDateTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class CharacterDomain(
    val id: Int,
    val name: String = "",
    val description: String = "",
    val modified: LocalDateTime,
    val resourceURI: String,
    val urls: List<Url>,
    val thumbnail: ImageDomain,
    val comics: ComicList,
    val stories: StoryList,
    val events: EventList,
    val series: SeriesList,
    val isFavorite: Boolean = false
)

fun CharacterDomain.toResponse(): CharacterResponse {
    return CharacterResponse(
        id = this.id,
        name = this.name,
        description = this.description,
        modified = this.modified.toOffsetDateTime(),
        resourceURI = this.resourceURI,
        urls = this.urls,
        thumbnail = this.thumbnail.toResponse(),
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
        urls = character.urls,
        thumbnail = character.thumbnail.toResponse(),
        comics = character.comics,
        stories = character.stories,
        events = character.events,
        series = character.series
    )
}

fun CharacterDomain.toCharacterCardUi(): CharacterCardUi =
    CharacterCardUi(
        id = this.id,
        name = this.name,
        imageDownloadUrl = this.thumbnail.downloadUrl,
        imageContentDescription = this.thumbnail.contentDescription,
        isFavorite = this.isFavorite
    )

fun List<CharacterDomain>.toCharacterCardUiList(): List<CharacterCardUi> =
    map { characterDomain -> characterDomain.toCharacterCardUi() }

fun CharacterDomain.toFavoriteCharacterEntity(): FavoriteCharacterEntity =
    FavoriteCharacterEntity(
        characterId = this.id,
        name = this.name,
        imageDownloadUrl = this.thumbnail.downloadUrl,
        imageContentDescription = this.thumbnail.contentDescription,
        isFavorite = this.isFavorite
    )

fun LocalDateTime.format(): String {
    return this.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy HH:mm")).toString()
}
