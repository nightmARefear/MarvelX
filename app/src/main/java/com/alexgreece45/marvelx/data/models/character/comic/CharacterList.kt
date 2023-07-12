package com.alexgreece45.marvelx.data.models.character.comic

data class CharacterList(
    val available: Int,
    val returned: Int,
    val collectionURI: String,
    val items: Array<CharacterSummary>
)
