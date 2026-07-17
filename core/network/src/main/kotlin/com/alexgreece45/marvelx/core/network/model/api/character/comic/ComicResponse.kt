package com.alexgreece45.marvelx.core.network.model.api.character.comic

import com.alexgreece45.marvelx.core.network.model.api.character.ComicSummary
import com.alexgreece45.marvelx.core.network.model.api.character.EventList
import com.alexgreece45.marvelx.core.network.model.api.character.ImageResponse
import com.alexgreece45.marvelx.core.network.model.api.character.SeriesSummary
import com.alexgreece45.marvelx.core.network.model.api.character.StoryList
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