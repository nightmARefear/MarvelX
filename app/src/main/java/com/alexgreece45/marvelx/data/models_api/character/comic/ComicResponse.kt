package com.alexgreece45.marvelx.data.models_api.character.comic

import com.alexgreece45.marvelx.domain.models_api.comic.ComicDomain
import com.alexgreece45.marvelx.domain.models_api.comic.ComicPriceDomain
import com.alexgreece45.marvelx.domain.models_api.comic.ComicPriceType
import com.alexgreece45.marvelx.data.models_api.character.ComicSummary
import com.alexgreece45.marvelx.data.models_api.character.EventList
import com.alexgreece45.marvelx.data.models_api.character.ImageResponse
import com.alexgreece45.marvelx.data.models_api.character.SeriesSummary
import com.alexgreece45.marvelx.data.models_api.character.StoryList
import com.alexgreece45.marvelx.data.models_api.character.toDomain
import com.alexgreece45.marvelx.util.extensions.toSystemLocalDateTime
import com.google.gson.annotations.SerializedName
import java.time.OffsetDateTime

data class ComicResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("digitalId") val digitalId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("issueNumber") val issueNumber: Double,
    @SerializedName("variantDescription") val variantDescription: String,
    @SerializedName("description") val description: String?,
    @SerializedName("modified") val modified: OffsetDateTime,
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
    @SerializedName("prices") val prices: List<ComicPrice>? = null,
    @SerializedName("thumbnail") val thumbnail: ImageResponse,
    @SerializedName("images") val images: List<ImageResponse>,
    @SerializedName("creators") val creators: CreatorList,
    @SerializedName("characters") val characters: CharacterList,
    @SerializedName("stories") val stories: StoryList,
    @SerializedName("events") val events: EventList,
)

fun ComicResponse.toComicDomain(): ComicDomain =
    ComicDomain(
        id = this.id,
        digitalId = this.digitalId,
        title = this.title,
        issueNumber = this.issueNumber,
        variantDescription = this.variantDescription,
        description = this.description.orEmpty(),
        modified = this.modified.toSystemLocalDateTime().toLocalDate(),
        isbn = this.isbn,
        upc = this.upc,
        diamondCode = this.diamondCode,
        ean = this.ean,
        issn = this.issn,
        format = this.format,
        pageCount = this.pageCount,
        textObjects = this.textObjects,
        resourceURI = this.resourceURI,
        series = this.series,
        variants = this.variants,
        collections = this.collections,
        collectedIssues = this.collectedIssues,
        dates = this.dates,
        prices = this.prices?.map { comicPrice ->
            ComicPriceDomain(
                type = ComicPriceType.getType(comicPrice.type),
                price = comicPrice.price
            )
        }.orEmpty(),
        thumbnail = this.thumbnail.toDomain(
            containerClassType = this::class
        ),
        images = this.images,
        creators = this.creators,
        characters = this.characters,
        stories = this.stories,
        events = this.events
    )

fun List<ComicResponse>.toComicDomainList(): List<ComicDomain> =
    this.map { comicResponse -> comicResponse.toComicDomain() }
