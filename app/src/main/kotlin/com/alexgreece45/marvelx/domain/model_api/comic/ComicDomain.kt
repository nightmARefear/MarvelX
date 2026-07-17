package com.alexgreece45.marvelx.domain.model_api.comic

import com.alexgreece45.marvelx.core.network.model.api.character.ComicSummary
import com.alexgreece45.marvelx.core.network.model.api.character.EventList
import com.alexgreece45.marvelx.core.network.model.api.character.ImageResponse
import com.alexgreece45.marvelx.core.network.model.api.character.SeriesSummary
import com.alexgreece45.marvelx.core.network.model.api.character.StoryList
import com.alexgreece45.marvelx.core.network.model.api.character.comic.CharacterList
import com.alexgreece45.marvelx.core.network.model.api.character.comic.ComicDate
import com.alexgreece45.marvelx.core.network.model.api.character.comic.ComicResponse
import com.alexgreece45.marvelx.core.network.model.api.character.comic.CreatorList
import com.alexgreece45.marvelx.core.network.model.api.character.comic.TextObject
import com.alexgreece45.marvelx.domain.model_api.image.ImageDomain
import com.alexgreece45.marvelx.domain.model_api.image.toDomain
import com.alexgreece45.marvelx.util.extensions.toSystemLocalDateTime
import java.time.LocalDate

data class ComicDomain(
    val id: Int,
    val digitalId: Int,
    val title: String,
    val issueNumber: Double,
    val variantDescription: String,
    val description: String,
    val modified: LocalDate,
    val isbn: String,
    val upc: String,
    val diamondCode: String,
    val ean: String,
    val issn: String,
    val format: String,
    val pageCount: Int,
    val textObjects: List<TextObject>,
    val resourceURI: String,
    val series: SeriesSummary,
    val variants: List<ComicSummary>,
    val collections: List<ComicSummary>,
    val collectedIssues: List<ComicSummary>,
    val dates: List<ComicDate>,
    val prices: List<ComicPriceDomain>,
    val thumbnail: ImageDomain,
    val images: List<ImageResponse>,
    val creators: CreatorList,
    val characters: CharacterList,
    val stories: StoryList,
    val events: EventList,
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

sealed class ComicPriceType(open val name: String = "", open val formattedName: String = "") {
    data object Print : ComicPriceType(name = "printPrice", formattedName = "Price")
    data object DigitalPurchase :
        ComicPriceType(name = "digitalPurchasePrice", formattedName = "Digital Purchase")

    data class Unknown(override val name: String, override val formattedName: String) :
        ComicPriceType()

    companion object {
        private fun asList(): List<ComicPriceType> = listOf(Print, DigitalPurchase)

        fun getType(name: String): ComicPriceType =
            asList().firstOrNull { it.name == name } ?: Unknown(name, name)
    }
}

data class ComicPriceDomain(
    val type: ComicPriceType,
    val price: Float
)