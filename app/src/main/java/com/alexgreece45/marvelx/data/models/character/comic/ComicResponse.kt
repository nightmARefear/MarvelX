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
    @SerializedName("textObjects") val textObjects: Array<TextObject>,
    @SerializedName("resourceURI") val resourceURI: String,
    @SerializedName("series") val series: SeriesSummary,
    @SerializedName("variants") val variants: Array<ComicSummary>,
    @SerializedName("collections") val collections: Array<ComicSummary>,
    @SerializedName("collectedIssues") val collectedIssues: Array<ComicSummary>,
    @SerializedName("dates") val dates: Array<ComicDate>,
    @SerializedName("prices") val prices: Array<ComicPrice>,
    @SerializedName("thumbnail") val thumbnail: Image,
    @SerializedName("images") val images: Array<Image>,
    @SerializedName("creators") val creators: CreatorList,
    @SerializedName("characters") val characters: CharacterList,
    @SerializedName("stories") val stories: StoryList,
    @SerializedName("events") val events: EventList,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ComicResponse

        if (id != other.id) return false
        if (digitalId != other.digitalId) return false
        if (title != other.title) return false
        if (issueNumber != other.issueNumber) return false
        if (variantDescription != other.variantDescription) return false
        if (description != other.description) return false
        if (modified != other.modified) return false
        if (isbn != other.isbn) return false
        if (upc != other.upc) return false
        if (diamondCode != other.diamondCode) return false
        if (ean != other.ean) return false
        if (issn != other.issn) return false
        if (format != other.format) return false
        if (pageCount != other.pageCount) return false
        if (!textObjects.contentEquals(other.textObjects)) return false
        if (resourceURI != other.resourceURI) return false
        if (series != other.series) return false
        if (!variants.contentEquals(other.variants)) return false
        if (!collections.contentEquals(other.collections)) return false
        if (!collectedIssues.contentEquals(other.collectedIssues)) return false
        if (!dates.contentEquals(other.dates)) return false
        if (!prices.contentEquals(other.prices)) return false
        if (thumbnail != other.thumbnail) return false
        if (!images.contentEquals(other.images)) return false
        if (creators != other.creators) return false
        if (characters != other.characters) return false
        if (stories != other.stories) return false
        if (events != other.events) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + digitalId
        result = 31 * result + title.hashCode()
        result = 31 * result + issueNumber.hashCode()
        result = 31 * result + modified.hashCode()
        result = 31 * result + isbn.hashCode()
        result = 31 * result + upc.hashCode()
        result = 31 * result + diamondCode.hashCode()
        result = 31 * result + ean.hashCode()
        result = 31 * result + issn.hashCode()
        result = 31 * result + format.hashCode()
        result = 31 * result + pageCount
        result = 31 * result + textObjects.contentHashCode()
        result = 31 * result + resourceURI.hashCode()
        result = 31 * result + series.hashCode()
        result = 31 * result + variants.contentHashCode()
        result = 31 * result + collections.contentHashCode()
        result = 31 * result + collectedIssues.contentHashCode()
        result = 31 * result + dates.contentHashCode()
        result = 31 * result + prices.contentHashCode()
        result = 31 * result + thumbnail.hashCode()
        result = 31 * result + images.contentHashCode()
        result = 31 * result + creators.hashCode()
        result = 31 * result + characters.hashCode()
        result = 31 * result + stories.hashCode()
        result = 31 * result + events.hashCode()
        return result
    }
}

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

fun Array<ComicResponse>.toDomain(): List<ComicDomain> =
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
