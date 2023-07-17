package com.alexgreece45.marvelx.data.models.character.comic

data class CreatorList(
    val available: Int,
    val returned: Int,
    val collectionURI: String,
    val items: List<CreatorSummary>
)
