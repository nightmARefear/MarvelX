package com.alexgreece45.marvelx.data.models.character.comic

import com.alexgreece45.marvelx.data.domains.ComicDomain
import com.alexgreece45.marvelx.data.models.character.ComicSummary
import com.alexgreece45.marvelx.data.models.character.EventList
import com.alexgreece45.marvelx.data.models.character.Image
import com.alexgreece45.marvelx.data.models.character.SeriesSummary
import com.alexgreece45.marvelx.data.models.character.StoryList
import com.google.gson.annotations.SerializedName
import java.util.Date

data class ComicResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("digitalId") val digitalId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("issueNumber") val issueNumber: Double,
    @SerializedName("variantDescription") val variantDescription: String,
    @SerializedName("description") val description: String,
    @SerializedName("modified") val modified: Date,
    @SerializedName("isbn") val isbn: String,
    @SerializedName("upc") val upc: String,
    @SerializedName("diamondCode") val diamondCode: String,
    @SerializedName("ean") val ean: String,
    @SerializedName("issn") val issn: String,
    @SerializedName("format") val format: String,
    @SerializedName("pageCount") val pageCount: Int,
    @SerializedName("textObjects") val textObjects: List<TextObject>,
    @SerializedName("resourceURI") val resourceURI: String,
    @SerializedName("series") val series: SeriesSummary,
    @SerializedName("variants") val variants: List<ComicSummary>,
    @SerializedName("collections") val collections: List<ComicSummary>,
    @SerializedName("collectedIssues") val collectedIssues: List<ComicSummary>,
    @SerializedName("dates") val dates: List<ComicDate>,
    @SerializedName("prices") val prices: List<ComicPrice>,
    @SerializedName("thumbnail") val thumbnail: Image,
    @SerializedName("images") val images: List<Image>,
    @SerializedName("creators") val creators: CreatorList,
    @SerializedName("characters") val characters: CharacterList,
    @SerializedName("stories") val stories: StoryList,
    @SerializedName("events") val events: EventList,
)
fun ComicResponse.toDomain(): ComicDomain = ComicDomain(
    id = this.id,
    digitalId = this.digitalId,
    title = this.title,
    issueNumber = this.issueNumber,
    variantDescription = this.variantDescription,
    description = this.description,
    modified = this.modified,
    isbn = this.isbn,
    upc = this.upc,
    diamondCode = this.diamondCode,
    ean = this.ean,
    issn = this.issn,
    format = this.format,
    pageCount = this.pageCount,
    textObjects = this.textObjects.toList(),
    resourceURI = this.resourceURI,
    series = this.series,
    variants = this.variants.toList(),
    collections = this.collections.toList(),
    collectedIssues = this.collectedIssues.toList(),
    dates = this.dates.toList(),
    prices = this.prices.toList(),
    thumbnail = this.thumbnail,
    images = this.images.toList(),
    creators = this.creators,
    characters = this.characters,
    stories = this.stories,
    events = this.events
)

fun List<ComicResponse>.toDomain(): List<ComicDomain> =
    this.map {
        ComicDomain(
            id = it.id,
            digitalId = it.digitalId,
            title = it.title,
            issueNumber = it.issueNumber,
            variantDescription = it.variantDescription,
            description = it.description,
            modified = it.modified,
            isbn = it.isbn,
            upc = it.upc,
            diamondCode = it.diamondCode,
            ean = it.ean,
            issn = it.issn,
            format = it.format,
            pageCount = it.pageCount,
            textObjects = it.textObjects.toList(),
            resourceURI = it.resourceURI,
            series = it.series,
            variants = it.variants.toList(),
            collections = it.collections.toList(),
            collectedIssues = it.collectedIssues.toList(),
            dates = it.dates.toList(),
            prices = it.prices.toList(),
            thumbnail = it.thumbnail,
            images = it.images.toList(),
            creators = it.creators,
            characters = it.characters,
            stories = it.stories,
            events = it.events
        )
    }
