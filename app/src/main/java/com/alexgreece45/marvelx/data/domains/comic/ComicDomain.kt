package com.alexgreece45.marvelx.data.domains.comic

import com.alexgreece45.marvelx.data.domains.image.ImageDomain
import com.alexgreece45.marvelx.data.models.character.ComicSummary
import com.alexgreece45.marvelx.data.models.character.EventList
import com.alexgreece45.marvelx.data.models.character.ImageResponse
import com.alexgreece45.marvelx.data.models.character.SeriesSummary
import com.alexgreece45.marvelx.data.models.character.StoryList
import com.alexgreece45.marvelx.data.models.character.comic.CharacterList
import com.alexgreece45.marvelx.data.models.character.comic.ComicDate
import com.alexgreece45.marvelx.data.models.character.comic.CreatorList
import com.alexgreece45.marvelx.data.models.character.comic.TextObject
import java.util.Date

data class ComicDomain(
    val id: Int,
    val digitalId: Int,
    val title: String,
    val issueNumber: Double,
    val variantDescription: String,
    val description: String,
    val modified: Date,
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

sealed class ComicPriceType(open val name: String = "", open val formattedName: String = "") {
    object Print : ComicPriceType(name = "printPrice", formattedName = "Price")
    object DigitalPurchase : ComicPriceType(name = "digitalPurchasePrice", formattedName = "Digital Purchase")
    data class Unknown(override val name: String, override val formattedName: String) : ComicPriceType()

    companion object {
        fun asList(): List<ComicPriceType> = listOf(Print, DigitalPurchase)

        fun getType(name: String): ComicPriceType =
            asList().firstOrNull { it.name == name } ?: Unknown(name, name)
    }
}

data class ComicPriceDomain(
    val type: ComicPriceType,
    val price: Float
)


